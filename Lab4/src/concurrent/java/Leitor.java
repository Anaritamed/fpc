import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Leitor implements Runnable {

    private String pathStr;
    private Buffer<FileData> readBuffer;
    private boolean success;

    public Leitor(String pathStr, Buffer<FileData> readBuffer) {
        this.pathStr = pathStr;
        this.readBuffer = readBuffer;
    }
    
    @Override
    public void run() {
        try {
            this.success = readFile(this.pathStr, this.readBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean readFile(String pathStr, Buffer<FileData> readBuffer) {
        try {
            Path path = Paths.get(pathStr);
            String content = Files.readString(path);

            FileIndexingPipeline.readCheio.acquire();
            FileIndexingPipeline.mutexRead.acquire();
            readBuffer.insert(new FileData(path.getFileName().toString(), content));
            FileIndexingPipeline.mutexRead.release();
            FileIndexingPipeline.readVazio.release();

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao ler arquivo " + pathStr + ": " + e.getMessage());
            return false;
        }
    }
        
    public boolean isSuccess() {
        return success;
    }
    
}
