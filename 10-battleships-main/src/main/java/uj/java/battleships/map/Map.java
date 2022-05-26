package uj.java.battleships.map;

import uj.java.battleships.game.Command;

public class Map {
    private final Cell[][] map = new Cell[10][10];
    public int numberOfShips = 0;

    public Map() {
        initializeMap();
    }

    public Map(String rawMap) {
        initializeMap();
        fillFromData(rawMap);
    }

    public void show() {
        var builder = new StringBuilder();
        for (Cell[] cells : map) {
            for (Cell cell : cells) {
                builder.append(cell.value().value);
            }
            builder.append('\n');
        }
        System.out.println(builder);
    }

    private void initializeMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new Cell();
            }
        }
    }

    private void fillFromData(String data) {
        var values = data.toCharArray();
        for (int i = 0; i < values.length; i++) {
            switch (values[i]) {
                case '#' -> set(i / 10, i % 10, Value.SHIP);
                case '.' -> set(i / 10, i % 10, Value.WATER);
            }
        }
    }

    public void uncoverWater() {
        for (Cell[] cells : map) {
            for (Cell cell : cells) {
                if (cell.value() == Value.UNKNOWN)
                    cell.value(Value.WATER);
            }
        }
    }

    public Command hit(int x, int y) {
        switch (map[x][y].value()) {
            case WATER -> {
                map[x][y].value(Value.MISS);
                return Command.MISS;
            }
            case HIT -> {
                if (map[x][y].isSunk())
                    return Command.HIT_AND_SINK;
                return Command.HIT;
            }
            case SHIP -> {
                map[x][y].sink();
                map[x][y].value(Value.HIT);
                if (map[x][y].isSunk()) {
                    if (--numberOfShips == 0)
                        return Command.LAST_SINK;
                    return Command.HIT_AND_SINK;
                }
                return Command.HIT;
            }
        }
        return Command.MISS;
    }

    public void check(int x, int y, Command command) {
        switch (command) {
            case MISS -> set(x, y, Value.WATER);
            case HIT -> set(x, y, Value.SHIP);
            case HIT_AND_SINK, LAST_SINK -> {
                set(x, y, Value.SHIP);
                map[x][y].ship().coordinates().forEach(c -> showAround(c.x(), c.y()));
            }
        }
    }

    private void showAround(int x, int y) {
        for (var dir : Pair.around) {
            if (checkCell(x + dir.x(), y + dir.y(), Value.UNKNOWN))
                set(x + dir.x(), y + dir.y(), Value.WATER);
        }
    }

    private void set(int x, int y, Value value) {
        if (value == Value.SHIP) {
            if (map[x][y].value(value))
                placeShip(x, y);
        } else
            map[x][y].value(value);
    }

    private void placeShip(int x, int y) {
        boolean found = false;
        Ship addedTo = null;
        for (var dir : Pair.nextTo) {
            if (checkCell(x + dir.x(), y + dir.y())) {
                if (found && map[x + dir.x()][y + dir.y()].ship() != addedTo) {
                    Ship toDestroy = map[x + dir.x()][y + dir.y()].ship();
                    Ship finalAddedTo = addedTo;
                    toDestroy.coordinates().forEach(c -> map[c.x()][c.y()].ship(finalAddedTo, new Pair(c.x(), c.y())));
                    toDestroy.destroy();
                    numberOfShips--;
                } else if (!found) {
                    addedTo = map[x + dir.x()][y + dir.y()].ship();
                    map[x][y].ship(map[x + dir.x()][y + dir.y()].ship(), new Pair(x, y));
                    found = true;
                }
            }
        }
        if (!found) {
            map[x][y].ship(new Ship(), new Pair(x, y));
            numberOfShips++;
        }
    }

    private boolean checkCell(int x, int y) {
        if (x < 0 || x > 9 || y < 0 || y > 9)
            return false;
        return map[x][y].isShip();
    }

    private boolean checkCell(int x, int y, Value value) {
        if (x < 0 || x > 9 || y < 0 || y > 9)
            return false;
        return map[x][y].value() == value;
    }
}
