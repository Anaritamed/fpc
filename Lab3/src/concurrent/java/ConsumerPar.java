import java.util.concurrent.Semaphore;

class ConsumerPar implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore mutex;
    private final Semaphore vazio;
    private final Semaphore cheio;
    
    public ConsumerPar(int id, Buffer buffer, int sleepTime, Semaphore mutex, Semaphore vazio, Semaphore cheio) {
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

                if (ehPar(item)) {
                    System.out.println("ConsumerPar " + id + " consumed item " + item);
                    mutex.release();
                    cheio.release();
                }

                if (!ehPar(item)) {
                    System.out.println("ConsumerPar " + id + " tried to consume item " + item);
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

    private boolean ehPar(int item) {
        return item % 2 == 0;
    }
}