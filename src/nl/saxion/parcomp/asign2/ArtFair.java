package nl.saxion.parcomp.asign2;

import java.util.concurrent.locks.*;


// WHERE IVE LEFT OFF:
// CURRENTLY I just got it to work to allow vips in again after all the waiting visitors have entered
// and left. Another thing to take into account is only one VIP visiting at a time.
public class ArtFair {

    // This will run for each individual thread working.
    // An external thread will manage the synchronization of the entire project.
    private final int maxVisitors, consecutiveVips;
    private Lock lock;
    private Condition spaceAvailable, finished, lowPriority, highPriority, vipVisiting, consecVips;

    public ArtFair(int maxVisitors, int consecutiveVips) {
        this.maxVisitors = maxVisitors;
        this.consecutiveVips = consecutiveVips;

        //Establishing fair lock to ensure that the longest wait time will go in first.
        lock = new ReentrantLock(true);
        spaceAvailable = lock.newCondition();
        lowPriority = lock.newCondition();
        highPriority = lock.newCondition();
        vipVisiting = lock.newCondition();
        finished = lock.newCondition();
        consecVips = lock.newCondition();
        isPriority = false;
    }

    private int highPriorityQueue, lowPriorityQueue, nrOfVisitors = 0, vipCounter = 0, numberOfVisitors, numberOfVips,newWaitingVisitors;
    private boolean isVisiting, disallowNewVisitors;
    private static boolean isPriority, vipInside;

    private boolean noSpaceAvailable() {
        return nrOfVisitors == maxVisitors || vipInside;
    };
    private boolean highPriorityWaiting() {
        return highPriorityQueue > 0;
    };
    private boolean consecutiveVips() {
        return vipCounter >= consecutiveVips;
    };
    private boolean lowPriorQueueEmpty() {return lowPriorityQueue == 0; };
    private boolean fairIsHalfFull() {
        return maxVisitors/2 < nrOfVisitors;
    }
    private boolean maxVips() { return numberOfVips == 1; }
    private boolean maxVisitors() {return numberOfVisitors == 5;}
    private boolean newVisitorsWaiting() {return newWaitingVisitors > 0;}


    public void enterVipLine(String name) throws InterruptedException {
        lock.lock();
        try {
            highPriorityQueue++;
            isPriority = true;
            disallowNewVisitors = true;
            while(noSpaceAvailable() || fairIsHalfFull() || maxVips()) {
                spaceAvailable.await();
            }
            numberOfVips++;
            while(!isPriority) {
                highPriority.await();
            }
            while(vipInside) {
                vipVisiting.await();
            }

            highPriorityQueue--;
            vipInside = true;
            visitFair(name);
        }finally {
            lock.unlock();
        }
    }
    public void enterVisitorLine(String name) throws InterruptedException {
        lock.lock();
        try {
            lowPriorityQueue++;
            newWaitingVisitors++;
            while(highPriorityWaiting() && disallowNewVisitors) {
                consecVips.await();
            }
            newWaitingVisitors--;
            while(noSpaceAvailable() || maxVisitors()) {
                spaceAvailable.await();
            }
            numberOfVisitors++;
            while(isPriority) {
                lowPriority.await();
            }
            lowPriorityQueue--;
            visitFair(name);
        }finally {
            lock.unlock();
        }
    }


    public void visitFair(String name) throws InterruptedException {
        lock.lock();
        try {
            // Cheat sheet:
            // || -> one value is true, all are true
            // && -> one value is false, all are false

            // TRY creating two more functions and splitting VIPS and VISITORS into
            // two different queues

            // Current issue to solve:
            // - If nothing but VIPS enter. Do a check for this.
            // - If Nothing but VISITORS enter. Do a check for this.
            // - If new VISITORS enter the queue at a later time, they need to wait. How?
            // - If all visitors and vips are done, priority needs to be made true;

            //TODO: Include some asserts.

            // ALL enter
            nrOfVisitors++;
            isVisiting = true;
            System.out.println(name+ " visiting");
            //When not visiting, wait till finished.
            while(!isVisiting) {
                finished.await();
            }
            // EXIT single
        } finally {
            lock.unlock();
        }
    }

    public void privateExit(String name) {
        lock.lock();
        try {
            System.out.println(name+ " exiting");
            numberOfVips--;
            nrOfVisitors--;
            vipCounter++;
            isVisiting = false;
            vipInside = false;
            vipVisiting.signal();
            spaceAvailable.signalAll();
            finished.signal();
            if(!highPriorityWaiting()) {
                isPriority = false;
                lowPriority.signalAll();
            }
            if(consecutiveVips()) {
                vipCounter = 0;
                isPriority = false;
                lowPriority.signalAll();
                if(newVisitorsWaiting()) {
                    disallowNewVisitors = false;
                    consecVips.signalAll();
                }
            }
            if(lowPriorQueueEmpty()) {
                vipCounter = 0;
                isPriority = true;
                highPriority.signalAll();
            }

        } finally {
            lock.unlock();
        }
    }

    public void publicExit(String name) {
        lock.lock();
        try {
            System.out.println(name+ " exiting");
            numberOfVisitors--;
            nrOfVisitors--;
            isVisiting = false;
            spaceAvailable.signalAll();
            finished.signal();
            //All current visitors need to go through.
            if(!newVisitorsWaiting() && !lowPriorQueueEmpty()) {
                isPriority = false;
                lowPriority.signalAll();
            }
            if(newVisitorsWaiting() && highPriorityWaiting() || lowPriorQueueEmpty()) {
                disallowNewVisitors = true;
                isPriority = true;
                highPriority.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}