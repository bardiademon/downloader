package com.bardiademon.Downloder;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

@bardiademon
public final class Download extends Thread
{
    private static final int PROGRESS_SHOW = 5; // Progress / PROGRESS_SHOW;
    private static final String VIEW_PROGRESS = "█";
    private static final String START_SHOW_PROGRESS = "[";
    private static final String END_SHOW_PROGRESS = "]";
    private BufferedReader readerLink;
    private int sec, min, h;
    private boolean download;

    private String argLink, argLocationSave;
    private boolean argMkDir;
    private boolean argLastLocIsFile;
    private final boolean downloadQuestion;
    private final boolean manuallyEnterTheAddress;

    private String link;

    private Timer timer;

    private File fileSave;
    private long downloadedSize = 0;
    private boolean pause = false;
    private boolean stopDownload = false;
    private boolean forPause = false;
    private FileOutputStream fileOutputStream;
    private InputStream inputStream;
    private HttpURLConnection connection;

    private static final String EXIT = ":exit";

    Download (final String Link , final String LocationSave , final boolean MkDir , final boolean LastLocIsFile , final boolean DownloadQuestion , final boolean ManuallyEnterTheAddress)
    {
        argLink = Link;
        argLocationSave = LocationSave;
        argMkDir = MkDir;
        argLastLocIsFile = LastLocIsFile;
        downloadQuestion = DownloadQuestion;
        manuallyEnterTheAddress = ManuallyEnterTheAddress;
        start ();
    }

    @Override
    public void run ()
    {
        runClass ();
    }

    private void runClass ()
    {
        stopDownload = false;
        pause = false;
        while (true)
        {
            try
            {
                String link = getLink ();
                if (link.equals ("exit"))
                    break;
                else if (link.equals ("list"))
                    listDownload ();
                else
                {
                    boolean okArgLocationSave;
                    if ((okArgLocationSave = isOkArgLocationSave ()))
                        print ("File saving path: " + argLocationSave + "\n");
                    download = true;
                    time ();

                    if (okArgLocationSave) download (link , true , new File (argLocationSave));
                    else download (link , true , null);

                    download = false;
                    print (" " + getTime () + " | " + okTime () + "\n\n");
                }
            }
            catch (final Exception e)
            {
                print ("\nDownload error => " + e.getMessage () + "\n");
            }
        }
    }

    private String getLink () throws IOException
    {
        print ("Link: ");
        if (isOkArgLink ())
        {
            print (argLink + "\n");
            return argLink;
        }
        else
        {
            readerLink = new BufferedReader (new InputStreamReader (System.in));
            return readerLink.readLine ();
        }
    }

    private boolean isOkArgLink ()
    {
        return (argLink != null && !argLink.isEmpty ());
    }

    private boolean isOkArgLocationSave ()
    {
        if (argLocationSave != null && !argLocationSave.isEmpty ())
            return (pathValidation (new File (argLocationSave) , "") != null);

        return false;
    }

    private File pathValidation (final File file , final String nameTypeFile)
    {
        final boolean enterFilename = !(FilenameUtils.getExtension (file.getName ())).isEmpty ();

        if ((enterFilename && file.getParentFile ().exists ()) || (!enterFilename && file.exists ()))
        {
            if (enterFilename) return file;
            else return (new File (file.getPath () + File.separator + nameTypeFile));
        }
        else
        {
            if (!enterFilename && argLastLocIsFile && file.getParentFile ().exists ()) return file;

                /*
                 * (argMkDir && ((enterFilename && file.getParentFile ().mkdirs ()) || (!enterFilename && ((argLastLocIsFile && file.getParentFile ().mkdirs ()) || (!argLastLocIsFile && file.mkdirs ())))) && file.exists ())
                 * in if => agar if bala nabod inja aval mige agar gofte bod karbar dir ro besaz , besazesh ama =>
                 *
                 * aval agar toye path vared shode .type bod parent file ro begir besaz agar in nashod va .type nabod va akharin path file bode yani .type nadare in file parent ro begir dir ro besaz
                 * agar file nist ke adi besaz yani parent ro nemikhad begiri
                 */
            else if (argMkDir && ((enterFilename && file.getParentFile ().mkdirs ()) || (!enterFilename && ((argLastLocIsFile && file.getParentFile ().mkdirs ()) || (!argLastLocIsFile && file.mkdirs ())))))
            {
                if (enterFilename || argLastLocIsFile) return file;
                else return (new File (file.getPath () + File.separator + nameTypeFile));
            }

            else print ("Invalid path!\n");
        }

        return null;
    }

    @bardiademon
    private void listDownload () throws Exception
    {
        List <String> links = new ArrayList <> ();
        String link;
        int counter = 1;
        print ("Link " + (counter++) + ": ");
        while (!(link = readerLink.readLine ()).equals ("flush"))
        {
            links.add (link);
            print ("Link " + (counter++) + ": ");
        }
        List <String> listName = new ArrayList <> ();
        print ("\n");
        for (String oneLink : links)
        {
            URL url = new URL (oneLink);
            String name = FilenameUtils.getName (url.getPath ());
            listName.add (name);
            print (name + ": " + GetSize.Get (Long.parseLong (url.openConnection ().getHeaderField ("Content-length"))) + "\n");
        }
        print ("\n");
        print ("Start Download " + links.size () + " Files (y/n): ");
        if (readerLink.readLine ().equalsIgnoreCase ("y"))
        {
            final File fileSave = getLocation (null , true);
            if (fileSave != null)
            {
                print ("\n");
                String oneLink, name;
                for (int i = 0; i < links.size (); i++)
                {
                    oneLink = links.get (i);
                    name = listName.get (i);
                    print ("=================================\n");
                    print ("Start download " + name + " || " + getTime () + "\n");

                    download = true;
                    time ();

                    download (oneLink , false , new File (fileSave.getPath () + File.separator + name));
                    print (" " + getTime () + " | " + okTime () + "\n");
                    print ("=================================\n\n");

                    download = false;
                }
                Desktop.getDesktop ().open (fileSave.getParentFile ());
                runClass ();
            }
            else
                throw new IOException ("Cancel download!");
        }
        else throw new IOException ("Error select path save file");
    }

    @bardiademon
    private String getTime ()
    {
        ConvertTime convertTime = new ConvertTime (String.valueOf (System.currentTimeMillis () / 1000));
        return String.format ("%s:%s:%s" , convertTime.hour24 () , convertTime.minutes () , convertTime.second ());
    }

    @bardiademon
    private String progress (final int progress)
    {
        int progressForShow = (progress / PROGRESS_SHOW);
        StringBuilder finalProgress;
        finalProgress = new StringBuilder ();
        finalProgress.append (START_SHOW_PROGRESS);
        for (int i = 0; i < progressForShow; i++) finalProgress.append (VIEW_PROGRESS);
        int spaceToFinal = Math.abs ((100 / PROGRESS_SHOW) - progressForShow);
        for (int i = 0; i < spaceToFinal - 1; i++)
            finalProgress.append (" ");
        finalProgress.append (END_SHOW_PROGRESS);
        finalProgress.append (String.format (" %d%%" , progress));
        return finalProgress.toString ();
    }

    @bardiademon
    private void time ()
    {
        sec = 0;
        min = 0;
        h = 0;
        (timer = new Timer ()).schedule (new TimerTask ()
        {
            @Override
            public void run ()
            {
                sec++;
                if (sec > 59)
                {
                    min++;
                    sec = 0;
                }
                if (min > 59)
                {
                    h++;
                    min = 0;
                    sec = 0;
                }

                if (!download) timer.cancel ();
            }
        } , 1000 , 1000);
    }

    @bardiademon
    private String okTime ()
    {
        return String.format ("%s:%s:%s" , zeroPlus (h) , zeroPlus (min) , zeroPlus (sec));
    }

    private String zeroPlus (final int num)
    {
        return ((num < 10) ? "0" + num : String.valueOf (num));
    }

    @bardiademon
    private void download (final String link , final boolean question , File fileSave) throws Exception
    {
        this.link = link;

        print ("Connecting...");

        final URL url = new URL (link);

        connection = (HttpURLConnection) url.openConnection ();

        if (downloadedSize > 0)
        {
            print (String.format ("\rContinue downloading from: %s\n" , GetSize.Get (downloadedSize)));
            connection.setRequestProperty ("Range" , "bytes=" + downloadedSize + "-");
        }

        connection.connect ();

        long filesize = Long.parseLong (connection.getHeaderField ("Content-length"));

        if (downloadedSize > 0) filesize += downloadedSize;

        print (String.format ("\rFile size : %s\n" , GetSize.Get (filesize)));

        boolean download;

        final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
        if (!downloadQuestion && question && (downloadedSize == 0))
        {
            print ("Download this file (y,n)? ");
            download = reader.readLine ().equalsIgnoreCase ("y");
        }
        else download = true;

        if (download)
        {
            String filename = FilenameUtils.getName (link);

            if (FilenameUtils.getExtension (filename).isEmpty ())
            {
                final String contentDisposition = connection.getHeaderField ("Content-Disposition");
                final String filenameEquals = "filename=";
                filename = contentDisposition.substring (contentDisposition.indexOf (filenameEquals) + filenameEquals.length ());

                if (filename.isEmpty ()) filename = FilenameUtils.getName (link);
            }

            print (filename + "\n");

            // agar download resume nashode bashe
            if (downloadedSize == 0)
            {
                if (fileSave == null)
                {
                    if (!manuallyEnterTheAddress) print ("File saving path: ");
                    fileSave = getLocation (filename , false);
                }
                else if (fileSave.isDirectory ())
                    fileSave = new File (fileSave + File.separator + filename);
            }
            else print ("File saving path: " + ((fileSave == null) ? this.fileSave : fileSave));

            if (fileSave != null)
            {
                // barasi in ke file vojod dare ya na , agar dare download na tamom ast ya na

                if (fileSave.exists () && downloadedSize == 0)
                {
                    final boolean fullNotDownloaded = (filesize > fileSave.length ());
                    print (String.format ("\nThis file<%s> is exists.\n" , filename));

                    if (fullNotDownloaded)
                    {
                        print ("Full not downloaded\n");
                        print ("1.Resume\n");
                    }

                    print ("2.Delete previous file and download again\n");
                    print ("3.Rename download file\n");
                    print ("4.Cancel\n");

                    final BufferedReader numRead = new BufferedReader (new InputStreamReader (System.in));

                    boolean breakWhile = false;
                    while (!breakWhile)
                    {
                        print ("Enter number: ");
                        final String strNum = numRead.readLine ();

                        int num;
                        try
                        {
                            num = Integer.parseInt (strNum);
                        }
                        catch (final Exception e)
                        {
                            print ("Please enter just a number!");
                            continue;
                        }

                        switch (num)
                        {
                            case 1:
                                if (fullNotDownloaded)
                                {
                                    downloadedSize = fileSave.length ();
                                    download (link , question , fileSave);
                                    return;
                                }
                                else
                                {
                                    print ("Error number!");
                                    break;
                                }
                            case 2:
                                if (fileSave.delete ())
                                {
                                    print ("File deleted!");
                                    breakWhile = true;
                                    break;
                                }
                                else
                                {
                                    print ("Delete file error!");
                                    return;
                                }
                            case 3:
                                fileSave = new File (fileSave.getParent () + File.separator + getNewFilename () + "." + FilenameUtils.getExtension (filename));
                                download (link , false , fileSave);
                                return;
                            case 4:
                                print ("Cancel download.");
                                return;
                        }
                    }
                }

                this.fileSave = fileSave;

                inputStream = connection.getInputStream ();

                final byte[] buffer = new byte[5120];

                if (downloadedSize == 0 || fileOutputStream == null)
                    fileOutputStream = new FileOutputStream (fileSave , (downloadedSize > 0));

                int len;
                long lenInSec = 0;
                float min;
                int max = 0;
                long startRead, endRead;
                double second, secTemp = 0;
                String strSpeedDownload = "0 KB/s";

                forPause ();
                while (!stopDownload)
                {
                    if (!pause)
                    {
                        try
                        {
                            startRead = System.nanoTime ();

                            len = inputStream.read (buffer);

                            if (len <= 0)
                            {
                                stopDownload = true;
                                pause = true;
                                break;
                            }

                            endRead = System.nanoTime ();

                            second = (endRead - startRead) / 1_000_000_000.0;

                            secTemp += second;
                            fileOutputStream.write (buffer , 0 , len);

                            downloadedSize += len;
                            lenInSec += len;

                            min = Math.abs (((float) downloadedSize / filesize) * 100);

                            if ((int) min > max)
                                max = (int) min;

                            if (secTemp >= 1)
                            {
                                strSpeedDownload = StringSpeedDownload.Get (lenInSec);
                                lenInSec = 0;
                                secTemp = 0;
                            }
                            print (String.format ("\r %s || %s || %s || %s " , progress (max) , GetSize.Get (downloadedSize) , strSpeedDownload , okTime ()));
                        }
                        catch (final Exception e)
                        {
                            print ("\rDownload Error <" + e.getMessage () + ">\n");
                            pause ();
                        }
                    }
                }
                closeStream ();
                if (question)
                {
                    print ("\rDownload complete.\n");
                    Desktop.getDesktop ().open (fileSave.getParentFile ());

                    argLastLocIsFile = false;
                    argLocationSave = null;
                    argMkDir = false;
                    argLink = null;

                    System.gc ();
                    runClass ();
                }
                else
                    print ("\rDownloaded " + FilenameUtils.getName (fileSave.getPath ()) + ".");
            }
            else
                throw new IOException ("Error select path save file");
        }
        else
        {
            print ("Cancel download.\n");
            close ();
        }
    }

    private String getNewFilename ()
    {
        final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));

        print ("\nExit = " + EXIT + "\n");
        while (true)
        {
            try
            {
                print ("\nEnter new name: ");
                final String name = reader.readLine ();
                if (name != null && !name.isEmpty ())
                {
                    if (name.equals (EXIT)) System.exit (0);

                    if (name.matches ("[-_.A-Za-z0-9]*")) return name;
                    else throw new IOException ("Invalid name!");
                }
                else throw new IOException ("Name is empty!");
            }
            catch (final IOException e)
            {
                print ("Error enter name <" + e.getMessage () + ">");
            }
        }
    }

    private void closeStream () throws IOException
    {
        fileOutputStream.flush ();
        fileOutputStream.close ();
        inputStream.close ();
        connection.disconnect ();
    }

    private void forPause ()
    {
        forPause = true;
        print ("\nPress Enter to pause the download\n");
        new Thread (() ->
        {
            final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            while (true)
            {
                if (forPause)
                {
                    try
                    {
                        reader.readLine ();
                        if (forPause) pause ();
                    }
                    catch (final IOException ignored)
                    {
                    }
                }
            }
        }).start ();
    }

    private void pause ()
    {
        forPause = false;
        new Thread (() ->
        {
            final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            print ("\nDownload is pause, continue ? (y,n): ");
            pause = true;
            try
            {
                final String continueDownload = reader.readLine ();
                if (continueDownload != null && continueDownload.toLowerCase (Locale.ROOT).equals ("y"))
                    pause = false;
                else
                    stopDownload = true;

                synchronized (Download.this)
                {
                    Download.this.notify ();
                    Download.this.notifyAll ();
                }

            }
            catch (final IOException e)
            {
                e.printStackTrace ();
            }
        }).start ();

        synchronized (Download.this)
        {
            try
            {
                Download.this.wait ();
            }
            catch (InterruptedException ignored)
            {
            }
        }

        if (!stopDownload && !pause)
        {
            try
            {
                download (link , true , fileSave);
            }
            catch (final Exception e)
            {
                pause ();
            }
        }
        else close ();
    }

    @bardiademon
    private File getLocation (final String nameTypeFile , final boolean justDir)
    {
        if (isOkArgLocationSave ()) return (new File (argLocationSave));
        else if (manuallyEnterTheAddress)
        {
            final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            print ("\nExit = " + EXIT + "\n");
            while (true)
            {
                try
                {
                    print ("Enter path <" + nameTypeFile + ">: ");
                    final String path = reader.readLine ();
                    if (path != null && !path.isEmpty ())
                    {
                        if (path.equals (EXIT))
                            System.exit (0);

                        else
                        {
                            final File file = pathValidation (new File (path) , nameTypeFile);
                            if (file != null) return file;
                        }
                    }
                }
                catch (final IOException e)
                {
                    print ("Error reader <" + e.getMessage () + ">");
                    System.exit (0);
                }
            }
        }
        else
        {
            final JFileChooser chooser = new JFileChooser ();

            if (justDir)
                chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            else
                chooser.setSelectedFile (new File (nameTypeFile));

            final AtomicInteger openDialogResult = new AtomicInteger ();
            SwingUtilities.invokeLater (() ->
            {
                openDialogResult.set (chooser.showSaveDialog (null));
                synchronized (Download.this)
                {
                    Download.this.notify ();
                    Download.this.notifyAll ();
                }
            });

            synchronized (Download.this)
            {
                try
                {
                    Download.this.wait ();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace ();
                }
            }

            File fileSave;
            if (openDialogResult.get () == JFileChooser.OPEN_DIALOG && (fileSave = chooser.getSelectedFile ()) != null && fileSave.getParentFile () != null)
            {
                print (fileSave.getPath () + "\n");
                return fileSave;
            }
            return null;
        }
    }

    private void close ()
    {
        System.gc ();
        System.exit (0);
    }

    private void print (String str)
    {
        if (!pause) System.out.print (str);
    }

}
