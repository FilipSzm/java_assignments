package uj.java.gvt;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

public class GvtParser {
    private static CommandExecutor exec;

    protected static void commandParser (String[] args) throws IOException, ClassNotFoundException {
        exec = new CommandExecutor();
        if (args[0].equals("init")) {
            init();
        } else if (exec.isInitialized()) {
            switch (args[0]) {
                case "add" -> add(args);
                case "detach" -> detach(args);
                case "commit" -> commit(args);
                case "checkout" -> checkout(args);
                case "history" -> history(args);
                case "version" -> version(args);
                default -> throwError("Unknown command " + args[0] + ".", 1);
            }
        } else
            throwError("Current directory is not initialized. Please use \"init\" command to initialize.", -2);
    }

    private static void init () throws IOException, ClassNotFoundException {
        int result = exec.init();

        if (result == 10)
            throwError("Current directory is already initialized.", 10);
        else
            System.out.println("Current directory initialized successfully.");
    }

    private static void add (String[] args) {
        String comment = getComment(args);

        if (args.length <= 1)
            throwError("Please specify file to add.", 20);

        String fileName = getFileName(args[1]);
        try {
            switch (exec.add(args[1], comment)) {
                case 0 -> System.out.println("File " + fileName + " added successfully.");
                case 1 -> System.out.println("File " + fileName + " already added.");
                case 21 -> throwError("File " + fileName + " not found.", 21);
            }
        } catch (Exception e) {
            throwError("File " + fileName + " cannot be added, see ERR for details.", 22, e);
        }
    }

    private static void detach (String[] args) {
        String comment = getComment(args);

        if (args.length <= 1) {
            throwError("Please specify file to detach.", 30);
        }

        String fileName = getFileName(args[1]);
        try {
            switch (exec.detach(args[1], comment)) {
                case 0 -> System.out.println("File " + fileName + " detached successfully.");
                case 1 -> System.out.println("File " + fileName + " is not added to gvt.");
            }
        } catch (Exception e) {
            throwError("File " + fileName + " cannot be detached, see ERR for details.", 31, e);
        }
    }

    private static void checkout (String[] args) throws IOException, ClassNotFoundException {
        switch (exec.checkout(args[1])) {
            case 0 -> System.out.println("Version " + args[1] + " checked out successfully.");
            case 40 -> throwError("Invalid version number: " + args[1], 40);
        }
    }

    private static void commit (String[] args) {
        String comment = getComment(args);

        if (args.length <= 1)
            throwError("Please specify file to commit.", 50);

        String fileName = getFileName(args[1]);
        try {
            switch (exec.commit(args[1], comment)) {
                case 0 -> System.out.println("File " + fileName + " committed successfully.");
                case 1 -> System.out.println("File " + fileName + " is not added to gvt.");
                case 51 -> throwError("File " + fileName + " does not exist.", 51);
            }
        } catch (Exception e) {
            throwError("File " + fileName + " cannot be commited, see ERR for details.", -52, e);
        }
    }

    private static void history (String[] args) throws IOException, ClassNotFoundException {
        exec.history(howManyVersions(args));
    }

    private static void version (String[] args) throws IOException, ClassNotFoundException {
        String versionNumber = args.length > 1 ? args[1] : null;
        try {
            if (exec.version(versionNumber) == 60)
                throwError("Invalid version number: " + args[1], 60);
        } catch (NumberFormatException e) {
            throwError("Invalid version number: " + args[1], 60);
        }
    }


    private static long howManyVersions (String[] args) {
        if (args.length < 2 || !args[1].equals("-last")) return -1;
        try {
            return Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String getFileName (String arg) {
        try {
            return Path.of(arg).getFileName().toString();
        } catch (InvalidPathException e) {
            return arg;
        }
    }

    private static String getComment (String[] args) {
        if (args.length <= 2) return null;
        if (Objects.equals(args[args.length - 2], "-m"))
            return args[args.length - 1];
        return null;
    }

    protected static void throwError (String message, int status) {
        System.out.println(message);
        System.exit(status);
    }
    protected static void throwError (String message, int status, Exception e) {
        System.out.println(message);
        e.printStackTrace();
        System.exit(status);
    }
}
