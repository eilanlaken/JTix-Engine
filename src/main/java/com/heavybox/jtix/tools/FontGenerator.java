package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.GraphicsException;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.freetype.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FontGenerator {

    private FontGenerator() {}

    public static void generateBitmapFont(final String directory, final String fileName, final String fontPath, int size) {
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

        /* get supported characters */
        List<Character> supportedCharacters = new ArrayList<>();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        long nextChar = FreeType.FT_Get_First_Char(ftFace, intBuffer);
        while (nextChar != 0) {
            supportedCharacters.add((char) nextChar);
            nextChar = FreeType.FT_Get_Next_Char(ftFace, nextChar, intBuffer);
        }

        /* get all glyphs' data: the bitmap, the bearing, the advance... from FreeType */
        Array<GlyphData> glyphsData = new Array<>(false, supportedCharacters.size());
        for (Character c : supportedCharacters) {
            /* set glyph data for every character */
            GlyphData data = new GlyphData();

            FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER); // TODO: set anti aliasing
            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();
            int glyph_width  = bitmap.width();
            int glyph_height = bitmap.rows();
            int glyph_pitch  = bitmap.pitch();

            if (glyph_width <= 0 || glyph_height <= 0) continue;

            ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(glyph_pitch) * glyph_height);
            BufferedImage glyphImage = new BufferedImage(glyph_width, glyph_height, BufferedImage.TYPE_INT_ARGB);
            int[] imageData = ((DataBufferInt) glyphImage.getRaster().getDataBuffer()).getData();

            // Copy pixel data from the FreeType bitmap to the BufferedImage
            for (int y = 0; y < glyph_height; y++) {
                for (int x = 0; x < glyph_width; x++) {
                    int srcIndex = y * Math.abs(glyph_pitch) + x;
                    assert ftCharImageBuffer != null;
                    int grayValue = ftCharImageBuffer.get(srcIndex) & 0xFF;
                    int alpha = grayValue;  // Use grayscale value for transparency
                    int rgb = (255 << 16) | (255 << 8) | 255;  // White color
                    imageData[y * glyph_width + x] = (alpha << 24) | rgb;
                }
            }

            data.character = c;
            data.width = glyph_width;
            data.height = glyph_height;
            data.bearingX = glyphSlot.bitmap_left();
            data.bearingY = glyphSlot.bitmap_top();
            data.advanceX = glyphSlot.advance().x();
            data.advanceY = glyphSlot.advance().y();
            data.bufferedImage = glyphImage;
            data.kernings = new HashMap<>();
            for (char rightChar : supportedCharacters) {
                FT_Vector kerningVector = FT_Vector.malloc();
                FreeType.FT_Get_Kerning(ftFace, c, rightChar, FreeType.FT_KERNING_DEFAULT, kerningVector);
                int kerningValue = (int) kerningVector.x() >> 6;
                data.kernings.put(rightChar, kerningValue);
                kerningVector.free();
            }

            glyphsData.add(data);
        }

        /* merge all glyphs images into a single buffered image. */
        for (GlyphData glyphData : glyphsData) {
            // TODO: pack everything.
            if (glyphData.character != 'G') continue;
            try {
                AssetUtils.saveImage(directory, fileName, glyphData.bufferedImage);
            } catch (Exception e) {
                throw new GraphicsException("Could not save font image to directory:" + directory + " with file name: " + fileName + ". Exception: " + e.getMessage());
            }
        }

        FreeType.FT_Done_Face(ftFace);
        FreeType.FT_Done_FreeType(library);
    }

    private static final class GlyphData {

        private char character;

        private int   width, height;
        private float bearingX, bearingY;
        private float advanceX;
        private float advanceY;

        private Map<Character, Integer> kernings;

        private BufferedImage bufferedImage;

    }

    @Deprecated
    public static void buildTextureFontFT(final String directory, final String fileName, final String fontPath, int size) {
        PointerBuffer libPointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_Init_FreeType(libPointerBuffer);

        long library = libPointerBuffer.get(0);
        ByteBuffer fontDataBuffer;
        try {
            fontDataBuffer = AssetUtils.fileToByteBuffer(fontPath);
        } catch (Exception e) {
            throw new GraphicsException("Could not read " + fontPath + " into ByteBuffer. Exception: " + e.getMessage());
        }

        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, fontDataBuffer, 0, facePointerBuffer);
        long face = facePointerBuffer.get(0);
        FT_Face ftFace = FT_Face.create(face);
        FreeType.FT_Set_Pixel_Sizes(ftFace, 0, size);

        List<Character> supportedCharacters = new ArrayList<>();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);

        /* get supported characters */
        long nextChar = FreeType.FT_Get_First_Char(ftFace, intBuffer);
        while (nextChar != 0) {
            supportedCharacters.add((char) nextChar);
            nextChar = FreeType.FT_Get_Next_Char(ftFace, nextChar, intBuffer);
        }

        FreeType.FT_Load_Char(ftFace,'B', FreeType.FT_LOAD_RENDER);
        FT_GlyphSlot glyphSlot = ftFace.glyph();
        FT_Bitmap bitmap = glyphSlot.bitmap();
        int width = bitmap.width();
        int height = bitmap.rows();
        int pitch = bitmap.pitch();
        ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(pitch) * height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        // Copy pixel data from the FreeType bitmap to the BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcIndex = y * Math.abs(pitch) + x;
                assert ftCharImageBuffer != null;
                int grayValue = ftCharImageBuffer.get(srcIndex) & 0xFF;
                int alpha = grayValue;  // Use grayscale value for transparency
                int rgb = (255 << 16) | (255 << 8) | 255;  // White color
                imageData[y * width + x] = (alpha << 24) | rgb;
            }
        }

        try {
            AssetUtils.saveImage(directory, fileName, image);
        } catch (Exception e) {
            throw new GraphicsException("Could not save font image to directory:" + directory + " with file name: " + fileName + ". Exception: " + e.getMessage());
        }

        FreeType.FT_Done_Face(ftFace);
        FreeType.FT_Done_FreeType(library);
    }

    @Deprecated
    public static void buildTextureFont(final String directory, final String fileName, final String fontPath, int size, boolean antialiasingOn) {
        /* get font metrics for raster */
        BufferedImage img = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        java.awt.Font font = new java.awt.Font(fontPath, Font.PLAIN, size);
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        float adjustment = 1.1f;
        int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
        int width = 0;
        int height = fontMetrics.getHeight();
        int lineHeight = fontMetrics.getHeight();
        int x = 0;
        int y = (int) (fontMetrics.getHeight() * adjustment);

        Map<Integer, com.heavybox.jtix.graphics.Font.Glyph> glyphMap = new HashMap<>();
        /* iterate over all the glyphs to estimate the width of the bitmap */
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                com.heavybox.jtix.graphics.Font.Glyph glyph = new com.heavybox.jtix.graphics.Font.Glyph(x,y,fontMetrics.charWidth(i), fontMetrics.getHeight());
                glyphMap.put(i, glyph);
                width = Math.max(x + fontMetrics.charWidth(i), width);
                x += glyph.width;
                if (x > estimatedWidth) {
                    x = 0;
                    y += fontMetrics.getHeight() * adjustment;
                    height += fontMetrics.getHeight() * adjustment;
                }
            }
        }
        height += fontMetrics.getHeight() * adjustment;
        g2d.dispose();

        System.out.println(width);
        System.out.println(height);
        /* rasterize the characters into a bitmap */
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasingOn ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                com.heavybox.jtix.graphics.Font.Glyph glyph = glyphMap.get(i);
                float u1 = (float) (glyph.sourceX) / width;
                float v1 = (float) (glyph.sourceY - height) / height;
                float u2 = (float) (glyph.sourceX + glyph.width) / width;
                float v2 = (float) (glyph.sourceY) / height;
                glyph.u1 = u1;
                glyph.v1 = v1;
                glyph.u2 = u2;
                glyph.v2 = v2;
                g2d.drawString("" + (char) i, glyph.sourceX, glyph.sourceY);
            }
        }
        g2d.dispose();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Paths.get(directory, fileName).toString()))) {
            AssetUtils.saveImage(directory, fileName, img);
            oos.writeObject(glyphMap);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
