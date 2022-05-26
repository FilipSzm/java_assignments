package uj.java.battleships.map;

public class Cell {
    private Value value = Value.UNKNOWN;
    private Ship ship;
    private boolean sunk;

    public boolean isShip() {
        return ship != null;
    }

    public Ship ship() {
        return ship;
    }

    public void ship(Ship ship, Pair coordinates) {
        sunk = false;
        ship.addCell(coordinates);
        this.ship = ship;
    }

    public boolean value(Value value) {
        boolean isUnknown = this.value == Value.UNKNOWN;
        this.value = value;
        return isUnknown;
    }

    public Value value() {
        return value;
    }

    public void sink() {
        if (!sunk) {
            sunk = true;
            ship.sink();
        }
    }

    public boolean isSunk() {
        return ship.isSunk();
    }
}
