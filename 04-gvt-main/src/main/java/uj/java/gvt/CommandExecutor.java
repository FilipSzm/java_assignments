package uj.java.gvt;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.lang.Long.min;

public class CommandExecutor {
    private final static String DATA = ".gvt";
    private final static String FILES = "/files";
    private final static String INFO = "/info";
    private final static String HISTORY = "/history";

    private final static String SLASH = "/";

    enum LocationType {
        fromExternal,
        toExternal,
        historyToCurrent
    }

    public int init () throws IOException, ClassNotFoundException {
        if (isInitialized())
            return 10;

        Files.createDirectory(Path.of(DATA));
        Files.createDirectory(Path.of(DATA + HISTORY));
        Files.createDirectory(Path.of(DATA + FILES));
        Files.createFile(Path.of(DATA + INFO));


        Version initialVersion = new Version(0, "GVT initialized.");
        setCurrentVersion(initialVersion);
        moveCurrentToHistory(null);
        return 0;
    }

    public int add (String pathFromArgs, String optionalMessage) throws IOException, ClassNotFoundException {
        Path path = getPath(pathFromArgs);
        Version currVer = getCurrentVersion();

        if (path == null || !Files.exists(path))
            return 21;

        String message = "Added file: " + path.getFileName().toString();
        if (optionalMessage != null)
            message = message.concat("\n" + optionalMessage);


        if (currVer.isFileTrackedExt(path.getFileName()))
            return 1;

        moveFileToCurrent(path);
        currVer.nextVersion(message);
        currVer.trackPath(path);
        setCurrentVersion(currVer);
        moveCurrentToHistory(path.getFileName());
        return 0;
    }

    public int detach (String fileName, String optionalMessage) throws IOException, ClassNotFoundException {
        Path path = getPath(fileName);

        Version currVer = getCurrentVersion();

        assert path != null;
        if (!currVer.isFileTrackedExt(path))
            return 1;

        String message = "Detached file: " + path.getFileName().toString();
        if (optionalMessage != null)
            message = message.concat("\n" + optionalMessage);

        currVer.nextVersion(message);
        currVer.untrackPath(fileName);
        setCurrentVersion(currVer);
        moveCurrentToHistory(null);
        return 0;
    }

    public int checkout (String versionFromArgs) throws IOException, ClassNotFoundException {
        long version = Long.parseUnsignedLong(versionFromArgs);
        if (version > getCurrentVersion().number() || version < 0)
            return 40;

        Version prevVersion = getVersion(versionFromArgs);
        
        for (Path p : prevVersion.trackedFilePaths()) {
            copyFile(LocationType.historyToCurrent, p.getFileName(), prevVersion.number());
            copyFile(LocationType.toExternal, p, prevVersion.number());
        }
        return 0;
    }

    public int commit (String fileName, String optionalMessage) throws IOException, ClassNotFoundException {
        Path path = getPath(fileName);
        Version currVer = getCurrentVersion();

        if (path == null || !Files.exists(path))
            return 51;

        if (!currVer.isFileTrackedExt(path))
            return 1;

        String message = "Committed file: " + fileName;
        if (optionalMessage != null)
            message = message.concat("\n" + optionalMessage);

        copyFile(LocationType.fromExternal, path, 0);

        currVer.nextVersion(message);
        setCurrentVersion(currVer);
        moveCurrentToHistory(path.getFileName());
        return 0;
    }

    public void history (long howManyVersionsToGet) throws IOException, ClassNotFoundException {
        long howMany = historyHowManyVersionsToGet(howManyVersionsToGet);
        long currVerNumber = getCurrentVersion().number();
        List<String> toPrint = new LinkedList<>();


        while (howMany >= 0) {
            String currMess = getVersion(String.valueOf(currVerNumber)).commitMessage();
            toPrint.add(currVerNumber + ": " + currMess.split("\\n")[0]);
            currVerNumber--;
            howMany--;
        }

        Collections.reverse(toPrint);
        toPrint.forEach(System.out::println);
    }

    public int version (String version) throws IOException, ClassNotFoundException {
        long versionNumber;
        if (version == null)
            versionNumber = getCurrentVersion().number();
        else
            versionNumber = Long.parseLong(version);

        if (versionNumber > getCurrentVersion().number() || versionNumber < 0)
            return 60;

        Version versionToPrint = getVersion(String.valueOf(versionNumber));

        System.out.println("Version: " + versionNumber);
        System.out.print(versionToPrint.commitMessage());
        return 0;
    }

    private long historyHowManyVersionsToGet (long howManyFromCommand) throws IOException, ClassNotFoundException {
        if (howManyFromCommand == -1)
           return getCurrentVersion().number();

        return min(getCurrentVersion().number(), howManyFromCommand - 1);
    }

    private void copyFile (LocationType locationType, Path pathData, long version) throws IOException {
        Path from;
        Path to;
        switch (locationType) {
            case fromExternal -> {
                from = pathData;
                to = Path.of(DATA + FILES + SLASH + pathData.getFileName().toString());
            }
            case toExternal -> {
                from = Path.of(DATA + HISTORY + SLASH + version + FILES + SLASH + pathData.getFileName().toString());
                to = pathData;
            }
            case historyToCurrent -> {
                from = Path.of(DATA + HISTORY + SLASH + version + FILES + SLASH + pathData);
                to = Path.of(DATA + FILES + SLASH + pathData);
            }
            default -> {
                from = Path.of(DATA);
                to = Path.of(DATA);
            }
        }
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    }

    private void moveFileToCurrent (Path path) throws IOException {
        Files.copy(
                path,
                Path.of(DATA + FILES + SLASH + path.getFileName().toString()),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private void moveCurrentToHistory (Path newFile) throws IOException, ClassNotFoundException {
        Version currVer = getCurrentVersion();

        Files.createDirectory(Path.of(DATA + HISTORY + SLASH + currVer.number()));
        Files.createDirectory(Path.of(DATA + HISTORY + SLASH + currVer.number() + FILES));

        Files.copy(
                Path.of(DATA + INFO),
                Path.of(DATA + HISTORY + SLASH + currVer.number() + INFO)
        );

        for (Path p : currVer.trackedFilePaths()) {
            if (p.equals(newFile))
                Files.copy(
                        Path.of(DATA + FILES + SLASH + newFile),
                        Path.of(DATA + HISTORY + SLASH + currVer.number() + FILES + SLASH + newFile)
                );
            else
                Files.copy(
                        Path.of(DATA + HISTORY + SLASH + (currVer.number() - 1) + FILES + SLASH + p.getFileName()),
                        Path.of(DATA + HISTORY + SLASH + currVer.number() + FILES + SLASH + p.getFileName())
                );
        }
    }

    private void setCurrentVersion (Version version) throws IOException {
        var out = new ObjectOutputStream(new FileOutputStream(DATA + INFO));
        out.writeObject(version);
        out.close();
    }

    private Version getCurrentVersion () throws IOException, ClassNotFoundException {
        var in = new ObjectInputStream(new FileInputStream(DATA + INFO));
        Version result = (Version) in.readObject();
        in.close();
        return result;
    }

    private Version getVersion (String number) throws IOException, ClassNotFoundException {
        var in = new ObjectInputStream(
                new FileInputStream(DATA + HISTORY + SLASH + number + INFO)
        );
        Version result = (Version) in.readObject();
        in.close();
        return result;
    }

    private Path getPath (String arg) {
        try {
            return Path.of(arg);
        } catch (InvalidPathException e) {
            return null;
        }
    }

    public boolean isInitialized () throws SecurityException {
        return Files.exists(Path.of(DATA));
    }
}
