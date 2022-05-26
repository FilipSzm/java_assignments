package uj.java.battleships.map;

public record Pair(int x, int y) {
    public static final Pair[] nextTo = {
            new Pair(1, 0), new Pair(0 , 1),
            new Pair(-1, 0), new Pair(0, -1)
    };

    public static final Pair[] around = {
            new Pair(1, 1), new Pair(1 , -1),
            new Pair(-1, 1), new Pair(-1, -1),
            new Pair(1, 0), new Pair(0 , 1),
            new Pair(-1, 0), new Pair(0, -1)
    };
}
