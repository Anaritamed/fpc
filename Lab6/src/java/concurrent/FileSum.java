import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSum implements Runnable {

    private String filePath;

    public FileSum(String filePath) {
        this.filePath = filePath;
    }
    
    private void fileSum(String filePath) throws Exception {
        File file = new File(filePath);
        List<Long> chunks = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[100];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                long sum = sum(buffer, bytesRead);
                chunks.add(sum);

                FileSimilarity.mutex.acquire();
                FileSimilarity.totalSum += sum;
                FileSimilarity.mutex.release();
            }
        }
        FileSimilarity.mutex.acquire();
        FileSimilarity.fileFingerprints.put(filePath, chunks);
        FileSimilarity.mutex.release();
        FileSimilarity.multiplex.release();
    }

    private long sum(byte[] buffer, int length) {
        long sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Byte.toUnsignedInt(buffer[i]);
        }
        return sum;
    }

    @Override
    public void run() {
        try {
            fileSum(this.filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
