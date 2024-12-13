package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple4;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.tools.ToolsFontGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.freetype.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontDynamic implements MemoryResource {

    //public final Map<Tuple3<Integer, Boolean, Boolean>, Glyph> glyphsCache = new HashMap<>();

    public FT_Face ftFace;

    public final Map<Integer, GlyphNotebook> glyphsNotebooks = new HashMap<>();

    public final Map<Tuple4<Character, Integer, Boolean, Boolean>, Glyph> cache = new HashMap<>(); // <char, size, bold?, italic?> -> Glyph

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

    public Glyph getGlyph(final char c, int size, boolean bold, boolean italic) {
        Tuple4<Character, Integer, Boolean, Boolean> props = new Tuple4<>(c,size,bold,italic);
        Glyph glyph = cache.get(props);
        if (glyph != null) return glyph;
        // TODO: figure out the optimal page width and page height.
        int pageWidth = 256;
        int pageHeight = 256;
        GlyphNotebook notebook = glyphsNotebooks.computeIfAbsent(size, k -> new GlyphNotebook(pageWidth, pageHeight)); // get notebook for given size
        glyph = notebook.draw(c, size, bold, italic);
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

        // finals
        private final int texturesWidth;
        private final int texturesHeight;

        private int penX = 0;
        private int penY = 0;
        private int maxGlyphWidth = 0;
        private int maxGlyphHeight = 0;
        private Array<Texture> glyphsPages = new Array<>(true,1);

        private GlyphNotebook(int texturesWidth, int texturesHeight) {
            this.texturesWidth = texturesWidth;
            this.texturesHeight = texturesHeight;
        }

        public Glyph draw(char c, int size, boolean bold, boolean italic) {
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

            return null;
        }

        // todo: inline
        private void flipPage() {

        }

    }

    public static final class Glyph {

        public Texture atlas;
        public int   width;
        public int   height;
        public float bearingX;
        public float bearingY;
        public float advanceX;
        public float advanceY;
        public int   atlasX;
        public int   atlasY;
        public Map<Character, Integer> kernings;

//        public Glyph(int width, int height, float bearingX, float bearingY, float advanceX, float advanceY, int atlasX, int atlasY, Map<Character, Integer> kernings) {
//            this.width = width;
//            this.height = height;
//            this.bearingX = bearingX;
//            this.bearingY = bearingY;
//            this.advanceX = advanceX;
//            this.advanceY = advanceY;
//            this.atlasX = atlasX;
//            this.atlasY = atlasY;
//            this.kernings = kernings;
//        }

    }

}
