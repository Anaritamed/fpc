import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
            Integer value = data.poll(600, TimeUnit.MILLISECONDS);
            if (value == null) {
                return -1;
            }
            System.out.println("Removed: " + value + " | Buffer size: " + data.size());
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
