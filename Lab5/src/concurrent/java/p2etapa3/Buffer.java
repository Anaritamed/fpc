package p2etapa3;

import java.util.ArrayList;
import java.util.List;

class Buffer {
    private final List<Integer> data = new ArrayList<>();
    private final int capacity = 10;
    
    public synchronized void put(int value) throws InterruptedException {
        while (this.isFull()) {
            wait();
        }

        data.add(value);
        System.out.println("Inserted: " + value + " | Buffer size: " + data.size());

        notifyAll();
    }
    
    public synchronized int remove() throws InterruptedException {
        while (data.isEmpty()) {
            wait();
        }

        int value = data.remove(0);
        System.out.println("Removed: " + value + " | Buffer size: " + data.size());

        notifyAll();
        return value;
    }

    public synchronized boolean isFull() {
        return data.size() == capacity;
    }
}
