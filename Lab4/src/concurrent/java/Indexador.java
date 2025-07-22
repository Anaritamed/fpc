import java.util.HashMap;
import java.util.Map;

public class Indexador implements Runnable {

    private Buffer<FileData> tokenBuffer;

    public Indexador(Buffer<FileData> tokenBuffer) {
        this.tokenBuffer = tokenBuffer;
    }
    
    @Override
    public void run() {
        try {
            index(this.tokenBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void index(Buffer<FileData> tokenBuffer) throws Exception {
        FileIndexingPipeline.tokenVazio.acquire();
        FileIndexingPipeline.mutexBuffer.acquire();
        FileData fileData = tokenBuffer.remove();
        FileIndexingPipeline.mutexBuffer.release();
        FileIndexingPipeline.tokenCheio.release();
        
        if (fileData == null) return;
        String[] words = fileData.content.split(",");
        for (String word : words) {
            FileIndexingPipeline.mutexFile.acquire();
            
            FileIndexingPipeline.fileIndex.putIfAbsent(word, new HashMap<>());
            Map<String, Integer> fileDatas = FileIndexingPipeline.fileIndex.get(word);
            fileDatas.put(fileData.name, fileDatas.getOrDefault(fileData.name, 0) + 1);
            
            FileIndexingPipeline.mutexFile.release();
        }
    }

}
