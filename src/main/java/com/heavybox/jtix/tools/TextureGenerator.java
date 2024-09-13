package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.collections.CollectionsUtils;
import com.heavybox.jtix.math.MathUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

public final class TextureGenerator {

    private static       boolean initialized     = false;
    private static final int[]   PERLIN_PERM_256 = new int[256];
    private static final int[]   PERLIN_PERM_512 = new int[512];

    private TextureGenerator() {}

    private static void init() {
        if (initialized) return;
        for (int i = 0; i < 256; i++) {
            PERLIN_PERM_256[i] = i;
        }
        CollectionsUtils.shuffle(PERLIN_PERM_256);
        for (int i = 0; i < 256; i++) {
            PERLIN_PERM_512[i] = PERLIN_PERM_256[i];
            PERLIN_PERM_512[256 + i] = PERLIN_PERM_256[i];
        }
        initialized = true;
    }

    /* Noise */

    public static void buildTextureNoisePerlin(int width, int height, final String directory, final String fileName, boolean overrideExistingFile) throws IOException {
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

        AssetUtils.saveImage(directory, fileName, image);
    }


    public static void buildTextureNoiseSimplex() {}
    public static void buildTextureNoiseWhite() {}
    public static void buildTextureNoiseValue() {}
    public static void buildTextureNoiseVornoi() {}

    /* Patterns */
    public static void buildTextureCheckers() {}
    public static void buildTextureGradient() {}
    public static void buildTextureWave() {}

    /* Maps */
    // https://codepen.io/BJS3D/pen/YzjXZgV?editors=1010
    public static void buildTextureMapNormal() {}
    public static void buildTextureMapRoughness() {}
    public static void buildTextureMapMetallic() {}

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

}
