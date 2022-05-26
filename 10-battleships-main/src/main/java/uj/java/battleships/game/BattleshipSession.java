package uj.java.battleships.game;

import uj.java.battleships.map.Map;
import uj.java.battleships.map.Pair;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class BattleshipSession implements Runnable {
    private final Mode mode;
    private final BufferedWriter out;
    private final BufferedReader in;
    private final Map playersMap;
    private final Map opponentsMap;
    private final Random rnd = new Random();
    private int tries = 0;

    public BattleshipSession(Socket socket, Mode mode, Map playersMap, Map opponentsMap) throws IOException {
        socket.setSoTimeout(1000);
        this.mode = mode;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.playersMap = playersMap;
        this.opponentsMap = opponentsMap;
    }

    @Override
    public void run() {
        Command command = null;
        String guess = null;
        String message = null;

        playersMap.show();
        try {
            if (mode == Mode.CLIENT) {
                guess = guess();
                message = generateMessage(Command.START, guess);
                send(message);
            }

            if (mode == Mode.SERVER)
            while (true) {
                String tempGuess = guess();
                command = receive(tempGuess);
                if (command != Command.FAIL) {
                    tries = 0;
                    guess = guess();
                    message = generateMessage(command, guess);
                    send(message);
                    break;
                }
            }

            while (true) {
                command = receive(guess);
                if (command == Command.WIN)
                    break;
                if (command != Command.FAIL) {
                    tries = 0;
                    guess = guess();
                    message = generateMessage(command, guess);
                }
                send(message);
                if (command == Command.LAST_SINK) {
                    command = Command.LOSE;
                    break;
                }
                if (tries >= 3) {
                    System.out.println("B\u0142\u0105d komunikacji");
                    System.exit(3);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (command == Command.WIN) {
            System.out.println("Wygrana");
            opponentsMap.uncoverWater();
        } else
            System.out.println("Przegrana");

        opponentsMap.show();
        System.out.println();
        playersMap.show();
        System.exit(0);
    }

    private void send(String message) throws IOException {
        System.out.print("sending: " + message);
        out.write(message);
        out.flush();
    }

    private Command receive(String lastGuess) throws IOException {
        String read = "";

        try {
            read = in.readLine();
        } catch (SocketTimeoutException ignore) {}

        System.out.println("recieved: " + read);
        String[] input = read.split(";");
        Command command;
        switch (input[0]) {
            case "start" -> command = Command.START;
            case "pud\u0142o" -> command = Command.MISS;
            case "trafiony" -> command = Command.HIT;
            case "trafiony zatopiony" -> command = Command.HIT_AND_SINK;
            case "ostatni zatopiony" -> command = Command.LAST_SINK;
            default -> {
                tries++;
                command = Command.FAIL;
            }
        }
        if (command == Command.FAIL)
            return command;

        if (command != Command.START) {
            var cords = cordsFromString(lastGuess);
            opponentsMap.check(cords.x(), cords.y(), command);
        }

        if (command == Command.LAST_SINK)
            return Command.WIN;

        var cords = cordsFromString(input[1]);
        return playersMap.hit(cords.x(), cords.y());
    }

    private String guess() {
        return (char)(rnd.nextInt(10) + 'A') + String.valueOf(rnd.nextInt(1,11));
    }

    private Pair cordsFromString(String cords) {
        return new Pair(Integer.parseInt(cords.substring(1)) - 1, cords.charAt(0) - 'A');
    }

    private String generateMessage(Command command, String coordinates) {
        String comm = command.label;
        if (command != Command.LAST_SINK)
            return comm + ";" + coordinates + "\n";
        return comm + "\n";
    }
}