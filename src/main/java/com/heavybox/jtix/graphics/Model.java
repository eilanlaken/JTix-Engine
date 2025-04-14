package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

public class Model implements MemoryResource {

    public ModelMesh[] meshes;
    public ModelMaterial[] materials;
    public Shader shader;

    public Model(ModelMesh[] meshes, ModelMaterial[] materials) {
        this.meshes = meshes;
        this.materials = materials;
        this.shader = null; // uses default shader
    }

    public Model(ModelMesh mesh, ModelMaterial material) {
        this.meshes = new ModelMesh[1];
        this.meshes[0] = mesh;
        this.materials = new ModelMaterial[1];
        this.materials[0] = material;
        this.shader = null;
    }

    // TODO: use the (mesh, material) constructor instead.
    @Deprecated public void addMaterial(ModelMaterial material) {
        if (materials == null) {
            materials = new ModelMaterial[] { material };
        } else {
            ModelMaterial[] newMaterials = new ModelMaterial[materials.length + 1];
            System.arraycopy(materials, 0, newMaterials, 0, materials.length);
            newMaterials[materials.length] = material;
            materials = newMaterials;
        }
    }

    @Override
    public void delete() {

    }

}
