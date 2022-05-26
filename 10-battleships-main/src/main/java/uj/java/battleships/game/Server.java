package uj.java.battleships.game;

import uj.java.battleships.map.Map;

import java.io.IOException;
import java.net.*;

public class Server {
    public Server(int port, Map playersMap, Map opponentsMap) throws IOException {
        createSession(port, playersMap, opponentsMap);
    }

    private void createSession(int port, Map playersMap, Map opponentsMap) throws IOException {
        InetAddress address = SrvUtil.findAddress();
        ServerSocket serverSocket = new ServerSocket(port, 1, address);
        System.out.println("Running Server at address: " + address + ", port: " + port);
        Socket socket = serverSocket.accept();
        System.out.println("Got request from " + socket.getRemoteSocketAddress() + ", starting session");
        var session = new BattleshipSession(socket, Mode.SERVER, playersMap, opponentsMap);
        new Thread(session, "[Server]").start();
    }
}
