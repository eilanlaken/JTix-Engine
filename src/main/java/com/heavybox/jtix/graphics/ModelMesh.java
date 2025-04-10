package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryUtils;
import com.heavybox.jtix.z_deprecated.z_graphics_old.VertexAttribute_old;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelMesh implements MemoryResource {

    public int vaoId;
    public int vertexCount;
    public boolean useIndices;
    public float boundingSphereRadius;
    public int attributeBitmask;
    public int[] vbos;

    public ModelMesh(float[] positions, float[] uvs, float[] colors, float[] normals, int[] indices, float boundingSphereRadius) {
        Array<VertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        this.vertexCount = indices != null ? indices.length : positions.length / 3;

        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
            storeDataInAttributeList(VertexAttribute.POSITION_3D, positions, attributesCollector, vbosCollector);
            storeIndicesBuffer(indices, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXT_COORDS0, uvs, attributesCollector, vbosCollector);
            //storeDataInAttributeList(VertexAttribute.COLOR, colors, attributesCollector, vbosCollector);
            //storeDataInAttributeList(VertexAttribute.NORMAL_3D, normals, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);

        this.attributeBitmask = VertexAttribute.generateBitmask(attributesCollector);
        this.useIndices = indices != null;
        this.boundingSphereRadius = boundingSphereRadius;
        this.vbos = vbosCollector.pack();
    }

    @Deprecated public ModelMesh(float[] positions) {
        if (positions.length % 3 != 0) throw new GraphicsException("Positions array must be a flat array of 3d vectors: [x0,y0,z0, x1,y1,z1, ...]. Size must be divisible by 3.");
        this.vertexCount = positions.length / 3;

        // store positions in vbo
        // TODO: modify to support different model with different vbos.
        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vaoId); // bind
        int vboID = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = MemoryUtils.store(positions);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0); // unbind
    }

    private void storeIndicesBuffer(int[] indices, ArrayInt vbosCollector) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbosCollector.add(vbo);
    }

    private void storeDataInAttributeList(final VertexAttribute attribute, final float[] data, Array<VertexAttribute> attributesCollector, ArrayInt vbosCollector) {
        if (data == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute.glslLocation, attribute.dimension, attribute.glType, attribute.normalized, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        vbosCollector.add(vbo);
        attributesCollector.add(attribute);
    }

    public boolean hasVertexAttribute(final VertexAttribute attribute) {
        return (attributeBitmask & attribute.bitmask) != 0;
    }

    // TODO: confirm it works
    @Override
    public void delete() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
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
