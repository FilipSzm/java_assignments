package uj.java.battleships.game;

import java.net.*;

public class SrvUtil {
    public static InetAddress findAddress() throws SocketException, UnknownHostException {
        var en0 = NetworkInterface.getByName("lo"); // 127.0.0.1 eth1
        return en0.inetAddresses()
                .filter(a -> a instanceof Inet4Address)
                .findFirst()
                .orElse(InetAddress.getLocalHost());
    }
}