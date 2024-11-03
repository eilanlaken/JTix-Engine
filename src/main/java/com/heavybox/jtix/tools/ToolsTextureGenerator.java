package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ToolsTextureGenerator {

    private static       boolean initialized     = false;
    private static final int[]   PERLIN_PERM_256 = new int[256];
    private static final int[]   PERLIN_PERM_512 = new int[512];

    private ToolsTextureGenerator() {}

    private static void init() {
        if (initialized) return;
        for (int i = 0; i < 256; i++) {
            PERLIN_PERM_256[i] = i;
        }
        Collections.shuffle(PERLIN_PERM_256);
        for (int i = 0; i < 256; i++) {
            PERLIN_PERM_512[i] = PERLIN_PERM_256[i];
            PERLIN_PERM_512[256 + i] = PERLIN_PERM_256[i];
        }
        initialized = true;
    }

    /* Noise */

    public static void generateTextureNoisePerlin(int width, int height, final String directory, final String outputName, boolean overrideExistingFile) throws IOException {
        init();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float nx = x / (float) width;
                float ny = y / (float) height;
                float noise = noise(nx * 10, ny * 10, 0);
                int value = (int) ((noise + 1) * 128); // Convert [-1,1] to [0,255]
                int rgb = value | (value << 8) | (value << 16); // Gray color
                image.setRGB(x, y, rgb);
            }
        }

        Assets.saveImage(directory, outputName, image);
    }


    public static void generateTextureNoiseSimplex() {}
    public static void generateTextureNoiseWhite() {}
    public static void generateTextureNoiseValue() {}
    public static void generateTextureNoiseVornoi() {}

    /* Patterns */
    public static void generateTextureCheckers() {}
    public static void generateTextureGradient() {}
    public static void generateTextureWave() {}

    /* Maps */
    // https://codepen.io/BJS3D/pen/YzjXZgV?editors=1010
    // https://github.com/Theverat/NormalmapGenerator/blob/master/src_generators/normalmapgenerator.cpp#L142
    public static void generateTextureMapNormal(final String directory, final String outputName, final String sourcePath, float strength, boolean tiled) {
        // TODO: check if already generated.

        /* get original image */
        BufferedImage imageInput = null;
        try {
            imageInput = ImageIO.read(new File(sourcePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ToolsException("Could not generate normal map: creating " + BufferedImage.class.getSimpleName() + " for the original image failed. Check the path.");
        }

        /* generate intensity map */
        int width = imageInput.getWidth();
        int height = imageInput.getHeight();
        BufferedImage imageIntensityMap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for(int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                int rgb = imageInput.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                int average = (red + green + blue) / 3;
                int invertedAverage = 255 - average;
                int grayscale = (invertedAverage << 16) | (invertedAverage << 8) | invertedAverage; // R, G, B all set to the same average value
                imageIntensityMap.setRGB(x, y, grayscale);
            }
        }

        float strengthInv = 1.0f / strength;
        BufferedImage imageNormalMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {

                final float topLeft      = readPixel(imageIntensityMap, x - 1, y - 1, tiled);
                final float top          = readPixel(imageIntensityMap, x - 1, y, tiled);
                final float topRight     = readPixel(imageIntensityMap, x - 1, y + 1, tiled);
                final float right        = readPixel(imageIntensityMap, x, y + 1, tiled);
                final float bottomRight  = readPixel(imageIntensityMap, x + 1, y + 1, tiled);
                final float bottom       = readPixel(imageIntensityMap, x + 1, y, tiled);
                final float bottomLeft   = readPixel(imageIntensityMap, x + 1, y - 1, tiled);
                final float left         = readPixel(imageIntensityMap, x, y - 1, tiled);

                final float[][] convolution_kernel = {
                    {topLeft,    top,    topRight},
                    {left,       0.0f,   right},
                    {bottomLeft, bottom, bottomRight}
                };

                Vector3 normal = sobel(convolution_kernel, strengthInv);
                // map normal -1..1 to rgb 0..255
                int mappedRed = (int) ((normal.x + 1.0f) * (255.0f / 2.0f));
                int mappedGreen = (int) ((normal.y + 1.0f) * (255.0f / 2.0f));
                int mappedBlue = (int) ((normal.z + 1.0f) * (255.0f / 2.0f));

                // Combine the mapped values into an RGB color
                int rgb = (mappedRed << 16) | (mappedGreen << 8) | mappedBlue;
                // Set the pixel in the normal map
                imageNormalMap.setRGB(x, y, rgb);
            }
        }

        try {
            Assets.saveImage(directory, outputName, imageNormalMap);
        } catch (IOException e) {
            // ignore
        }
    }

    public static void generateTextureMapRoughness() {}
    public static void generateTextureMapMetallic() {}

    public static void generateTextureMapSSAO() {

    }

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10); // t^3 * (6 * t^2 - 16 * t + 10)
    }

    private static float grad(int hash, float x, float y, float z) {
        int h = hash & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private static float noise(float x, float y, float z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= (float) Math.floor(x);
        y -= (float) Math.floor(y);
        z -= (float) Math.floor(z);

        float u = fade(x);
        float v = fade(y);
        float w = fade(z);

        int A = PERLIN_PERM_512[X] + Y, AA = PERLIN_PERM_512[A] + Z, AB = PERLIN_PERM_512[A + 1] + Z;
        int B = PERLIN_PERM_512[X + 1] + Y, BA = PERLIN_PERM_512[B] + Z, BB = PERLIN_PERM_512[B + 1] + Z;

        /* Sometimes it is beneficial to not ask questions. */
        return MathUtils.lerp(w, MathUtils.lerp(v, MathUtils.lerp(u, grad(PERLIN_PERM_512[AA], x, y, z),
                                grad(PERLIN_PERM_512[BA], x - 1, y, z)),
                        MathUtils.lerp(u, grad(PERLIN_PERM_512[AB], x, y - 1, z), grad(PERLIN_PERM_512[BB], x - 1, y - 1, z))),
                MathUtils.lerp(v, MathUtils.lerp(u, grad(PERLIN_PERM_512[AA + 1], x, y, z - 1), grad(PERLIN_PERM_512[BA + 1], x - 1, y, z - 1)), MathUtils.lerp(u, grad(PERLIN_PERM_512[AB + 1], x, y - 1, z - 1), grad(PERLIN_PERM_512[BB + 1], x - 1, y - 1, z - 1))));
    }

    // convolution_kernel is 3x3
    private static Vector3 sobel(final float[][] convolution_kernel, float strengthInv) {
        final float top_side    = convolution_kernel[0][0] + 2.0f * convolution_kernel[0][1] + convolution_kernel[0][2];
        final float bottom_side = convolution_kernel[2][0] + 2.0f * convolution_kernel[2][1] + convolution_kernel[2][2];
        final float right_side  = convolution_kernel[0][2] + 2.0f * convolution_kernel[1][2] + convolution_kernel[2][2];
        final float left_side   = convolution_kernel[0][0] + 2.0f * convolution_kernel[1][0] + convolution_kernel[2][0];

        final float dY = right_side - left_side;
        final float dX = bottom_side - top_side;
        final float dZ = strengthInv;

        return new Vector3(dX, dY, dZ).nor();
    }

    private static float readPixel(final BufferedImage img, int x, int y, boolean tiled) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (x >= width) {
            x = tiled ? width - x : width - 1;
        } else if (x < 0) {
            x = tiled ? width + x : 0;
        }

        if (y >= height) {
            y = tiled ? height - y : height - 1;
        } else if (y < 0) {
            y = tiled ? height + y : 0;
        }

        return (img.getRGB(x, y) & 0xFF) / 255f;
    }

}
