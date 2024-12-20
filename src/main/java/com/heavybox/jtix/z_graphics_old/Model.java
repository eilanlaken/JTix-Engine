package com.heavybox.jtix.z_graphics_old;

import com.heavybox.jtix.memory.MemoryResource;

public class Model implements MemoryResource {

    public final ModelPart[] parts;
    public final ModelArmature armature;

    public Model(final ModelPart[] parts, final ModelArmature armature) {
        this.parts = parts;
        this.armature = armature;
    }

    @Override
    public void delete() {

    }
}
