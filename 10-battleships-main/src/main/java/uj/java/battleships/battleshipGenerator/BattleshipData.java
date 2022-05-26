package uj.java.battleships.battleshipGenerator;

import java.util.*;

public class BattleshipData {
    protected final static int SIZE_OF_MAP = 10;
    protected final static int NUMBER_OF_TRIES = 40;

    protected final static List<ShipsInSize> LIST_OF_SHIPS = new ArrayList<>(Arrays.asList(
            new ShipsInSize(4, 1),
            new ShipsInSize(3, 2),
            new ShipsInSize(2, 3),
            new ShipsInSize(1, 4)
    ));
}

record ShipsInSize (int size, int amount) { }

class CompareSizesDes implements Comparator<ShipsInSize> {

    @Override
    public int compare (ShipsInSize o1, ShipsInSize o2) {
        return Integer.compare(o2.size(), o1.size());
    }
}

enum MapPoint {
    SHIP,
    WATER,
    NTSHIP,
    TRIED,
    CURRENT
}


