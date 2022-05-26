package uj.java.battleships.map;

public enum Value {
    WATER('.'),
    SHIP('#'),
    UNKNOWN('?'),
    HIT('@'),
    MISS('~');

    public final char value;

    Value(char value) {
        this.value = value;
    }
}
