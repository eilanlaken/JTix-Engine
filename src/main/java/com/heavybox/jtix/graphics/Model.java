package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

public class Model implements MemoryResource {

    public ModelMesh[] meshes;
    public ModelMaterial[] materials;

    // TODO: tmp.
    public Model(ModelMesh[] meshes, ModelMaterial[] materials) {
        this.meshes = meshes;
    }

    @Override
    public void delete() {

    }

}
