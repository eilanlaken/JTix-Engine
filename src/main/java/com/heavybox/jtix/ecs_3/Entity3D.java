package com.heavybox.jtix.ecs_3;

import org.jetbrains.annotations.NotNull;

public abstract class Entity3D extends Entity {

    protected Entity3D() {
        this(0,0,0,0,0,0,1,1,1);
    }

    protected Entity3D(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        super(new ComponentTransform3D(x, y, z, degX, degY, degZ, sclX, sclY, sclZ));
    }

    @Override protected abstract ComponentAudio     createComponentAudio();
    @Override protected abstract ComponentRender3D  createComponentRender();
    @Override protected abstract ComponentCamera    createComponentCamera();
    @Override protected abstract ComponentPhysics3D createComponentPhysics();
    @Override protected abstract ComponentLogics    createComponentLogics();
    @Override protected abstract ComponentSignals   createComponentSignals();
    @Override protected abstract ComponentRegion    createComponentRegion();

}
