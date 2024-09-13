package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Font implements MemoryResource {

    public final Texture               fontMap;
    public final String                filepath;
    public final int                   size;
    public final Map<Character, Glyph> glyphMap;

    public Font(Texture fontMap, String filepath, int size, Map<Character, Glyph> glyphMap) {
        this.fontMap = fontMap;
        this.filepath = filepath;
        this.size = size;
        this.glyphMap = glyphMap;
    }

    @Override
    public void delete() {

    }

    // TODO: include better glyph data.
    public static final class Glyph implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L; // Add a serial version ID for version control

        public final int sourceX;
        public final int sourceY;
        public final int width;
        public final int height;

        // TODO: better to make final somehow.
        public float u1, v1;
        public float u2, v2;

        public Glyph(int sourceX, int sourceY, int width, int height) {
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.width = width;
            this.height = height;
        }

    }

}
