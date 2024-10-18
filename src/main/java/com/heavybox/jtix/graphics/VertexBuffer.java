package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// plan: let's create a simple "hard-coded" vertex buffer first with positions[...] colors[...] and uvs [...]
@Deprecated public class VertexBuffer implements MemoryResource {

    int vao;

    int vboPositions;
    int vboColors;
    int vboTextCoords;
    FloatBuffer positions;
    FloatBuffer colors;
    FloatBuffer textCoords;

    int ebo;
    IntBuffer indices;

    VertexBuffer(int capacity) {
        positions = BufferUtils.createFloatBuffer(capacity * 2);
        colors = BufferUtils.createFloatBuffer(capacity * 1);
        textCoords = BufferUtils.createFloatBuffer(capacity * 2);
        indices = BufferUtils.createIntBuffer(capacity);

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        this.vboPositions = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.vboColors = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);
        GL20.glEnableVertexAttribArray(1);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.vboTextCoords = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextCoords); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoords, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(2);
        //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind

        this.ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL30.glBindVertexArray(0);
    }

    public void flip() {
        positions.flip();
        colors.flip();
        textCoords.flip();
        indices.flip();
    }

    public void clear() {
        positions.clear();
        colors.clear();
        textCoords.clear();
        indices.clear();
    }

    // TODO: implement.
    public boolean ensureCapacity(int verticesCount) {
        // TODO: make sure that the vertex buffer has enough room to contain additional verticesCount of vertices.
        return true;
    }

    @Override
    public void delete() {

    }

}
