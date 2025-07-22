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
        FileIndexingPipeline.mutexRead.acquire();
        FileData fileData = readBuffer.remove();
        FileIndexingPipeline.mutexRead.release();
        FileIndexingPipeline.readCheio.release();
        
        if (fileData == null) return;
        String[] words = fileData.content.split("\\s+");
        String newContent = String.join(",", words);
        
        FileIndexingPipeline.tokenCheio.acquire();
        FileIndexingPipeline.mutexToken.acquire();
        tokenBuffer.insert(new FileData(fileData.name, newContent));
        FileIndexingPipeline.mutexToken.release();
        FileIndexingPipeline.tokenVazio.release();
    }

}
