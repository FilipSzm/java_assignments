package uj.java.battleships.battleshipGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class MapCreator {

    public static void createMap(Path path) {
        var generator = BattleshipGenerator.defaultInstance();
        try {
            if (!Files.exists(path))
                Files.createFile(path);
            Files.write(path, Collections.singleton(generator.generateMap()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
