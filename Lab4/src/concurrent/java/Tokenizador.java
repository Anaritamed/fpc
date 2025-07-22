public class Tokenizador implements Runnable {

    private Buffer<FileData> readBuffer;
    private Buffer<FileData> tokenBuffer;

    public Tokenizador(Buffer<FileData> readBuffer, Buffer<FileData> tokenBuffer) {
        this.readBuffer = readBuffer;
        this.tokenBuffer = tokenBuffer;
    }
    
    @Override
    public void run() {
        try {
            tokenize(this.readBuffer, this.tokenBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void tokenize(Buffer<FileData> readBuffer, Buffer<FileData> tokenBuffer) throws Exception {
        FileIndexingPipeline.readVazio.acquire();
        FileIndexingPipeline.mutexBuffer.acquire();
        FileData fileData = readBuffer.remove();
        FileIndexingPipeline.mutexBuffer.release();
        FileIndexingPipeline.readCheio.release();
        
        if (fileData == null) return;
        String[] words = fileData.content.split("\\s+");
        String newContent = String.join(",", words);
        
        FileIndexingPipeline.tokenCheio.acquire();
        FileIndexingPipeline.mutexBuffer.acquire();
        tokenBuffer.insert(new FileData(fileData.name, newContent));
        FileIndexingPipeline.mutexBuffer.release();
        FileIndexingPipeline.tokenVazio.release();
    }

}
