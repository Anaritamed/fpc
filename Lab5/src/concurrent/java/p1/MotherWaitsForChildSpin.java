package p1;

public class MotherWaitsForChildSpin {

    private static volatile boolean done = false;

    public static void main(String[] args) {

		Runnable childTask = () -> {
            System.out.println("child");
            done = true;
        };

        System.out.println("mother : begin");
        Thread childThread = new Thread(childTask, "child-thread");
        childThread.start();
  
        while (!done) {
            continue;
        }

        System.out.println("mother: end");
    }
}
