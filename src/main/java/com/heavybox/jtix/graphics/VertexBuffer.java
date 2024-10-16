package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/*
represents a dynamic mesh (a container of vertices) that can be written to, rendered and cleared.
 */
public final class VertexBuffer implements MemoryResource {

    /* buffers */

    final int vao;
    final int[] vbos;
    final int ebo;
    final int vertexSize;
    final IntBuffer indicesBuffer;//  = BufferUtils.createIntBuffer(INDICES_CAPACITY * 3);
    final FloatBuffer[] verticesBuffers;// = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * VERTEX_SIZE);
    final int bitmask; // attributes bitmask


    VertexBuffer(int capacity, final VertexAttribute_2 ...attributes) {
        // AssetLoaderModel.storeDataInAttributeList() example...
        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // calculate vertex size
        this.vertexSize = VertexAttribute_2.getVertexLength(attributes);
        this.vbos = new int[VertexAttribute_2.values().length];
        this.bitmask = VertexAttribute_2.getBitmask(attributes);

        this.verticesBuffers = new FloatBuffer[VertexAttribute_2.values().length];
        for (int i = 0; i < VertexAttribute_2.values().length; i++) {
            VertexAttribute_2 attribute_2 = VertexAttribute_2.values()[i];
            if ((attribute_2.bitmask & bitmask) == 0) {
                this.vbos[i] = -1;
                continue;
            }
            int vbo = GL15.glGenBuffers();
            this.vbos[i] = vbo;
            this.verticesBuffers[i] = BufferUtils.createFloatBuffer(capacity * vertexSize);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.verticesBuffers[i].capacity(), GL15.GL_DYNAMIC_DRAW);
            GL20.glVertexAttribPointer(i, attribute_2.length, attribute_2.primitiveType, attribute_2.normalized, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
            GL20.glEnableVertexAttribArray(i);
        }


        indicesBuffer = BufferUtils.createIntBuffer(capacity * 3);
        this.ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), GL15.GL_DYNAMIC_DRAW);

        GL30.glBindVertexArray(0);
    }

    public boolean hasVertexAttribute(final VertexAttribute attribute) {
        return (bitmask & attribute.bitmask) != 0;
    }

    // TODO: this is the hard part.
    void write(final VertexAttribute attribute, float ...values) {

    }

    void erase() {

    }

    @Override
    public void delete() {
        // TODO
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertex Buffer: ").append(Integer.toBinaryString(bitmask)).append('\n');
        sb.append("Vertices: ").append('\n');
        for (int i = 0; i < VertexAttribute_2.values().length; i++) {
            if (verticesBuffers[i] == null) continue;
            sb.append(VertexAttribute_2.values()[i].name()).append(" :");
            for (int j = 0; j < verticesBuffers[i].limit(); j++) {
                sb.append(String.format("%5f", verticesBuffers[i].get(j)));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}

/*


private ModelPartMesh create(final ModelPartMeshData meshData) {
        Array<VertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
            storeIndicesBuffer(meshData.indices, vbosCollector);
            storeDataInAttributeList(VertexAttribute.POSITION_3D, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.COLOR, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXTURE_COORDINATES0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXTURE_COORDINATES1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TANGENT, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BI_NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT2, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT3, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT4, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BONE_WEIGHT5, meshData, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);
        final short bitmask = generateBitmask(attributesCollector);
        final int[] vbos = vbosCollector.pack();
        return new ModelPartMesh(vaoId, meshData.vertexCount, bitmask,meshData.indices != null, meshData.boundingSphereCenter, meshData.boundingSphereRadius, vbos);
    }


    private void storeDataInAttributeList(final VertexAttribute attribute, final ModelPartMeshData meshData, Array<VertexAttribute> attributesCollector, ArrayInt vbosCollector) {
        final float[] data = (float[]) meshData.vertexBuffers.get(attribute);
        if (data == null) return;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.length;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        attributesCollector.add(attribute);
        vbosCollector.add(vbo);
    }

    private void storeIndicesBuffer(int[] indices, ArrayInt vbosCollector) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbosCollector.add(vbo);
    }

 */