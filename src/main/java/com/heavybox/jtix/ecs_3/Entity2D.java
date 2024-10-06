package com.heavybox.jtix.ecs_3;

public abstract class Entity2D extends Entity {

    protected Entity2D() {
        this(0,0,0,1,1);
    }

    protected Entity2D(float x, float y, float z, float deg) {
        this(x, y, deg, 1, 1);
    }

    protected Entity2D(float x, float y, float deg, float sclX, float sclY) {
        super(new ComponentTransform2D(x, y, deg, sclX, sclY));
    }

    @Override protected abstract ComponentAudio     createComponentAudio();
    @Override protected abstract ComponentRender2D  createComponentRender();
    @Override protected abstract ComponentCamera    createComponentCamera();
    @Override protected abstract ComponentPhysics2D createComponentPhysics();
    @Override protected abstract ComponentLogics    createComponentLogics();
    @Override protected abstract ComponentSignals   createComponentSignals();
    @Override protected abstract ComponentRegion    createComponentRegion();

}
