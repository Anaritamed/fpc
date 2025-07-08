import java.util.concurrent.Semaphore;

class ConsumerPar implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore mutex;
    private final Semaphore consumerSemaphore;
    private final Semaphore producerSemaphore;
    private final int[] qtdDisponivel;
    
    public ConsumerPar(int id, Buffer buffer, int sleepTime, Semaphore mutex, Semaphore consumerSemaphore, Semaphore producerSemaphore, int[] qtdDisponivel) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.mutex = mutex;
        this.consumerSemaphore = consumerSemaphore;
        this.producerSemaphore = producerSemaphore;
        this.qtdDisponivel = qtdDisponivel;
    }
    
    @Override
    public void run() {
        try {
            if (qtdDisponivel[0] > 0) {
                consumerSemaphore.acquire();
            }
            
            mutex.acquire();
            int item = buffer.remove();
            if (item == -1) return;
            if (!ehPar(item)) {
                System.out.println("ConsumerPar " + id + " tried to consume item " + item);
                buffer.put(item);
                consumerSemaphore.release();
            }
            if (ehPar(item)) {
                producerSemaphore.release();
                System.out.println("ConsumerPar " + id + " consumed item " + item);
                qtdDisponivel[0]--;
            }
            mutex.release();

            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean ehPar(int item) {
        return item % 2 == 0;
    }
}