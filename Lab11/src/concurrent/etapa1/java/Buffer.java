import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Buffer {
    private final BlockingQueue<Integer> data = new ArrayBlockingQueue<>(3);
    
    public void put(int value) {
        try {
            data.put(value);
            System.out.println("Inserted: " + value + " | Buffer size: " + data.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int remove() {
        try {
            int value = data.take();
            System.out.println("Removed: " + value + " | Buffer size: " + data.size());
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
