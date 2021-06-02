package com.bardiademon.Downloder;

@bardiademon
public class Dl
{
    private static final String ARG_LINK = "-l",
            ARG_LOCATION_SAVE = "-p",
            ARG_MK_DIR = "-m",
            ARG_LAST_LOC_IS_FILE = "-f",
            ARG_HELP = "-h",
            ARG_DOWNLOAD_QUESTION = "-q",
            ARG_MANUALLY_ENTER_THE_ADDRESS = "-ma";

    private final String[] args;

    private Dl (final String[] args)
    {
        this.args = args;

        String link = null;
        String locationSave = null;
        boolean mkDir = false;
        boolean lastLocIsFile = false;
        boolean downloadQuestion = false;
        boolean manuallyEnterTheAddress = false;

        if (args.length > 0)
        {
            if (findSingle (ARG_HELP))
            {
                printHelp ();
                System.exit (0);
            }

            link = getArg (find (ARG_LINK));
            locationSave = getArg (find (ARG_LOCATION_SAVE));

            mkDir = findSingle (ARG_MK_DIR);
            lastLocIsFile = findSingle (ARG_LAST_LOC_IS_FILE);
            downloadQuestion = findSingle (ARG_DOWNLOAD_QUESTION);
            manuallyEnterTheAddress = findSingle (ARG_MANUALLY_ENTER_THE_ADDRESS);
        }

        // lastLocIsFile => yani agar file bedone pasvand bod ono dir dar nazar nagire
        new Download (link , locationSave , mkDir , lastLocIsFile , downloadQuestion , manuallyEnterTheAddress);
    }

    @bardiademon
    public static void main (final String[] args)
    {
        new Dl (args);
    }

    private String getArg (final int index)
    {
        if (index >= 0)
        {
            try
            {
                return args[index];
            }
            catch (final Exception e)
            {
                e.printStackTrace ();
                System.out.println ("Error args");
                System.exit (0);
            }
        }

        return null;
    }

    private int find (final String what)
    {
        for (int i = 0, len = args.length; i < len; i++)
        {
            try
            {
                if (args[i].equals (what)) return (i + 1);
            }
            catch (Exception ignored)
            {
            }
        }
        return -1;
    }

    private boolean findSingle (final String what)
    {
        for (final String arg : args)
        {
            try
            {
                if (arg.equals (what)) return true;
            }
            catch (Exception ignored)
            {
            }
        }

        return false;
    }

    private void printHelp ()
    {
        System.out.printf ("\n%s -> Get the download link , Sample ( java -jar download.jar %s \"LINK\" )\n\n" +
                        "%s -> File saving path , Sample ( java -jar download.jar %s \"PATH\" )\n\n" +
                        "%s -> If there is no path, create the path , Sample ( java -jar download.jar %s )\n\n" +
                        "%s -> If the file has no extension, So it is not a folder , Sample ( java -jar download.jar %s )\n\n" +
                        "%s -> If it is incorrect, the download will start without any questions , Sample ( java -jar download.jar %s )\n\n" +
                        "%s -> If it is false, the file chooser window will open , Sample ( java -jar download.jar %s )\n\n" ,
                ARG_LINK , ARG_LINK ,
                ARG_LOCATION_SAVE , ARG_LOCATION_SAVE ,
                ARG_MK_DIR , ARG_MK_DIR ,
                ARG_LAST_LOC_IS_FILE , ARG_LAST_LOC_IS_FILE ,
                ARG_DOWNLOAD_QUESTION , ARG_DOWNLOAD_QUESTION ,
                ARG_MANUALLY_ENTER_THE_ADDRESS , ARG_MANUALLY_ENTER_THE_ADDRESS
        );
    }
}
