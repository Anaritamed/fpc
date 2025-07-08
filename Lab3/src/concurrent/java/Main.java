import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 5) {
            System.out.println("Use: java Main <num_producers> <max_items_per_producer> <producing_time> <num_consumers> <consuming_time>");
            return;
        }
        
        int numProducers = Integer.parseInt(args[0]);
        int maxItemsPerProducer = Integer.parseInt(args[1]);
        int producingTime = Integer.parseInt(args[2]);
        int numConsumers = Integer.parseInt(args[3]);
        int consumingTime = Integer.parseInt(args[4]);
        
        Buffer buffer = new Buffer();
        Semaphore mutex = new Semaphore(1);
        Semaphore consumerSemaphore = new Semaphore(0);
        Semaphore producerSemaphore = new Semaphore(maxItemsPerProducer);
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 1; i <= numProducers; i++) {
            Producer producer = new Producer(i, buffer, producingTime, mutex, consumerSemaphore, producerSemaphore);
            Thread thread = new Thread(producer);
            threads.add(thread);
        }
        
        int[] qtdDisponivel = { numProducers };
        for (int i = 1; i <= numConsumers; i++) {
            ConsumerPar consumerPar = new ConsumerPar(i, buffer, consumingTime, mutex, consumerSemaphore, producerSemaphore, qtdDisponivel);
            ConsumerImpar consumerImpar = new ConsumerImpar(i, buffer, consumingTime, mutex, consumerSemaphore, producerSemaphore, qtdDisponivel);
            Thread threadPar = new Thread(consumerPar);
            Thread threadImpar = new Thread(consumerImpar);
            threads.add(threadPar);
            threads.add(threadImpar);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
