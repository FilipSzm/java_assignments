package uj.java.battleships.game;

public enum Command {
    START("start"),
    MISS("pud\u0142o"),
    HIT("trafiony"),
    HIT_AND_SINK("trafiony zatopiony"),
    LAST_SINK("ostatni zatopiony"),
    WIN("win"),
    LOSE("lose"),
    FAIL("fail");

    public final String label;

    Command(String label) {
        this.label = label;
    }
}
