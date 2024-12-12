package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple3;
import com.heavybox.jtix.memory.MemoryResource;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FontDynamic implements MemoryResource {

    public final Map<Tuple3<Integer, Boolean, Boolean>, Glyph> glyphsCache = new HashMap<>();

    public FontDynamic() {

    }

    @Override
    public void delete() {

    }

    public static final class Glyph implements Serializable {

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
