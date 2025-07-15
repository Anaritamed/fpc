import java.util.concurrent.Semaphore;

class Producer implements Runnable {
    private final Buffer buffer;
    private final int maxItems;
    private final int sleepTime;
    private final int id;
    private final Semaphore mutex;
    private final Semaphore vazio;
    private final Semaphore cheio;
    
    public Producer(int id, Buffer buffer, int maxItems, int sleepTime, Semaphore mutex, Semaphore vazio, Semaphore cheio) {
        this.id = id;
        this.buffer = buffer;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
        this.mutex = mutex;
        this.vazio = vazio;
        this.cheio = cheio;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < maxItems; i++) {
            try {
                Thread.sleep(sleepTime);
                int item = (int) (Math.random() * 100);
                
                cheio.acquire();
                mutex.acquire();
                System.out.println("Producer " + id + " produced item " + item);
                buffer.put(item);
                mutex.release();
                vazio.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
