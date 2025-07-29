package p2etapa3;

class Producer implements Runnable {
    private final Buffer buffer;
    private final int maxItems;
    private final int sleepTime;
    private final int id;
    
    public Producer(int id, Buffer buffer, int maxItems, int sleepTime) {
        this.id = id;
        this.buffer = buffer;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < maxItems; i++) {
            try {
                Thread.sleep(sleepTime);

                synchronized (Main.cheio) {
                    while (buffer.isFull()) {
                        Main.cheio.wait();
                    }
                }

                int item = (int) (Math.random() * 100);
                System.out.println("Producer " + id + " produced item " + item);

                synchronized (Main.vazio) {
                    Main.vazio.notify();
                }

                buffer.put(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
