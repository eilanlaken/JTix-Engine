package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.graphics.GraphicsException;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.freetype.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public final class ToolsFontGenerator {

    private ToolsFontGenerator() {}

    // TODO: implement.
    public synchronized static void generateFontSDF() {

    }

    public synchronized static void generateFontBitmap(final String fontPath, int size, boolean antialiasing, @Nullable String charset) {
        Path font = Paths.get(fontPath);
        Path directory = font.getParent();
        String filename = font.getFileName().toString();
        String filenameNoExtension = AssetUtils.removeExtension(filename);
        generateFontBitmap(directory.toString(), filenameNoExtension + "-" + size, fontPath, size, antialiasing, charset);
    }

    private synchronized static void generateFontBitmap(final String directory, final String outputName, final String fontPath, int size, boolean antialiasing, @Nullable String charset) {
        if (alreadyGenerated(directory, outputName, fontPath, size, antialiasing, charset)) return;

        /* init font library */
        PointerBuffer libPointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_Init_FreeType(libPointerBuffer);

        /* load .ttf file to bytebuffer */
        long library = libPointerBuffer.get(0);
        ByteBuffer fontDataBuffer;
        try {
            fontDataBuffer = AssetUtils.fileToByteBuffer(fontPath);
        } catch (Exception e) {
            throw new GraphicsException("Could not read " + fontPath + " into ByteBuffer. Exception: " + e.getMessage());
        }

        /* create new in-memory face with face index 0 */
        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, fontDataBuffer, 0, facePointerBuffer); // each ttf file may have multiple indices / multiple faces. Guarantees to have 0
        long face = facePointerBuffer.get(0);
        FT_Face ftFace = FT_Face.create(face);
        FreeType.FT_Set_Pixel_Sizes(ftFace, 0, size);

        /* get font supported characters && charset */
        List<Character> supportedCharacters = new ArrayList<>();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        long nextChar = FreeType.FT_Get_First_Char(ftFace, intBuffer);
        while (nextChar != 0) {
            if (charset == null) supportedCharacters.add((char) nextChar);
            else if (charset.indexOf((char) nextChar) != -1) supportedCharacters.add((char) nextChar);
            nextChar = FreeType.FT_Get_Next_Char(ftFace, nextChar, intBuffer);
        }

        /* get all glyphs' data {bitmap, bearing, advance...} from FreeType */
        GlyphData[] glyphsData = new GlyphData[supportedCharacters.size()];
        for (int i = 0; i < supportedCharacters.size(); i++) {
            char c = supportedCharacters.get(i);
            /* set glyph data for every character */
            GlyphData data = new GlyphData();
            glyphsData[i] = data;

            if (antialiasing) FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER);
            else FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_MONOCHROME);

            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();
            int glyph_width  = bitmap.width();
            int glyph_height = bitmap.rows();
            int glyph_pitch  = bitmap.pitch();

            data.character = c;
            data.atlasX = -1;
            data.atlasY = -1;
            data.width = glyph_width;
            data.height = glyph_height;
            data.bearingX = glyphSlot.bitmap_left();
            data.bearingY = glyphSlot.bitmap_top();
            data.advanceX = glyphSlot.advance().x() >> 6; // FreeType gives the advance in x64 units, so we divide by 64.
            data.advanceY = glyphSlot.advance().y() >> 6; // FreeType gives the advance in x64 units, so we divide by 64.

            data.kernings = new HashMap<>();
            FT_Vector kerningVector = FT_Vector.malloc();
            for (char rightChar : supportedCharacters) {
                int result = FreeType.FT_Get_Kerning(ftFace, c, rightChar, FreeType.FT_KERNING_DEFAULT, kerningVector);
                if (result == 0) continue;
                int kerningValue = (int) kerningVector.x() >> 6;
                data.kernings.put(rightChar, kerningValue);
            }
            kerningVector.free();

            /* set glyph image, if applicable (for non-space characters, like ABC...). */
            if (glyph_width <= 0 || glyph_height <= 0) continue;

            ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(glyph_pitch) * glyph_height);
            BufferedImage glyphImage = new BufferedImage(glyph_width, glyph_height, BufferedImage.TYPE_INT_ARGB);
            int[] imageData = ((DataBufferInt) glyphImage.getRaster().getDataBuffer()).getData();

            assert ftCharImageBuffer != null;
            if (antialiasing) {
                for (int y = 0; y < glyph_height; y++) {
                    for (int x = 0; x < glyph_width; x++) {
                        int srcIndex = y * Math.abs(glyph_pitch) + x;
                        int alpha = ftCharImageBuffer.get(srcIndex) & 0xFF;  // Use grayscale value for transparency
                        int rgb = (255 << 16) | (255 << 8) | 255;  // White color
                        imageData[y * glyph_width + x] = (alpha << 24) | rgb;
                    }
                }
            } else {
                for (int y = 0; y < glyph_height; y++) {
                    for (int x = 0; x < glyph_width; x++) {
                        int byteIndex = y * Math.abs(glyph_pitch) + (x / 8);  // Get the byte that holds this pixel
                        int bitIndex = 7 - (x % 8);  // Get the specific bit for this pixel (most significant bit first)
                        byte pixelByte = ftCharImageBuffer.get(byteIndex);
                        int pixelValue = (pixelByte >> bitIndex) & 1;  // Get the bit for the current pixel
                        int alpha = pixelValue == 1 ? 255 : 0;  // Fully opaque if 1, fully transparent if 0
                        int rgb = (255 << 16) | (255 << 8) | 255;  // White color
                        imageData[y * glyph_width + x] = (alpha << 24) | rgb;
                    }
                }
            }

            data.bufferedImage = glyphImage;
        }

        /* estimate the font image atlas width and height */
        float heightAdjustment = 1.1f;
        int estimatedWidth = (int) Math.sqrt(glyphsData.length) * size + 1;
        int atlasWidth = 0;
        int atlasHeight = size;
        int padding = 2;
        int x = 0;
        int y = (int) (size * heightAdjustment);
        for (GlyphData data : glyphsData) {
            data.atlasX = x;
            data.atlasY = y;
            atlasWidth = Math.max(x + data.width, atlasWidth);
            x += data.width + padding;
            if (x > estimatedWidth) {
                x = 0;
                y += size * heightAdjustment;
                atlasHeight += size * heightAdjustment;
            }
        }
        atlasHeight += size * heightAdjustment;
        atlasWidth = MathUtils.nextPowerOf2(atlasWidth);
        atlasHeight = MathUtils.nextPowerOf2(atlasHeight);

        BufferedImage fontAtlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = fontAtlas.createGraphics();
        for (GlyphData glyphData : glyphsData) {
            if (glyphData.bufferedImage == null) continue;
            pen.drawImage(glyphData.bufferedImage, glyphData.atlasX, glyphData.atlasY, null); // Draw img1 at (100, 100) in img2
        }
        try {
            AssetUtils.saveImage(directory, outputName, fontAtlas);
        } catch (Exception e) {
            throw new GraphicsException("Could not save font image to directory:" + directory + " with file name: " + outputName + ". Exception: " + e.getMessage());
        }
        pen.dispose();

        /* save glyphs data */
        Map<String, Object> yamlData = new HashMap<>();
        {
            // meta-data
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("name", AssetUtils.removeExtension(Paths.get(fontPath).getFileName().toString()));
            metaData.put("atlas", outputName + ".png");

            // options
            Map<String, Object> optionsData = new HashMap<>();
            optionsData.put("size", size);
            optionsData.put("antialiasing", antialiasing);
            optionsData.put("charset", charset);

            yamlData.put("meta", metaData);
            yamlData.put("options", optionsData);
            yamlData.put("glyphs", glyphsData);
        }
        String content = AssetUtils.yaml().dump(yamlData);
        try {
            AssetUtils.saveFile(directory, outputName + ".yml", content);
        } catch (Exception e) {
            throw new GraphicsException("Could not save texture pack data file. Exception: " + e.getMessage());
        }

        /* free FreeType library and face */
        FreeType.FT_Done_Face(ftFace);
        FreeType.FT_Done_FreeType(library);
    }

    private static synchronized boolean alreadyGenerated(final String directory, final String outputName, final String fontPath, int size, boolean antialiasing, @Nullable String charset) {
        try {
            String texturePath = Paths.get(directory, outputName + ".png").toString();
            if (!AssetUtils.fileExists(texturePath)) return false;

            String dataPath = Paths.get(directory, outputName + ".yml").toString();
            if (!AssetUtils.fileExists(dataPath)) return false;

            Date lastGenerated = AssetUtils.lastModified(dataPath);
            Date lastModified = AssetUtils.lastModified(fontPath);
            if (lastModified.after(lastGenerated)) return false;

            String fontDataContent = AssetUtils.getFileContent(dataPath);
            Map<String, Object> data = AssetUtils.yaml().load(fontDataContent);

            // compare 'meta' section
            Map<String, Object> meta = (Map<String, Object>) data.get("meta");
            String name = (String) meta.get("name");
            Path font = Paths.get(fontPath);
            String filename = font.getFileName().toString();
            String filenameNoExtension = AssetUtils.removeExtension(filename);
            if (!Objects.equals(filenameNoExtension, name)) return false;

            // Extract 'options' section
            Map<String, Object> options = (Map<String, Object>) data.get("options");
            String charsetData = (String) options.get("charset");
            int sizeData = (int) options.get("size");
            boolean antialiasingData = (boolean) options.get("antialiasing");
            if (!Objects.equals(charset, charsetData)) return false;
            if (!Objects.equals(size, sizeData)) return false;
            if (!Objects.equals(antialiasing, antialiasingData)) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static final class GlyphData {

        public int   character;
        public int   width, height;
        public float bearingX, bearingY;
        public float advanceX;
        public float advanceY;
        public int   atlasX;
        public int   atlasY;

        public  Map<Character, Integer> kernings;

        transient private BufferedImage bufferedImage;

    }

}