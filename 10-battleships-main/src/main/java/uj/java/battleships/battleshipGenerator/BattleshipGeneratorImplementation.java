package uj.java.battleships.battleshipGenerator;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static uj.java.battleships.battleshipGenerator.BattleshipData.SIZE_OF_MAP;

class BattleshipGeneratorImplementation implements BattleshipGenerator {

    @Override
    public String generateMap() {
        Deque<MapPoint[][]> stepMaps = new LinkedList<>();
        stepMaps.push(generateWaterPointMap());
        List<ShipsInSize> shipsToPlace = sortShipList(BattleshipData.LIST_OF_SHIPS);
        ShipPlacer shipPlacer = new ShipPlacer(stepMaps, shipsToPlace);


        if (!shipPlacer.placeAllShips())
            return "error";

        return pointArrToStringMap(stepMaps.peek());
    }

    public static MapPoint[][] generateWaterPointMap () {
        MapPoint[][] map = new MapPoint[SIZE_OF_MAP][SIZE_OF_MAP];
        for (int i = 0; i < SIZE_OF_MAP; i++)
            for (int j = 0; j < SIZE_OF_MAP; j++)
                map[i][j] = MapPoint.WATER;
        return map;
    }

    public static List<ShipsInSize> sortShipList (List<ShipsInSize> list) {
        list.sort(new CompareSizesDes());
        return list;
    }

    public static String pointArrToStringMap (MapPoint[][] arr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < SIZE_OF_MAP; i++)
            for (int j = 0; j < SIZE_OF_MAP; j++)
                builder.append(charChooser(arr[i][j]));
        return builder.toString();
    }

    private static char charChooser (MapPoint point) {
        if (point == MapPoint.SHIP)
            return '#';
        return '.';
    }
}
