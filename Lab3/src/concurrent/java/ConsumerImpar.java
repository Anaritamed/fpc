import java.util.concurrent.Semaphore;

class ConsumerImpar implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore mutex;
    private final Semaphore vazio;
    private final Semaphore cheio;
    
    public ConsumerImpar(int id, Buffer buffer, int sleepTime, Semaphore mutex, Semaphore vazio, Semaphore cheio) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.mutex = mutex;
        this.vazio = vazio;
        this.cheio = cheio;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                vazio.acquire();
                
                mutex.acquire();
                int item = buffer.remove();

                if (ehImpar(item)) {
                    System.out.println("ConsumerImpar " + id + " consumed item " + item);
                    mutex.release();
                    cheio.release();
                }

                if (!ehImpar(item)) {
                    System.out.println("ConsumerImpar " + id + " tried to consume item " + item);
                    buffer.put(item);
                    mutex.release();
                    vazio.release();
                }
    
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean ehImpar(int item) {
        return item % 2 != 0;
    }
}