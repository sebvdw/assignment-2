package nl.saxion.parcomp.asign2;

import java.util.Scanner;

public class ArtWorld {
    // Current understanding: I need to get MULTIPLE visitors going in and out
    // Managing when or how long they stay for must either be decided by a guard or by themselves.

    private final int sizeOfTheClub = 5, nrOfRepresentatives = 7, nrOfVisitors = 10, consecutiveRepresentatives = 3;

    public static void main(String[] args) throws InterruptedException {
        new ArtWorld().startWorld();

    }
    public void startWorld() throws InterruptedException {
        ArtFair artFair = new ArtFair(sizeOfTheClub, consecutiveRepresentatives);


        for (int i = 0; i < nrOfVisitors; i++) {
            new Thread(new Visitor(artFair,"Visitor " + i),"Visitor" + i ).start();
        }
        for (int i = 0; i < nrOfRepresentatives; i++) {
            new Thread(new VipVisitor(artFair,"VipVisitor " + i),"VipVisitor" + i ).start();
        }

        for (int i = 0; i < nrOfVisitors; i++) {
            new Thread(new Visitor(artFair,"Visitor NEW " + i),"Visitor" + i ).start();
        }
        for (int i = 0; i < nrOfRepresentatives; i++) {
            new Thread(new VipVisitor(artFair,"VipVisitor NEW " + i),"VipVisitor" + i ).start();
        }

        Thread.sleep(30000);
        System.out.println("NEWWWW VIISSSIIITTOOORSS COMMMINNNNGGG");
        for (int i = 0; i < nrOfVisitors; i++) {
            new Thread(new Visitor(artFair,"Visitor NEW NEW" + i),"Visitor" + i ).start();
        }

        //System.out.flush();
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.exit(0);

    }

}
