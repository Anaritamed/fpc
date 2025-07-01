import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class Deconvolution {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Uso: java Deconvolution <imagem_borrada_path>");
            return;
        }

        String imagePath = args[0];
        BufferedImage input = ImageIO.read(new File(imagePath));
        int width = input.getWidth();
        int height = input.getHeight();

        // Separa canais R, G, B
        float[][] red = new float[height][width];
        float[][] green = new float[height][width];
        float[][] blue = new float[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(input.getRGB(x, y));
                red[y][x] = c.getRed() / 255f;
                green[y][x] = c.getGreen() / 255f;
                blue[y][x] = c.getBlue() / 255f;
            }
        }

        // Kernel Gaussiano (ajustável conforme o tipo de desfoque)
        float[][] psf = gaussianKernel(9, 2.0f);
        float[][] psfFlipped = invertKernel(psf);

        // Aplica Richardson-Lucy por canal
        int iterations = 15;
        Map<String, float[][]> colorsRestored = new HashMap<>();
        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(new RichardsonLucy("red", red, psf, psfFlipped, iterations, colorsRestored)));
        threads.add(new Thread(new RichardsonLucy("green", green, psf, psfFlipped, iterations, colorsRestored)));
        threads.add(new Thread(new RichardsonLucy("blue", blue, psf, psfFlipped, iterations, colorsRestored)));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Salva imagem restaurada
        saveColorImage(colorsRestored.get("red"), colorsRestored.get("green"), colorsRestored.get("blue"), "restaurada.png");
        System.out.println("Imagem restaurada salva como restaurada.png");
    }

    // Espelha o kernel horizontal e verticalmente
    public static float[][] invertKernel(float[][] kernel) {
        int h = kernel.length, w = kernel[0].length;
        float[][] result = new float[h][w];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                result[y][x] = kernel[h - y - 1][w - x - 1];
        return result;
    }

    // Kernel Gaussiano normalizado
    public static float[][] gaussianKernel(int size, float sigma) {
        float[][] kernel = new float[size][size];
        float mean = size / 2f;
        float sum = 0f;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float val = (float) Math.exp(-0.5 * (
                    Math.pow((x - mean) / sigma, 2) +
                    Math.pow((y - mean) / sigma, 2)));
                kernel[y][x] = val;
                sum += val;
            }
        }

        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                kernel[y][x] /= sum;

        return kernel;
    }

    // Salva a imagem RGB em PNG
    public static void saveColorImage(float[][] r, float[][] g, float[][] b, String filename) throws Exception {
        int h = r.length, w = r[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int red = clampToByte(r[y][x] * 255f);
                int green = clampToByte(g[y][x] * 255f);
                int blue = clampToByte(b[y][x] * 255f);
                Color color = new Color(red, green, blue);
                out.setRGB(x, y, color.getRGB());
            }
        }

        ImageIO.write(out, "png", new File(filename));
    }

    private static int clampToByte(float val) {
        return Math.min(255, Math.max(0, Math.round(val)));
    }
}

