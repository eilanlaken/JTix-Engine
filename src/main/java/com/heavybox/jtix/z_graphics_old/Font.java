package com.heavybox.jtix.z_graphics_old;

import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.memory.MemoryResource;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Deprecated
public class Font implements MemoryResource {

    public final Texture fontAtlas;
    public final String  charset;
    public final float   invAtlasWidth;
    public final float   invAtlasHeight;
    public final int     size;
    public final boolean antialiasing;

    public final Map<Integer, Glyph> glyphs;

    public Font(Texture fontAtlas, final String charset, int size, boolean antialiasing, Map<Integer, Glyph> glyphs) {
        this.fontAtlas = fontAtlas;
        this.invAtlasWidth = 1.0f / fontAtlas.width;
        this.invAtlasHeight = 1.0f / fontAtlas.height;
        this.charset = charset;
        this.size = size;
        this.antialiasing = antialiasing;
        this.glyphs = glyphs;
    }

    @Override
    public void delete() {
        fontAtlas.delete();
    }

    public static final class Glyph implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L; // Add a serial version ID for version control

        public final int   width;
        public final int   height;
        public final float bearingX;
        public final float bearingY;
        public final float advanceX;
        public final float advanceY;
        public final int   atlasX;
        public final int   atlasY;

        public final Map<Character, Integer> kernings;

        public Glyph(int width, int height, float bearingX, float bearingY, float advanceX, float advanceY, int atlasX, int atlasY, Map<Character, Integer> kernings) {
            this.width = width;
            this.height = height;
            this.bearingX = bearingX;
            this.bearingY = bearingY;
            this.advanceX = advanceX;
            this.advanceY = advanceY;
            this.atlasX = atlasX;
            this.atlasY = atlasY;
            this.kernings = kernings;
        }

    }

}
