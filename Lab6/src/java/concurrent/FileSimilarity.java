import java.util.*;
import java.util.concurrent.Semaphore;

public class FileSimilarity {

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

        List<Thread> threads = new ArrayList<>();
        multiplex = new Semaphore(args.length / 2);

        // Calculate the fingerprint for each files
        for (String path : args) {
            Thread thread = new Thread(new FileSum(path));
            threads.add(thread);
        }

        for (Thread thread : threads) {
            multiplex.acquire();
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Compare each pair of files
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                String file1 = args[i];
                String file2 = args[j];
                List<Long> fingerprint1 = fileFingerprints.get(file1);
                List<Long> fingerprint2 = fileFingerprints.get(file2);
                float similarityScore = similarity(fingerprint1, fingerprint2);
                System.out.println("Similarity between " + file1 + " and " + file2 + ": " + (similarityScore * 100) + "%");
            }
        }

        // Printing totalSum
        System.out.println("Total sum: " + totalSum);
    }

    private static float similarity(List<Long> base, List<Long> target) {
        int counter = 0;
        List<Long> targetCopy = new ArrayList<>(target);

        for (Long value : base) {
            if (targetCopy.contains(value)) {
                counter++;
                targetCopy.remove(value);
            }
        }

        return (float) counter / base.size();
    }
}
