import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Du {
    @Option(name = "-h", usage = "human-readable")
    private boolean human_readable = false;
    @Option(name = "-c", usage = "total size of all files")
    private boolean count = false;
    @Option(name = "--si", usage = "base 1000, not 1024")
    private boolean baseSize = false;
    private int base = 1024;
    private String units; //"KB"
    @Argument
    private List<String> fileN = new ArrayList<>();

    private void argumentParsing(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            System.out.println("");
            return;
        }
        if (baseSize) base = 1000;
    }

    private String changeFormat(long bytes) throws Exception {
        if (human_readable) {
            if (bytes % base == bytes) {
                return (bytes + " B");
            } else if (bytes % base <= base) {
                long kiloBytes = (bytes / base);
                return (kiloBytes + " KB");
            } else if (bytes % base <= Math.pow(2, base)) {
                long megaBytes = (long) (bytes / Math.pow(2, base));
                return (megaBytes + " MB");
            } else if (bytes % base <= Math.pow(3, base)) {
                long gigaBytes = (long) (bytes / Math.pow(3, base));
                return (gigaBytes + " GB");
            }
        }
        return ((bytes / base) + "");
    }

    private long getSizeOfDir(File dir) {
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getSizeOfDir(file);
                }
            }
        }
        return size;
    }

    private long getSizeOfFile(String name) throws IllegalArgumentException {
        Path path = Paths.get(name);
        File file = path.toFile();
        if (file.exists()) {
            if (file.isDirectory()) {
                return getSizeOfDir(file);
            }
            return file.length();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void eachSize() throws NullPointerException {
        if (fileN.isEmpty())
            throw new NullPointerException();
        List<String> sizes = new ArrayList<>();
        for (String name : fileN) {
            long size = getSizeOfFile(name);
            try {
                String toFormat = changeFormat(size);
                sizes.add(toFormat);
            } catch (Exception ex) {
                System.out.println("");
                return;
            }
        }
        StringBuilder output = new StringBuilder();
        for (String str : sizes) output.append(str).append(" ");
        System.out.println(output.toString().strip());
    }

    private void totalSize() throws NullPointerException {
        if (fileN.isEmpty()) throw new NullPointerException();
        long total = 0;
        for (String file : fileN) total += getSizeOfFile(file);
        try {
            String totalFormat = changeFormat(total);
            System.out.println(totalFormat);
        } catch(Exception e) {
            System.out.println("Unsupported unit");
        }
    }

    private void returnSizeOfFiles(String[] args) throws Exception {
        argumentParsing(args);
        try {
            if (count) {
                totalSize();
            } else {
                eachSize();
            }
        } catch(IllegalArgumentException ell) {
            System.out.println("Can not find one of the files");
        } catch(NullPointerException en) {
            System.out.println("No filenames given");
        }
    }

    public static void main(String[] args) throws Exception {
        new Du().returnSizeOfFiles(args);
    }

    /*public void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
           parser.parseArgument(args);
           if (arguments.isEmpty())
               throw new CmdLineException(parser, "No argument is given");
        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java DuCommands [options...] arguments...");
            //System.err.println("Example:\n" + "du [-h] [-c] [--si] file1 file2 file3");
            parser.printUsage(System.err);
            System.err.println("Example: java DuCommands" + parser.printExample(ALL));
            return;
        }
    }*/
}