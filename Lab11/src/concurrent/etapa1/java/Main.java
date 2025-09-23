import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Use: java Main <num_producers> <producing_time> <num_consumers> <consuming_time>");
            return;
        }
        
        int numProducers = Integer.parseInt(args[0]);
        int producingTime = Integer.parseInt(args[1]);
        int numConsumers = Integer.parseInt(args[2]);
        int consumingTime = Integer.parseInt(args[3]);
        
        Buffer buffer = new Buffer();
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 1; i <= numProducers; i++) {
            Producer producer = new Producer(i, buffer, producingTime);
            Thread thread = new Thread(producer);
            threads.add(thread);
        }

        for (int i = 1; i <= numConsumers; i++) {
            Consumer consumer = new Consumer(i, buffer, consumingTime);
            Thread thread = new Thread(consumer);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
