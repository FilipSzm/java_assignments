package uj.java.battleships.game;

import uj.java.battleships.map.Map;

import java.io.IOException;
import java.net.Socket;

public class Client {

    public Client(String address, int port, Map playersMap, Map opponentsMap) throws IOException {
        joinSession(address, port, playersMap, opponentsMap);
    }

    private void joinSession(String address, int port, Map playersMap, Map opponentsMap) throws IOException {
        Socket s = new Socket(address, port);
        BattleshipSession session = new BattleshipSession(s, Mode.CLIENT, playersMap, opponentsMap);
        new Thread(session, "[Client]").start();
    }
}
