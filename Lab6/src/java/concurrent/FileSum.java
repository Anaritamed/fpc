import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class FileSum implements Runnable {

    private String path;

    public FileSum(String path) {
        this.path = path;
    }

    private List<Long> fileSum(String filePath) throws Exception {
        File file = new File(filePath);
        List<Long> chunks = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[100];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                long sum = sum(buffer, bytesRead);
                chunks.add(sum);

                Main.mutex.acquire();
                Main.totalSum += sum;
                Main.mutex.release();
            }
        }
        return chunks;
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
            List<Long> fingerprint = fileSum(this.path);
            Main.mutex.acquire();
            Main.fileFingerprints.put(this.path, fingerprint);
            Main.mutex.release();
            Main.multiplex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
