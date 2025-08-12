import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {

    // Total sum of all files
    static long totalSum = 0;
    static Semaphore multiplex;
    static Semaphore mutex = new Semaphore(1);
    static Map<String, List<Long>> fileFingerprints = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

        List<Thread> threadsSum = new ArrayList<>();
        multiplex = new Semaphore(args.length / 2);

        // Calculate the fingerprint for each files
        for (String path : args) {
            Thread thread = new Thread(new FileSum(path));
            threadsSum.add(thread);
            
            multiplex.acquire();
            thread.start();
        }

        for (Thread thread : threadsSum) {
            thread.join();
        }

        List<Thread> threadsSimilarity = new ArrayList<>();
        multiplex = new Semaphore(args.length / 2);

        // Compare each pair of files
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                String file1 = args[i];
                String file2 = args[j];

                List<Long> fingerprint1 = fileFingerprints.get(file1);
                List<Long> fingerprint2 = fileFingerprints.get(file2);
                
                Thread thread = new Thread(new Similarity(fingerprint1, fingerprint2, file1, file2));
                threadsSimilarity.add(thread);

                multiplex.acquire();
                thread.start();
            }
        }

        for (Thread thread : threadsSimilarity) {
            thread.join();
        }

        // Printing totalSum
        System.out.println("Total sum: " + totalSum);
    }
}
