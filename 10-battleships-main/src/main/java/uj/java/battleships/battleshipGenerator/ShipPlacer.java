package uj.java.battleships.battleshipGenerator;

import java.util.*;

import static uj.java.battleships.battleshipGenerator.BattleshipData.NUMBER_OF_TRIES;
import static uj.java.battleships.battleshipGenerator.BattleshipData.SIZE_OF_MAP;

public class ShipPlacer {
    private final Random rand;
    private final Deque<MapPoint[][]> stepMaps;
    private final List<ShipsInSize> shipsToPlace;

    public ShipPlacer (Deque<MapPoint[][]> stepMaps, List<ShipsInSize> shipsToPlace) {
        this.stepMaps = stepMaps;
        this.shipsToPlace = shipsToPlace;
        rand = new Random();
    }


    protected boolean placeAllShips () {
        Deque<Integer> betweenPlacements = new LinkedList<>();
        betweenPlacements.add(stepMaps.size());
        ListIterator<ShipsInSize> shipsBeingPlaced = shipsToPlace.listIterator();
        while (shipsBeingPlaced.hasNext()) {
            ShipsInSize currentShips = shipsBeingPlaced.next();
            if (!placeShipsOfSameSize(stepMaps, currentShips.size(), currentShips.amount(), betweenPlacements.peek())) {
                if (betweenPlacements.size() <= 1)
                    return false;

                betweenPlacements.pop();
                while(((Integer) stepMaps.size()).equals(betweenPlacements.peek()))
                    stepMaps.pop();

                shipsBeingPlaced.previous();
                continue;
            }
            betweenPlacements.push(stepMaps.size());
        }
        return true;
    }

    private boolean placeShipsOfSameSize (Deque<MapPoint[][]> stepMaps, int size, int amount, Integer lastPlacementIndex) {
        assert stepMaps.peek() != null;
        MapPoint[][] currentMap = copyMap(stepMaps.peek());

        for (int i = 0; i < amount; i++) {
            if (placeShip(currentMap, size))
                stepMaps.push(currentMap);
            else if (stepMaps.size() > lastPlacementIndex) {
                stepMaps.pop();
                i--;
            } else
                return false;
        }
        return true;
    }

    private boolean placeShip (MapPoint[][] map, int sizeOfShip) {
        MapPoint[][] mapCopy = copyMap(map);
        for (int i = 0; i < NUMBER_OF_TRIES; i++)
            if (tryRandomPoint(mapCopy, sizeOfShip, map))
                return true;
        return false;
    }

    private boolean tryRandomPoint (MapPoint[][] mapCopy, int sizeOfShip, MapPoint[][] map) {
        int x = rand.nextInt(SIZE_OF_MAP);
        int y = rand.nextInt(SIZE_OF_MAP);

        if (mapCopy[x][y].equals(MapPoint.WATER)) {
            mapCopy[x][y] = MapPoint.CURRENT;

            if (placeShipInWater(mapCopy, sizeOfShip, x, y, map)) {
                map[x][y] = MapPoint.SHIP;
                pointsAround(map, x, y);
                return true;
            } else mapCopy[x][y] = MapPoint.TRIED;
        }

        return false;
    }

    private boolean placeShipInWater (MapPoint[][] mapCopy, int sizeOfShip, int x, int y, MapPoint[][] map) {
        if (sizeOfShip > 1)
            return placeBiggerShip(mapCopy, sizeOfShip - 1, x, y, map);

        return true;
    }

    private boolean placeBiggerShip (MapPoint[][] map, int size, int lastX, int lastY, MapPoint[][] originalMap) {
        int[][] coordinates = new int[][] {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        List<Integer> toShuffle = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(toShuffle);

        for (int i = 0; i < 4; i++) {
            int x = lastX + coordinates[toShuffle.get(i)][0];
            int y = lastY + coordinates[toShuffle.get(i)][1];
            if (checkDirection(map, size, x, y, originalMap))
                return true;
        }
        return false;
    }

    private boolean checkDirection (MapPoint[][] map, int size, int x, int y, MapPoint[][] originalMap) {
        if (isValidPoint(x, y, map)) {
            map[x][y] = MapPoint.CURRENT;
            if (placeShipPart(map, size, x, y, originalMap)) {
                originalMap[x][y] = MapPoint.SHIP;
                pointsAround(originalMap, x, y);
                return true;
            }
        }
        return false;
    }

    private boolean placeShipPart (MapPoint[][] map, int size, int x, int y, MapPoint[][] originalMap) {
        if (size > 1)
            return placeBiggerShip(map, size - 1, x, y, originalMap);
        return true;
    }

    public static MapPoint[][] copyMap(MapPoint[][] arr) {
        MapPoint[][] newArr = new MapPoint[SIZE_OF_MAP][SIZE_OF_MAP];
        for (int i = 0; i < arr.length; i++)
            System.arraycopy(arr[i], 0, newArr[i], 0, arr[i].length);

        return newArr;
    }

    public static void pointsAround(MapPoint[][] map, int x, int y) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (isValidPointAround(x + i, y + j, i, j, map))
                    map[x + i][y + j] = MapPoint.NTSHIP;
            }
        }
    }

    public static boolean isValidPoint (int x, int y, MapPoint[][] map) {
        return checkIfPointInsideMap(x, y) && (map[x][y] == MapPoint.WATER || map[x][y] == MapPoint.TRIED);
    }

    public static boolean isValidPointAround(int x, int y, int i, int j, MapPoint[][] map) {
        return checkIfPointInsideMap(x, y) && !(i == 0 && j == 0) && !map[x][y].equals(MapPoint.SHIP);
    }

    public static boolean checkIfPointInsideMap(int x, int y) {
        return x >= 0 && x < SIZE_OF_MAP && y >= 0 && y < SIZE_OF_MAP;
    }
}
