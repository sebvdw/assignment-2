package nl.saxion.parcomp.asign2;

import java.util.Random;

public abstract class BaseVisitor implements Runnable {

    final protected ArtFair artFair;
    final protected String name;
    final private Random random = new Random();
    public BaseVisitor(ArtFair artFair, String name) {
        this.artFair = artFair;
        this.name = name;
    }
    @Override
    public void run() {
        while(true) {
            live();
        }
    }
    //so we do not get too many in the waiting queue
    protected void live() {
        randomTime(30000,random.nextInt(30) * 1000);
    }
    protected void randomTime(int max, int min) {
        try {
            Thread.sleep(min + (random.nextInt((max - min) / 100) * 100) );
        } catch ( InterruptedException e) {

        }
    }
}
