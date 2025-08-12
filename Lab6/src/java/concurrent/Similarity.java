import java.util.ArrayList;
import java.util.List;

public class Similarity implements Runnable {

    private List<Long> fingerprint1;
    private List<Long> fingerprint2;
    private String file1;
    private String file2;

    public Similarity(List<Long> fingerprint1, List<Long> fingerprint2, String file1, String file2) {
        this.fingerprint1 = fingerprint1;
        this.fingerprint2 = fingerprint2;
        this.file1 = file1;
        this.file2 = file2;
    }

    private float similarity(List<Long> base, List<Long> target) {
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

    @Override
    public void run() {
        float similarityScore = similarity(this.fingerprint1, this.fingerprint2);
        System.out.println("Similarity between " + this.file1 + " and " + this.file2 + ": " + (similarityScore * 100) + "%");
        Main.multiplex.release();
    }
    
}
