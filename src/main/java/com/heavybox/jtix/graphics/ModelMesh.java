package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelMesh implements MemoryResource {

    public int     vertexArrayObjectId;
    public int     vertexCount;
    public boolean useIndices;
    public float   boundingSphereRadius;
    public int     attributeBitmask;
    public int[]   vertexBufferObjects;

    public ModelMesh(float[] positions, float[] uvs, @Deprecated float[] colors, float[] normals, float[] tangents, @Deprecated float[] biTangents, int[] indices, float boundingSphereRadius) {
        Array<VertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        this.vertexCount = indices != null ? indices.length : positions.length / 3;

        this.vertexArrayObjectId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayObjectId);
        {
            storeDataInAttributeList(VertexAttribute.POSITION, positions, attributesCollector, vbosCollector);
            storeIndicesBuffer(indices, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXT_COORDS0, uvs, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.NORMAL, normals, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TANGENT, tangents, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BI_TANGENT, biTangents, attributesCollector, vbosCollector); // TODO: remove
        }
        GL30.glBindVertexArray(0);

        this.attributeBitmask = VertexAttribute.generateBitmask(attributesCollector);
        this.useIndices = indices != null;
        this.boundingSphereRadius = boundingSphereRadius;
        this.vertexBufferObjects = vbosCollector.pack();
    }

    @Deprecated public ModelMesh(float[] positions) {
        if (positions.length % 3 != 0) throw new GraphicsException("Positions array must be a flat array of 3d vectors: [x0,y0,z0, x1,y1,z1, ...]. Size must be divisible by 3.");
        this.vertexCount = positions.length / 3;

        // store positions in vbo
        // TODO: modify to support different model with different vbos.
        this.vertexArrayObjectId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vertexArrayObjectId); // bind
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
        GL30.glDeleteVertexArrays(vertexArrayObjectId);
        for (int vbo : vertexBufferObjects) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    // https://github.com/mrdoob/three.js/tree/dev/src/geometries

    public static ModelMesh createCube(float sizeX, float sizeY, float sizeZ) {
        return null;
    }

    public static ModelMesh createPlane(float width, float height, int subdivisionsWidth, int subdivisionsHeight) {

        float width_half = width / 2.0f;
        float height_half = height / 2.0f;

        int gridX = subdivisionsWidth;
        int gridY = subdivisionsHeight;

        int gridX1 = gridX + 1;
        int gridY1 = gridY + 1;

        float segment_width = width / gridX;
        float segment_height = height / gridY;

        int[] indices = new int[1];
		float[] positions = new float[1];
		float[] normals = new float[1];
		float[] tangents = new float[1];
		float[] biTangents = new float[1];
		float[] uvs = new float[1];

        // set vertex array buffer
        for (int iy = 0; iy < gridY1; iy ++ ) {
			float y = iy * segment_height - height_half;
            for (int ix = 0; ix < gridX1; ix ++ ) {

				float x = ix * segment_width - width_half;

                //vertices.push( x, - y, 0 );
                //normals.push( 0, 0, 1 );
                //tangents.push( 1, 0, 0 );
                //biTangents.push( 0, 1, 0 );
                //uvs.push( ix / gridX );
                //uvs.push( 1 - ( iy / gridY ) );
            }
        }

        // set element array buffer (indices)
        for (int iy = 0; iy < gridY; iy ++ ) {
            for (int ix = 0; ix < gridX; ix ++ ) {
				int a = ix + gridX1 * iy;
                int b = ix + gridX1 * ( iy + 1 );
                int c = ( ix + 1 ) + gridX1 * ( iy + 1 );
                int d = ( ix + 1 ) + gridX1 * iy;
                //indices.push( a, b, d );
                //indices.push( b, c, d );
            }
        }

        float radius = (float) Math.sqrt(width_half * width_half + height_half * height_half);
        // TODO: remove biTangents.
        return new ModelMesh(positions, uvs, null, normals, tangents, biTangents, indices, radius);
    }

}