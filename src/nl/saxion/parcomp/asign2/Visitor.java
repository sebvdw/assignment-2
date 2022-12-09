package nl.saxion.parcomp.asign2;

public class Visitor extends BaseVisitor {
    public Visitor(ArtFair club, String name) { super(club, name); }

    @Override
    public void run() {
        try {
            artFair.enterVisitorLine(name);
            Thread.sleep(2000);
            artFair.publicExit(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
