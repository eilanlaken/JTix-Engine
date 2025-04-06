package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryUtils;
import com.heavybox.jtix.z_deprecated.z_graphics_old.VertexAttribute_old;
import com.heavybox.jtix.z_deprecated.z_old_assets.AssetLoaderModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class ModelMesh implements MemoryResource {

    public int vaoId;
    public int vertexCount;
    public ArrayInt vbos = new ArrayInt();

    public ModelMesh(float[] positions) {
        if (positions.length % 3 != 0) throw new GraphicsException("Positions array must be a flat array of 3d vectors: [x0,y0,z0, x1,y1,z1, ...]. Size must be divisible by 3.");
        this.vertexCount = positions.length / 3;

        // store positions in vbo
        // TODO: modify to support different model with different vbos.
        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vaoId); // bind
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = MemoryUtils.store(positions);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0); // unbind

        System.out.println(vaoId);
        System.out.println(vbos);
    }

    // TODO: confirm it works
    @Override
    public void delete() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int i = 0; i < vbos.size; i++) {
            GL30.glDeleteBuffers(vbos.get(i));
        }
    }


}

/*
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

    public boolean hasVertexAttribute(final VertexAttribute_old attribute) {
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


 */
