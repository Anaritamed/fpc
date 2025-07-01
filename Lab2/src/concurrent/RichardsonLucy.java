public class RichardsonLucy implements Runnable {

    private final float[][] image;
    private final float[][] estimate;
    private final float[][] psf;
    private final float[][] psfFlipped;
    private final int h;
    private final int w;

    public RichardsonLucy(float[][] image, float[][] estimate, float[][] psf, float[][] psfFlipped, int h, int w) {
        this.image = image;
        this.estimate = estimate;
        this.psf = psf;
        this.psfFlipped = psfFlipped;
        this.h = h;
        this.w = w;
    }

    @Override
    public void run() {
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
