import java.util.*;
import java.util.concurrent.Semaphore;

public class FileIndexingPipeline {

    static Map<String, Map<String, Integer>> fileIndex = new HashMap<>();
    static Semaphore mutexFile = new Semaphore(1);
    static Semaphore mutexRead = new Semaphore(1);
    static Semaphore mutexToken = new Semaphore(1);
    static Semaphore readVazio = new Semaphore(0);
    static Semaphore tokenVazio = new Semaphore(0);
    static Semaphore readCheio = new Semaphore(50);
    static Semaphore tokenCheio = new Semaphore(50);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java IndexadorPipeline <arquivo1.txt> <arquivo2.txt> ...");
            return;
        }

        Buffer<FileData> readBuffer = new Buffer<>();
        Buffer<FileData> tokenBuffer = new Buffer<>();

        List<Thread> threads = new ArrayList<>();

        for (String pathStr : args) {
            
            // Etapa 1: Leitura do arquivo
            Leitor leitor = new Leitor(pathStr, readBuffer);
            threads.add(new Thread(leitor));
            
            // Etapa 2: Tokenização
            Tokenizador tokenizador = new Tokenizador(readBuffer, tokenBuffer);
            threads.add(new Thread(tokenizador));

            // Etapa 3: Indexação
            Indexador indexador = new Indexador(tokenBuffer);
            threads.add(new Thread(indexador));            
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("fileIndex:");
        for (var word : fileIndex.keySet()) {
            System.out.println(word + " -> " + fileIndex.get(word));
        }
    }

}
