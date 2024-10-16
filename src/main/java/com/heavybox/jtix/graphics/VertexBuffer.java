package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.AssetLoaderModel;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/*
represents a dynamic mesh (a container of vertices) that can be written to, rendered and cleared.
 */
public class VertexBuffer {

    /* buffers */

    private int vao;
    private int vbo;
    private int ebo;
    private int vertexSize;
    private IntBuffer indicesBuffer;//  = BufferUtils.createIntBuffer(INDICES_CAPACITY * 3);
    private FloatBuffer verticesBuffer;// = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * VERTEX_SIZE);
    private int bitmask; // attributes bitmask
    private boolean indexed;

    VertexBuffer(int capacity, final VertexAttribute ...attributes) {
        // AssetLoaderModel.storeDataInAttributeList() example...
    }

    // TODO: this is the hard part.
    void addVertex(final VertexAttribute attribute, float ...values) {

    }

    void clear() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertex Buffer: ").append(Integer.toBinaryString(bitmask)).append('\n');
        sb.append("Vertices: ").append('\n');
        for (int i = 0; i < verticesBuffer.limit(); i++) {
            sb.append(String.format("%6f", verticesBuffer.get(i)));
            if (i % vertexSize == 0) sb.append('\n');
        }
        return sb.toString();
    }

}
