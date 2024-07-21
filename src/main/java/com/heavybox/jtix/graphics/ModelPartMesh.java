package com.heavybox.jtix.graphics;

import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL30;

public class ModelPartMesh implements MemoryResource {

    public final int vaoId;
    public final int vertexCount;
    public final short vertexAttributeBitmask;
    public final boolean indexed;
    public final Vector3 boundingSphereCenter;
    public final float   boundingSphereRadius;
    public final int[] vbos;

    public ModelPartMesh(final int vaoId, final int vertexCount, final short bitmask, final boolean indexed, final Vector3 boundingSphereCenter, float boundingSphereRadius, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vertexAttributeBitmask = bitmask;
        this.indexed = indexed;
        this.boundingSphereCenter = new Vector3(boundingSphereCenter);
        this.boundingSphereRadius = boundingSphereRadius;
        this.vbos = vbos;
    }

    public boolean hasVertexAttribute(final ModelVertexAttribute attribute) {
        return (vertexAttributeBitmask & attribute.bitmask) != 0;
    }

    @Override
    // TODO: confirm it works
    public void delete() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
    }
}
