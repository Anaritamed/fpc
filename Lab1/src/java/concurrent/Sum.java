import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Sum implements Runnable {

    private String path;

    public Sum(String path) {
        this.path = path;
    }

    public static int sum(FileInputStream fis) throws IOException {
        
	    int byteRead;
        int sum = 0;
        
        while ((byteRead = fis.read()) != -1) {
        	sum += byteRead;
        }

        return sum;
    }

    public static long sum(String path) throws IOException {

        Path filePath = Paths.get(path);
        if (Files.isRegularFile(filePath)) {
       	    FileInputStream fis = new FileInputStream(filePath.toString());
            return sum(fis);
        } else {
            throw new RuntimeException("Non-regular file: " + path);
        }
    }

    @Override
    public void run() {
        long sum = 0;
        try {
            sum = sum(this.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(this.path + " : " + sum);
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

	    List<Thread> threads = new ArrayList<>();
        for (String path : args) {
            Sum sum = new Sum(path);
            Thread thread = new Thread(sum, "thread-sum");
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
