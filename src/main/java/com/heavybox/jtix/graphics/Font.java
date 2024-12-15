package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple3;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Font implements MemoryResource {


    public FT_Face ftFace;

    public final Map<Integer, GlyphNotebook> glyphsNotebooks = new HashMap<>();
    public final Map<Tuple3<Character, Integer, Boolean>, Glyph> cache = new HashMap<>(); // <char, size, bold?, italic?> -> Glyph

    public Font(final String fontPath) {
        long library = Graphics.getFreeType();
        ByteBuffer fontDataBuffer;
        try {
            fontDataBuffer = Assets.fileToByteBuffer(fontPath);
        } catch (Exception e) {
            throw new GraphicsException("Could not read " + fontPath + " into ByteBuffer. Exception: " + e.getMessage());
        }
        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, fontDataBuffer, 0, facePointerBuffer); // each ttf file may have multiple indices / multiple faces. Guarantees to have 0
        long face = facePointerBuffer.get(0);
        ftFace = FT_Face.create(face);
    }

    public Font(final ByteBuffer bytes) {
        long library = Graphics.getFreeType();
        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, bytes, 0, facePointerBuffer); // each ttf file may have multiple indices / multiple faces. Guarantees to have 0
        long face = facePointerBuffer.get(0);
        ftFace = FT_Face.create(face);
    }

    public Glyph getGlyph(final char c, int size, boolean antialiasing) {
        Tuple3<Character, Integer, Boolean> props = new Tuple3<>(c,size,antialiasing);
        Glyph glyph = cache.get(props);
        if (glyph != null) return glyph;

        int pageSize = Math.min(2048, MathUtils.nextPowerOf2i(size * 5));

        GlyphNotebook notebook = glyphsNotebooks.computeIfAbsent(size, k -> new GlyphNotebook(pageSize)); // get notebook for given size
        glyph = notebook.draw(c, size, antialiasing);
        cache.put(props, glyph);
        return glyph;
    }

    @Override
    public void delete() {
        FreeType.FT_Done_Face(ftFace);
        for (Map.Entry<Integer, GlyphNotebook> entry : glyphsNotebooks.entrySet()) {
            GlyphNotebook value = entry.getValue();
            for (Texture page : value.pages) {
                page.delete();
            }
        }
    }

    public final class GlyphNotebook {

        private static final int PADDING = 5;

        private final int            pageSize;
        private       int            penX           = PADDING;
        private       int            penY           = PADDING;
        private       int            maxGlyphHeight = 0;
        public  final Array<Texture> pages          = new Array<>(true,1);

        private GlyphNotebook(int pageSize) {
            this.pageSize = pageSize;
        }

        // TODO: add SDF using
        // TODO: https://stackoverflow.com/questions/71185718/how-to-use-ft-render-mode-sdf-in-freetype
        // FT_RENDER_MODE_SDF
        public Glyph draw(char c, int size, boolean antialiasing) {
            /* if size is use for the first time, create the first texture */
            if (pages.size == 0) {
                ByteBuffer bufferEmpty = ByteBuffer.allocateDirect(pageSize * pageSize * 4);
                Texture page = new Texture(pageSize, pageSize, bufferEmpty,
                        Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                        Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1,true);
                pages.add(page);
            }

            /* set the face size */
            FreeType.FT_Set_Pixel_Sizes(ftFace, 0, size);
            /* get font supported characters && charset */
            List<Character> supportedCharacters = new ArrayList<>();
            IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
            long nextChar = FreeType.FT_Get_First_Char(ftFace, intBuffer);
            while (nextChar != 0) {
                supportedCharacters.add((char) nextChar);
                nextChar = FreeType.FT_Get_Next_Char(ftFace, nextChar, intBuffer);
            }
            /* rasterize the character */
            if (antialiasing) FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_FORCE_AUTOHINT);
            else FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_MONOCHROME);

            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();
            int glyph_width  = bitmap.width();
            int glyph_height = bitmap.rows();
            int glyph_pitch  = bitmap.pitch();
            /* create the Glyph and get basic metrics (atlasX and atlasY will be set later */
            Glyph data = new Glyph();
            data.atlasX = -1; // we need to set this to wherever we draw the glyph x
            data.atlasY = -1; // we need to set this to wherever we draw the glyph y
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

            /* get the bitmap ByteBuffer and create our own RGBA byte buffer (for antialiased fonts) */
            ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(glyph_pitch) * glyph_height);
            ByteBuffer buffer = MemoryUtil.memAlloc(data.width * data.height * 4);

            if (antialiasing) { // antialiasing
                for (int i = 0; i < data.width * data.height; i++) {
                    byte value = ftCharImageBuffer.get(i);
                    buffer.put((byte) 255); // Red
                    buffer.put((byte) 255); // Green
                    buffer.put((byte) 255); // Blue
                    buffer.put(value); // Alpha
                }
            } else { // no antialiasing
                for (int y = 0; y < data.height; y++) {
                    for (int x = 0; x < data.width; x++) {
                        // Calculate the byte and bit positions in the monochrome buffer
                        int byteIndex = y * Math.abs(glyph_pitch) + (x / 8);
                        int bitIndex = 7 - (x % 8); // Bits are stored high-to-low in each byte
                        boolean isOn = (ftCharImageBuffer.get(byteIndex) & (1 << bitIndex)) != 0;

                        // RGBA for the current pixel
                        byte r = (byte) (isOn ? 255 : 0); // Red channel
                        byte g = (byte) (isOn ? 255 : 0); // Green channel
                        byte b = (byte) (isOn ? 255 : 0); // Blue channel
                        byte a = (byte) (isOn ? 255 : 0); // Alpha channel (255 for opaque, 0 for transparent)

                        // Write RGBA to the buffer
                        buffer.put(r); // Red
                        buffer.put(g); // Green
                        buffer.put(b); // Blue
                        buffer.put(a); // Alpha
                    }
                }
            }
            buffer.flip();
            maxGlyphHeight = Math.max(maxGlyphHeight, data.height);

            if (penX + PADDING + data.width < pageSize && penY + PADDING + data.height < pageSize) {
                TextureBinder.bind(pages.last());
                GL11.glTexSubImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        penX,
                        penY,
                        data.width,
                        data.height,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        buffer          // Data
                );
                data.atlasX = penX;
                data.atlasY = penY;
                data.texture = pages.last();

                penX += PADDING + data.width;
                return data;
            }

            if (penX + PADDING + data.width >= pageSize && penY + maxGlyphHeight + PADDING + data.height < pageSize) {
                penX = PADDING;
                penY += maxGlyphHeight + PADDING;

                TextureBinder.bind(pages.last());
                GL11.glTexSubImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        penX,
                        penY,
                        data.width,
                        data.height,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        buffer          // Data
                );
                data.atlasX = penX;
                data.atlasY = penY;
                data.texture = pages.last();

                penX += PADDING + data.width;
                return data;
            }

            // flip page
            ByteBuffer bufferEmpty = ByteBuffer.allocateDirect(pageSize * pageSize * 4);
            Texture page = new Texture(pageSize, pageSize, bufferEmpty,
                    Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1, true);
            penX = PADDING;
            penY = PADDING;
            pages.add(page);

            TextureBinder.bind(pages.last());
            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    penX,
                    penY,
                    data.width,
                    data.height,
                    GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    buffer          // Data
            );
            data.atlasX = penX;
            data.atlasY = penY;
            data.texture = pages.last();

            penX += PADDING + data.width;
            return data;
        }

    }

    public static final class Glyph {

        public Texture texture;
        public int   width;
        public int   height;
        public float bearingX;
        public float bearingY;
        public float advanceX;
        public float advanceY;
        public int   atlasX;
        public int   atlasY;
        public Map<Character, Integer> kernings;


    }

}
