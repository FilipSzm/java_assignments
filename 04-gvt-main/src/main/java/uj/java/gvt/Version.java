package uj.java.gvt;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Version implements Serializable {
    private long number;
    private String commitMessage;
    private final List<String> trackedFilePaths;

    public Version (long number, String message) {
        this.number = number;
        commitMessage = message;
        trackedFilePaths = new LinkedList<>();
    }

    public long number () { return number; }

    public void nextVersion (String message) {
        commitMessage = message;
        number++;
    }

    public String commitMessage () {
        return commitMessage;
    }

    public List<Path> trackedFilePaths() { return trackedFilePaths.stream().map(Path::of).collect(Collectors.toList()); }

    public boolean isFileTrackedExt (Path name) {
        return trackedFilePaths.contains(name.toString());
    }

    public void trackPath (Path name) {
        trackedFilePaths.add(name.toString());
    }

    public void untrackPath (String name) {
        trackedFilePaths.remove(name);
    }
}
