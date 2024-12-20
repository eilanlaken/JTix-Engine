package com.heavybox.jtix.z_graphics_old;

import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.memory.MemoryResource;

public class ModelPart implements MemoryResource {

    public ModelPartMesh mesh;
    public ModelPartMaterial material;
    public final Class<? extends Shader> customShaderClass;

    public ModelPart(final ModelPartMesh mesh, final ModelPartMaterial material, final Shader shader) {
        this.mesh = mesh;
        this.material = material;
        if (shader == null) this.customShaderClass = null;
        else customShaderClass = shader.getClass();
    }

    @Override
    public void delete() {
        mesh.delete();
    }
}
