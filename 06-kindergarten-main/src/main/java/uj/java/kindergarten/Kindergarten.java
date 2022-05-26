package uj.java.kindergarten;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.floorMod;

public class Kindergarten {

    public static void main(String[] args) throws IOException {
        init();

        final var fileName = args[0];
        System.out.println("File name: " + fileName);

        List<String> names = new ArrayList<>();
        List<Integer> hungerTimes = new ArrayList<>();
        int numberOfChildren = readFile(Path.of(fileName), names, hungerTimes);

        List<Lock> forks = createLocks(numberOfChildren);
        List<ChildImpl> children = createChildrenList(names, hungerTimes);
        makeChildrenEat(children, forks);
    }

    private static int readFile(Path path, List<String> names, List<Integer> hungerTimes) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            int numberOfChildren = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numberOfChildren; i++) {
                String[] childData = reader.readLine().split(" ");
                names.add(childData[0]);
                hungerTimes.add(Integer.parseInt(childData[1]));
            }
            return numberOfChildren;
        } catch (Exception e) {
            return 0;
        }
    }

    private static List<Lock> createLocks(int size) {
        List<Lock> locks = new ArrayList<>();
        for (int i = 0; i < size; i++)
            locks.add(new ReentrantLock());
        return locks;
    }

    private static List<ChildImpl> createChildrenList(List<String> names, List<Integer> hungerTimes) {
        List<ChildImpl> children = new ArrayList<>();
        for (int i = 0; i < names.size(); i++)
            children.add(new ChildImpl(names.get(i), hungerTimes.get(i)));
        return children;
    }

    private static void makeChildrenEat(List<ChildImpl> children, List<Lock> forks) {
        int size = children.size();
        for (int i = 0; i < size; i++)
            children.get(i).startEating(
                    children.get(floorMod(i - 1, size)),
                    children.get((i + 1) % size),
                    forks.get(i),
                    forks.get((i + 1) % size)
            );
    }

    private static void init() throws IOException {
        Files.deleteIfExists(Path.of("out.txt"));
        System.setErr(new PrintStream(new FileOutputStream("out.txt")));
        new Thread(Kindergarten::runKindergarden).start();
    }

    private static void runKindergarden() {
        try {
            Thread.sleep(10100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            List<String> errLines = Files.readAllLines(Path.of("out.txt"));
            System.out.println("Children cries count: " + errLines.size());
            errLines.forEach(System.out::println);
            System.exit(errLines.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
