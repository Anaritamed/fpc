import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class LogAnalyzer2 {

    static int total200 = 0;
    static int total500 = 0;
    static Semaphore mutex = new Semaphore(1);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        List<Thread> threads = new ArrayList<>();
        for (String fileName : args) {
            Thread thread = new Thread(new Processor(fileName));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

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
