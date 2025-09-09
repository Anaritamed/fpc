import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class LogAnalyzer3f {

    static int total200 = 0;
    static int total500 = 0;
    static Semaphore mutex = new Semaphore(1);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (String fileName : args) {
            Processor processor = new Processor(fileName);
            executor.execute(processor);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Total 200: " + total200);
        System.out.println("Total 500: " + total500);
    }

    public static class Processor implements Runnable {

        private String fileName;

        public Processor(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
            System.out.println("Processando arquivo: " + fileName);
            processFile(fileName);
        }

        public static void processFile(String fileName) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        String code = parts[2];
                        if (code.equals("200")) {
                            mutex.acquire();
                            total200++;
                            mutex.release();
                        } else if (code.equals("500")) {
                            mutex.acquire();
                            total500++;
                            mutex.release();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao ler arquivo: " + fileName);
                e.printStackTrace();
            }
        }
    }
}
