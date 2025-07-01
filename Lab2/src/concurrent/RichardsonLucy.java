import java.util.Map;

public class RichardsonLucy implements Runnable {

    private final String color;
    private final float[][] image;
    private final float[][] psf;
    private final float[][] psfFlipped;
    private final int iterations;
    private final Map<String, float[][]> colorsRestored;

    public RichardsonLucy(String color, float[][] image, float[][] psf, float[][] psfFlipped, int iterations, Map<String, float[][]> colorsRestored) {
        this.color = color;
        this.image = image;
        this.psf = psf;
        this.psfFlipped = psfFlipped;
        this.iterations = iterations;
        this.colorsRestored = colorsRestored;
    }

    @Override
    public void run() {
        int h = image.length;
        int w = image[0].length;
        float[][] estimate = new float[h][w];

        // Inicializa com valor constante (pode ser a imagem borrada)
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                estimate[y][x] = 0.5f;

        for (int it = 0; it < iterations; it++) {
            float[][] estimateBlurred = convolve(estimate, psf);
            float[][] ratio = new float[h][w];

            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++) {
                    float eb = estimateBlurred[y][x];
                    ratio[y][x] = (eb > 1e-6f) ? image[y][x] / eb : 0f;
                }

            float[][] correction = convolve(ratio, psfFlipped);

            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    estimate[y][x] *= correction[y][x];
        }

        colorsRestored.put(color, estimate);
    }

    public static float[][] convolve(float[][] image, float[][] kernel) {
        int h = image.length, w = image[0].length;
        int kh = kernel.length, kw = kernel[0].length;
        int kyc = kh / 2, kxc = kw / 2;

        float[][] result = new float[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float sum = 0f;
                for (int ky = 0; ky < kh; ky++) {
                    for (int kx = 0; kx < kw; kx++) {
                        int iy = y + ky - kyc;
                        int ix = x + kx - kxc;
                        if (iy >= 0 && iy < h && ix >= 0 && ix < w) {
                            sum += image[iy][ix] * kernel[ky][kx];
                        }
                    }
                }
                result[y][x] = sum;
            }
        }
        return result;
    }
    
}
