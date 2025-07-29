package p2etapa3;

class Consumer implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    
    public Consumer(int id, Buffer buffer, int sleepTime) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                synchronized (Main.vazio) {
                    while (buffer.isEmpty()) {
                        Main.vazio.wait();
                    }
                }
                
                int item = buffer.remove();
                if (item == -1) break;
                System.out.println("Consumer " + id + " consumed item " + item);

                synchronized (Main.cheio) {
                    Main.cheio.notify();
                }

                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}