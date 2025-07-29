package p2etapa3;

import java.util.ArrayList;
import java.util.List;

class Buffer {
    private final List<Integer> data = new ArrayList<>();
    
    synchronized public void put(int value) {
        data.add(value);
        System.out.println("Inserted: " + value + " | Buffer size: " + data.size()); 
    }
    
    synchronized public int remove() {
        if (!data.isEmpty()) {
            int value = data.remove(0);
            System.out.println("Removed: " + value + " | Buffer size: " + data.size());
            return value;
        }
        return -1;
    }

    synchronized public boolean isEmpty() {
        return this.data.isEmpty();
    }

    synchronized public boolean isFull() {
        return this.data.size() == 10;
    }
}
