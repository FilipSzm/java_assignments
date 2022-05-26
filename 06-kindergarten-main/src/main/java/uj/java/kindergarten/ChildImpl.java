package uj.java.kindergarten;

import java.util.concurrent.locks.Lock;

public final class ChildImpl extends Child {
    ChildImpl leftNeighbor;
    ChildImpl rightNeighbor;
    Lock leftFork;
    Lock rightFork;

    enum Direction {
        LEFT,
        RIGHT
    }

    public ChildImpl(String name, int hungerSpeedMs) {
        super(name, hungerSpeedMs);
    }

    public void startEating(ChildImpl leftNeighbor, ChildImpl rightNeighbor, Lock leftFork, Lock rightFork) {
        lookThroughSurroundings(leftNeighbor, rightNeighbor, leftFork, rightFork);
        new Thread(this::eatLoop).start();
    }

    private void lookThroughSurroundings(ChildImpl leftNeighbor, ChildImpl rightNeighbor, Lock leftFork, Lock rightFork) {
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void eatLoop() {
        while (true) {
            if (tryGetFork(Direction.LEFT)) {
                if (tryGetFork(Direction.RIGHT)) {
                    super.eat();
                    rightFork.unlock();
                }
                leftFork.unlock();
            }
        }
    }

    private boolean tryGetFork(Direction dir) {
        if (dir == Direction.LEFT) {
            if (isCloserToCry(leftNeighbor)) {
                return leftFork.tryLock();
            }
        } else {
            if (isCloserToCry(rightNeighbor)) {
                return rightFork.tryLock();
            }
        }
        return false;
    }

    private boolean isCloserToCry(ChildImpl child) {
        return child.happiness() > super.happiness();
    }
}
