package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.io.Serial;
import java.io.Serializable;

public class Font implements MemoryResource {

    String filepath;
    int size;



    @Override
    public void delete() {

    }

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
