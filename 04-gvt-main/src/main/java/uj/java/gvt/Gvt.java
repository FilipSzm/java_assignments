package uj.java.gvt;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

import static uj.java.gvt.GvtParser.commandParser;
import static uj.java.gvt.GvtParser.throwError;

public class Gvt {
    public static void main (String... args) {
        if (args.length == 0)
            throwError("Please specify command.", 1);

        try {
            commandParser(args);
        } catch (Exception e) {
            throwError("Underlying system problem. See ERR for details.", -3, e);
        }
    }
}
