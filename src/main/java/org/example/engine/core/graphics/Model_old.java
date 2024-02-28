package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;

@Deprecated
public class Model_old implements Resource {

    // TODO: refactor into parts etc.
    public final int vaoId;
    ArrayInt vbos;
    public final int vertexCount;

    // TODO: refactor into material etc.
    public Texture texture;

    public Model_old(final int vaoId, final int vertexCount, @Deprecated final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;

        // TODO: see how to deprecate
        this.vbos = new ArrayInt();
        for (int vbo : vbos) {
            this.vbos.add(vbo);
        }
    }

    // later, TODO.
    public Array<ModelPart> parts;
    public ModelArmature armature;

    @Override
    public void free() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos.items) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    public HashMap<String, Object> get_material_debug() {
        HashMap<String, Object> uniforms = new HashMap<>();
        uniforms.put("texture0", texture);
        return uniforms;
    }
}