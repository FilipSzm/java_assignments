package uj.java.battleships.map;

import java.util.LinkedList;
import java.util.List;

public class Ship {
    private List<Pair> coordinates = new LinkedList<>();
    private int partsNotSunk = 0;

    public void addCell(Pair coordinates) {
        this.coordinates.add(coordinates);
        partsNotSunk++;
    }

    public boolean isSunk() {
        return partsNotSunk == 0;
    }

    public void sink() {
        partsNotSunk--;
    }

    public List<Pair> coordinates() {
        return coordinates;
    }

    public void destroy() {
        coordinates = null;
    }
}
