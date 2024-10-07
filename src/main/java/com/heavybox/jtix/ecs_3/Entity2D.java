package com.heavybox.jtix.ecs_3;

public abstract class Entity2D extends Entity {

    public final ComponentTransform2D transform;

    protected Entity2D() {
        this(0,0,0,1,1);
    }

    protected Entity2D(float x, float y, float z, float deg) {
        this(x, y, deg, 1, 1);
    }

    protected Entity2D(float x, float y, float deg, float sclX, float sclY) {
        this.transform = new ComponentTransform2D(x, y, deg, sclX, sclY);
    }

    @Override protected          ComponentTransform2D createComponentTransform() { return transform; }
    @Override protected abstract ComponentAudio       createComponentAudio();
    @Override protected abstract ComponentRender2D    createComponentRender();
    @Override protected abstract ComponentCamera      createComponentCamera();
    @Override protected abstract ComponentPhysics2D   createComponentPhysics();
    @Override protected abstract ComponentLogics      createComponentLogics();
    @Override protected abstract ComponentRegion      createComponentRegion();

    @Override public final ComponentTransform2D getComponentTransform() {
        return transform;
    }


}
