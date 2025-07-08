import java.util.concurrent.Semaphore;

class Producer implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore mutex;
    private final Semaphore consumerSemaphore;
    private final Semaphore producerSemaphore;
    
    public Producer(int id, Buffer buffer, int sleepTime, Semaphore mutex, Semaphore consumerSemaphore, Semaphore producerSemaphore) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.mutex = mutex;
        this.consumerSemaphore = consumerSemaphore;
        this.producerSemaphore = producerSemaphore;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(sleepTime);
            int item = (int) (Math.random() * 100);
            System.out.println("Producer " + id + " produced item " + item);

            producerSemaphore.acquire();
            mutex.acquire();
            buffer.put(item);
            mutex.release();
            consumerSemaphore.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
