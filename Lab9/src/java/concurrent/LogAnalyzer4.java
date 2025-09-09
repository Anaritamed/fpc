import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogAnalyzer4 {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<int[]>> totaisArquivos = new ArrayList<>();

        for (String fileName : args) {
            Processor processor = new Processor(fileName);
            Future<int[]> totais = executor.submit(processor);
            totaisArquivos.add(totais);
        }

        executor.shutdown();

        int total200 = 0;
        int total500 = 0;

        for (Future<int[]> totais : totaisArquivos) {
            total200 += totais.get()[0];
            total500 += totais.get()[1];
        }

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Total 200: " + total200);
        System.out.println("Total 500: " + total500);
    }

    public static class Processor implements Callable<int[]> {

        private String fileName;

        public Processor(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public int[] call() {
            System.out.println("Processando arquivo: " + fileName);
            int[] totais = new int[2];
            processFile(fileName, totais);
            return totais;
        }

        public static void processFile(String fileName, int[] totais) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");
                    if (parts.length == 3) {
                        String code = parts[2];
                        if (code.equals("200")) {
                            totais[0]++;
                        } else if (code.equals("500")) {
                            totais[1]++;
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
