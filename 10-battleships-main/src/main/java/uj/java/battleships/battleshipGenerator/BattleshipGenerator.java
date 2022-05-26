package uj.java.battleships.battleshipGenerator;

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipGeneratorImplementation();
    }

}
