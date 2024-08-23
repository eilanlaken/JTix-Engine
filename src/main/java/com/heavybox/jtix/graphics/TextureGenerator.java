package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.CollectionsUtils;
import com.heavybox.jtix.math.MathUtils;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

// TODO: implement
public final class TextureGenerator {

    private static boolean initialized = false;

    private static final int[] PERMUTATION = new int[256];
    private static final int[] p           = new int[512];

    private TextureGenerator() {}

    private static void init() {
        if (initialized) return;
        for (int i = 0; i < 256; i++) {
            PERMUTATION[i] = i;
        }
        CollectionsUtils.shuffle(PERMUTATION);
        for (int i = 0; i < 256; i++) {
            p[i] = PERMUTATION[i];
            p[256 + i] = PERMUTATION[i];
        }
        initialized = true;
    }

    /* Noise */

    public static void generateTextureNoisePerlin(int width, int height, final String directory, final String fileName, boolean overrideExistingFile) throws IOException {
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
    public static void generateTextureMapNormal() {}
    public static void generateTextureMapRoughness() {}
    public static void generateTextureMapMetallic() {}

    /* Texture Packs */
    public static void generateTexturePack(final String directory, final String outputDirectory, final String outputName, final boolean recursive) {
        if (directory == null) throw new IllegalArgumentException("Must provide non-null directory name.");
        if (!AssetUtils.directoryExists(directory)) throw new IllegalArgumentException("The provided path: " + directory + " does not exist, or is not a directory");
        // TODO: ...
    }

    public static void generateTexturePack(String outputDirectory, String outputName, int extrude, int padding, TexturePackSize maxTexturesSize, final String ...texturePaths) throws IOException {
        /* check if TexturePack was already generated and updated using the same options and input textures. */
        if (alreadyPacked(outputDirectory, outputName, extrude, padding, maxTexturesSize, texturePaths)) return;

        Array<PackedRegionData> regionsData = new Array<>(texturePaths.length);
        for (String texturePath : texturePaths) {
            File sourceImageFile = new File(texturePath);
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            PackedRegionData regionData = getPackedRegionData(texturePath, sourceImage);
            if (regionData.packedWidth > maxTexturesSize.value || regionData.packedHeight > maxTexturesSize.value)
                throw new IOException("Input texture file: " + regionData.name + " cannot be packed - it's dimensions are bigger than the allowed maximum: width = " + regionData.packedWidth + ", height: " + regionData.packedHeight + ", maximum: " + maxTexturesSize.value + ".");
            regionsData.add(regionData);
        }
        regionsData.sort();
        Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack = new HashMap<>();
        int index = 0;
        while (regionsData.size > 0) {
            int last = regionsData.size - 1;
            while (!pack(outputDirectory, outputName, extrude, padding, maxTexturesSize, texturePack, regionsData, last, index)) last--;
            index++;
        }

        /* Generate Texture Pack Yaml File */
        TextureData[] texturesData = new TextureData[texturePack.size()];
        int i = 0;
        for (IndexedBufferedImage img : texturePack.keySet()) {
            texturesData[i] = new TextureData();
            texturesData[i].file = outputName + "_" + img.index + ".png";
            texturesData[i].width = img.getWidth();
            texturesData[i].height = img.getHeight();
            i++;
        }
        Map<String, Object> optionsData = new HashMap<>();
        optionsData.put("extrude", extrude);
        optionsData.put("padding", padding);
        optionsData.put("maxTexturesSize", maxTexturesSize.value);
        Array<PackedRegionData> allRegions = new Array<>();
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            allRegions.addAll(imageRegions.getValue());
        }
        allRegions.pack();
        Map<String, Object> yamlData = new HashMap<>();
        yamlData.put("regions", allRegions.items);
        yamlData.put("options", optionsData);
        yamlData.put("textures", texturesData);
        String content = AssetUtils.yaml.dump(yamlData);
        try {
            AssetUtils.saveFile(outputDirectory, outputName + ".yml", content);
        } catch (Exception e) {
            throw new GraphicsException("Could not save texture pack data file. Exception: " + e.getMessage());
        }

        /* Generate Texture Pack Images */
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            IndexedBufferedImage texturePackImage = imageRegions.getKey();
            Graphics2D graphics = texturePackImage.createGraphics();
            for (PackedRegionData region : imageRegions.getValue()) {
                File sourceImageFile = new File(region.name);
                BufferedImage sourceImage = ImageIO.read(sourceImageFile);
                // copy non-transparent region
                for (int y = region.y; y < region.y + region.packedHeight; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude up
                for (int y = region.y - extrude; y < region.y; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, 0);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude down
                for (int y = region.y + region.packedHeight; y < region.y + region.packedHeight + extrude; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, region.originalHeight - 1);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude left
                for (int x = region.x - extrude; x < region.x; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = sourceImage.getRGB(0, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude right
                for (int x = region.x + region.packedWidth; x < region.x + region.packedWidth + extrude; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = sourceImage.getRGB(region.originalWidth - 1, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
            }
            AssetUtils.saveImage(outputDirectory, outputName + "_" + texturePackImage.index, texturePackImage);
            graphics.dispose();
        }
    }

    private static boolean alreadyPacked(String outputDirectory, String outputName, int extrude, int padding, TexturePackSize maxTexturesSize, final String ...texturePaths) {
        // check if the output directory or the texture map file is missing
        final String mapPath = outputDirectory + File.separator + outputName + ".yml";
        if (!AssetUtils.fileExists(mapPath)) {
            return false;
        }

        // if we did find the map file, check for the presence of all required textures
        String contents = AssetUtils.getFileContent(mapPath);
        Map<String, Object> yamlData = AssetUtils.yaml.load(contents);

        try {
            ArrayList<LinkedHashMap<String, Object>> regionsData = (ArrayList<LinkedHashMap<String, Object>>) yamlData.get("regions");
            Set<String> packingNow = new HashSet<>(Arrays.asList(texturePaths));

            Set<String> packedAlready = new HashSet<>();
            for (LinkedHashMap<String, Object> regionData : regionsData) {
                packedAlready.add((String) regionData.get("name"));
            }

            /* if we are packing different textures, we must run the packer again. */
            if (!packingNow.equals(packedAlready)) return false;

            /* if we are packing the same textures, but one or more of the source textures was modified after our last
            packing, we need to pack again. */
            Date created = AssetUtils.lastModified(mapPath);
            for (String texturePath : packingNow) {
                Date lastModified = AssetUtils.lastModified(texturePath);
                if (lastModified.after(created)) return false;
            }
        } catch (Exception any) {
            return false;
        }

        /* check if options are the same */
        try {
            Map<String, Object> optionsMap = (Map<String, Object>) yamlData.get("options");

            int yml_padding = (Integer) optionsMap.get("padding");
            if (yml_padding != padding) return false;
            int yml_extrude = (Integer) optionsMap.get("extrude");
            if (yml_extrude != extrude) return false;
            int yml_maxTexturesSize = (Integer) optionsMap.get("maxTexturesSize");
            if (yml_maxTexturesSize != maxTexturesSize.value) return false;

        } catch (Exception any) {
            return false;
        }

        return true;
    }

    private static boolean pack(String outputDirectory, String outputName, int extrude, int padding, TexturePackSize maxTexturesSize, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack, Array<PackedRegionData> remaining, int last, int currentImageIndex) {
        if (last < 0) return true;
        int width = 1;
        int height = 1;
        boolean stepWidth = true;
        while (width <= maxTexturesSize.value) {
            STBRPContext context = STBRPContext.create();
            STBRPNode.Buffer nodes = STBRPNode.create(width); // Number of nodes can be context width
            STBRectPack.stbrp_init_target(context, width, height, nodes);
            STBRPRect.Buffer rects = STBRPRect.create(last + 1);
            for (int i = 0; i < rects.capacity(); i++) {
                rects.position(i);
                rects.id(i);
                rects.w(remaining.get(i).packedWidth + 2 * (extrude + padding));
                rects.h(remaining.get(i).packedHeight + 2 * (extrude + padding));
            }
            rects.position(0);
            int result = STBRectPack.stbrp_pack_rects(context, rects);
            if (result != 0) {
                IndexedBufferedImage bufferedImage = new IndexedBufferedImage(currentImageIndex, width, height);
                Array<PackedRegionData> regionsData = new Array<>();
                rects.position(0);
                for (int i = 0; i < rects.capacity(); i++) {
                    rects.position(i);
                    PackedRegionData item = remaining.get(i);
                    item.x = rects.x() + extrude + padding;
                    item.y = rects.y() + extrude + padding;
                    item.textureIndex = currentImageIndex;
                    regionsData.add(item);
                }
                texturePack.put(bufferedImage, regionsData);
                remaining.removeAll(regionsData, true);
                return true;
            } else {
                if (stepWidth) width *= 2;
                else height *= 2;
                stepWidth = !stepWidth;
            }
        }
        return false;
    }

    private static PackedRegionData getPackedRegionData(final String path, final BufferedImage sourceImage) {
        int originalWidth = sourceImage.getWidth();
        int originalHeight = sourceImage.getHeight();

        int minX = originalWidth;
        int minY = originalHeight;
        int maxX = 0;
        int maxY = 0;
        // Determine the bounds
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                int alpha = (sourceImage.getRGB(x, y) >> 24) & 0xff;
                if (alpha != 0) {  // Pixel is not transparent
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        maxX++;
        maxY++;

        /* the packed width is the width of the texture after trimming the transparent margins */
        int packedWidth  = Math.max(0, maxX - minX);
        int packedHeight = Math.max(0, maxY - minY);

        int offsetX = minX;
        int offsetY = originalHeight - packedHeight - minY;

        return new PackedRegionData(path, originalWidth, originalHeight, packedWidth, packedHeight, offsetX, offsetY, minX, minY);
    }

    private static void generateTexturePackYamlFile(String outputDirectory, String outputName, int extrude, int padding, TexturePackSize maxTexturesSize, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack) {
        TextureData[] texturesData = new TextureData[texturePack.size()];
        int i = 0;
        for (IndexedBufferedImage img : texturePack.keySet()) {
            texturesData[i] = new TextureData();
            texturesData[i].file = outputName + "_" + img.index + ".png";
            texturesData[i].width = img.getWidth();
            texturesData[i].height = img.getHeight();
            i++;
        }

        Map<String, Object> optionsData = new HashMap<>();
        optionsData.put("extrude", extrude);
        optionsData.put("padding", padding);
        optionsData.put("maxTexturesSize", maxTexturesSize);

        Array<PackedRegionData> allRegions = new Array<>();
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            allRegions.addAll(imageRegions.getValue());
        }
        allRegions.pack();

        Map<String, Object> yamlData = new HashMap<>();
        yamlData.put("regions", allRegions.items);
        yamlData.put("options", optionsData);
        yamlData.put("textures", texturesData);

        String content = AssetUtils.yaml.dump(yamlData);
        try {
            AssetUtils.saveFile(outputDirectory, outputName + ".yml", content);
        } catch (Exception e) {
            throw new GraphicsException("Could not save texture pack data file. Exception: " + e.getMessage());
        }
    }

    private static void generateTexturePackImages(String outputDirectory, String outputName, int extrude, int padding, TexturePackSize maxTexturesSize, Map<IndexedBufferedImage, Array<PackedRegionData>> texturePack) throws IOException {
        for (Map.Entry<IndexedBufferedImage, Array<PackedRegionData>> imageRegions : texturePack.entrySet()) {
            IndexedBufferedImage texturePackImage = imageRegions.getKey();
            Graphics2D graphics = texturePackImage.createGraphics();
            for (PackedRegionData region : imageRegions.getValue()) {
                File sourceImageFile = new File(region.name);
                BufferedImage sourceImage = ImageIO.read(sourceImageFile);

                // copy non-transparent region
                for (int y = region.y; y < region.y + region.packedHeight; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude up
                for (int y = region.y - extrude; y < region.y; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, 0);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude down
                for (int y = region.y + region.packedHeight; y < region.y + region.packedHeight + extrude; y++) {
                    for (int x = region.x; x < region.x + region.packedWidth; x++) {
                        int color = sourceImage.getRGB(region.minX + x - region.x, region.originalHeight - 1);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude left
                for (int x = region.x - extrude; x < region.x; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = sourceImage.getRGB(0, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
                // extrude right
                for (int x = region.x + region.packedWidth; x < region.x + region.packedWidth + extrude; x++) {
                    for (int y = region.y; y < region.y + region.packedHeight; y++) {
                        int color = sourceImage.getRGB(region.originalWidth - 1, region.minY + y - region.y);
                        texturePackImage.setRGB(x,y,color);
                    }
                }
            }
            AssetUtils.saveImage(outputDirectory, outputName + "_" + texturePackImage.index, texturePackImage);
            graphics.dispose();
        }
    }

    /* Texture Generation Utils  */
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

        int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z;
        int B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;

        /* Sometimes it is beneficial to not ask questions. */
        return MathUtils.lerp(w, MathUtils.lerp(v, MathUtils.lerp(u, grad(p[AA], x, y, z),
                grad(p[BA], x - 1, y, z)),
                MathUtils.lerp(u, grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z))),
                MathUtils.lerp(v, MathUtils.lerp(u, grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1)), MathUtils.lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    private static final class PackedRegionData implements Comparable<PackedRegionData> {

        public final String name;

        public final int  originalWidth;
        public final int  originalHeight;
        public final int  packedWidth;
        public final int  packedHeight;
        public final int  offsetX;
        public final int  offsetY;
        private final int minX;
        private final int minY;
        private final int area;

        public int x;
        public int y;
        public int textureIndex;

        public PackedRegionData(String name, int originalWidth, int originalHeight, int packedWidth, int packedHeight, int offsetX, int offsetY, int minX, int minY) {
            this.name = name;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.packedWidth = packedWidth;
            this.packedHeight = packedHeight;
            this.area = packedWidth * packedHeight;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.minX = minX;
            this.minY = minY;
        }

        @Override
        public int compareTo(PackedRegionData o) {
            return Integer.compare(o.area, this.area);
        }

    }

    private static final class TextureData {

        public String file;
        public int width;
        public int height;

    }

    private static final class IndexedBufferedImage extends BufferedImage {

        private final int index;

        private IndexedBufferedImage(int index, int width, int height) {
            super(width, height, BufferedImage.TYPE_INT_ARGB);
            this.index = index;
        }

    }

    public enum TexturePackSize {

        XX_SMALL_128(128),
        X_SMALL_256(256),
        SMALL_512(512),
        MEDIUM_1024(1024),
        LARGE_2048(2048),
        X_LARGE_4096(4096),
        XX_LARGE_8192(8192),
        ;

        public final int value;

        TexturePackSize(int value) {
            this.value = value;
        }

        public static TexturePackSize get(int value) {
            for (TexturePackSize size : TexturePackSize.values()) if (size.value == value) return size;
            return XX_LARGE_8192;
        }

    }


}



