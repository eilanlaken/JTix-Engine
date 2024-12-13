package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.collections.Tuple4;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.tools.ToolsFontGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.freetype.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontDynamic implements MemoryResource {


    public FT_Face ftFace;

    public final Map<Integer, GlyphNotebook> glyphsNotebooks = new HashMap<>();

    public final Map<Tuple2<Character, Integer>, Glyph> cache = new HashMap<>(); // <char, size, bold?, italic?> -> Glyph

    public FontDynamic(final String fontPath) {
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

    public Glyph getGlyph(final char c, int size) {
        Tuple2<Character, Integer> props = new Tuple2<>(c,size);
        Glyph glyph = cache.get(props);
        if (glyph != null) return glyph;
        // TODO: figure out the optimal page width and page height.
        int pageWidth = 256;
        int pageHeight = 256;
        GlyphNotebook notebook = glyphsNotebooks.computeIfAbsent(size, k -> new GlyphNotebook(pageWidth, pageHeight)); // get notebook for given size
        glyph = notebook.draw(c, size);
        cache.put(props, glyph);
        return glyph;
    }

    @Override
    public void delete() {
        FreeType.FT_Done_Face(ftFace);
        for (Map.Entry<Integer, GlyphNotebook> entry : glyphsNotebooks.entrySet()) {
            GlyphNotebook value = entry.getValue();
            for (Texture page : value.glyphsPages) {
                page.delete();
            }
        }
    }

    private final class GlyphNotebook {

        private final int texturesWidth;
        private final int texturesHeight;

        private int penX = 0;
        private int penY = 0;
        private int maxGlyphWidth = 0;
        private int maxGlyphHeight = 0;
        private final Array<Texture> glyphsPages = new Array<>(true,1);

        private GlyphNotebook(int texturesWidth, int texturesHeight) {
            this.texturesWidth = texturesWidth;
            this.texturesHeight = texturesHeight;
        }

        public Glyph draw(char c, int size) {
            FreeType.FT_Set_Pixel_Sizes(ftFace, 0, size);

            /* get font supported characters && charset */
            List<Character> supportedCharacters = new ArrayList<>();
            IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
            long nextChar = FreeType.FT_Get_First_Char(ftFace, intBuffer);
            while (nextChar != 0) {
                supportedCharacters.add((char) nextChar);
                nextChar = FreeType.FT_Get_Next_Char(ftFace, nextChar, intBuffer);
            }

            Glyph data = new Glyph();
            FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER);

            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();
            int glyph_width  = bitmap.width();
            int glyph_height = bitmap.rows();
            int glyph_pitch  = bitmap.pitch();
            data.atlasX = -1; // we need to set this
            data.atlasY = -1; // we need to set this
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

            ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(glyph_pitch) * glyph_height);
            ftCharImageBuffer.flip();

            Texture page;
            int padding = 2;
            if (glyphsPages.size == 0 || penX + data.width >= texturesWidth || penY + data.height >= texturesHeight) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(texturesWidth * texturesHeight * 4);
                page = new Texture(texturesWidth, texturesHeight, buffer,
                        Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                        Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1,false);
                penX = 0;
                penY = 0;
                glyphsPages.add(page);
            } else {
                page = glyphsPages.last();
            }
            TextureBinder.bind(page);
            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    penX,
                    penY,
                    data.width,
                    data.height,
                    GL11.GL_RGB,
                    GL11.GL_UNSIGNED_BYTE,
                    ftCharImageBuffer          // Data
            );
            data.atlasX = penX; // we need to set this
            data.atlasY = penY; // we need to set this
            // advance the pen

            maxGlyphWidth = Math.max(data.width, maxGlyphWidth);
            maxGlyphHeight = Math.max(data.height, maxGlyphHeight);

            penX += maxGlyphWidth + padding;
            if (penX > texturesWidth) {
                penX = 0;
                penY += maxGlyphHeight;
            }

            data.texture = page;
            return data;
        }

        // todo: inline
        private void flipPage() {

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
