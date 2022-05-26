package uj.java.battleships;

import uj.java.battleships.game.Client;
import uj.java.battleships.game.Mode;
import uj.java.battleships.game.Server;
import uj.java.battleships.battleshipGenerator.MapCreator;
import uj.java.battleships.map.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Loader {
    private Mode mode;
    private String address = null;
    private int port = -1;
    private Path mapPath;
    private String map;
    private Map playersMap;
    private Map opponentsMap;

    public Loader(String[] args) {
        load(args);
    }

    private void load(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-mode" -> mode(args[++i]);
                case "-port" -> port(args[++i]);
                case "-address" -> address(args[++i]);
                case "-map" -> mapPath(args[++i]);
                default -> throwParameterError(args[i]);
            }
        }
        checkParams();
        readMap();

        playersMap = new Map(map);
        opponentsMap = new Map();
        try {
            createPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPlayer() throws IOException {
        if (mode == Mode.SERVER) {
            new Server(port, playersMap, opponentsMap);
        }
        else {
            new Client(address, port, playersMap, opponentsMap);
        }
    }

    private void readMap() {
        if (!Files.exists(mapPath))
            MapCreator.createMap(mapPath);

        List<String> fileData = null;
        try {
            fileData = Files.readAllLines(mapPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (!mapCorrectness(fileData)) {
            System.err.println("File: " + mapPath + ", does not contain map.");
            System.exit(2);
        }
        map = fileData.get(0);
    }

    private boolean mapCorrectness(List<String> fileData) {
        if (fileData.size() != 1)
            return false;

        String map = fileData.get(0);
        return map.matches("[.#]{100}");
    }

    private void checkParams() {
        if (mode == null)
            throwNoParameterError("mode");
        if (mode == Mode.SERVER && address != null)
            throwParameterError("-address");
        if (mode == Mode.CLIENT && address == null)
            throwNoParameterError("address");
        if (port == -1)
            throwNoParameterError("port");
        if (mapPath == null)
            throwNoParameterError("map");
    }

    private void mode(String arg) {
        switch (arg) {
            case "client" -> mode = Mode.CLIENT;
            case "server" -> mode = Mode.SERVER;
            default -> throwParameterError(arg);
        }
    }

    private void address(String arg) {
        address = arg;
    }

    private void port(String arg) {
        try {
            port = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throwParameterError(arg);
        }
    }

    private void mapPath(String arg) {
        mapPath = Path.of(arg);
    }

    public void throwNoParameterError(String errorMessage) {
        System.err.println("Please specify: " + errorMessage);
        System.exit(1);
    }

    public void throwParameterError(String errorMessage) {
        System.err.println("Incorrect parameter: " + errorMessage);
        System.exit(-1);
    }
}
