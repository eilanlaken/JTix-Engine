package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import org.jetbrains.annotations.NotNull;

public abstract class Entity2D extends Entity {

    public final ComponentTransform2D transform;

    private Entity2D parent;
    private Array<Entity2D> children;

    protected Entity2D() {
        this(0,0,0,1,1);
    }

    protected Entity2D(float x, float y, float z, float deg) {
        this(x, y, deg, 1, 1);
    }

    protected Entity2D(float x, float y, float deg, float sclX, float sclY) {
        this.transform = new ComponentTransform2D(x, y, deg, sclX, sclY);
    }

    // TODO
    protected void setParent(Entity2D parent, boolean keepTransform) {

    }

    // TODO
    protected void clearParent(Entity2D parent, boolean keepTransform) {

    }

    protected void attachChild(Entity2D child) {
        if (child == null) throw new ECSException("Child entity cannot be null");
        if (child == this) throw new ECSException("Cannot add an " + Entity2D.class.getSimpleName() + " as a child to itself.");
        if (child.parent != null) throw new ECSException("child " + Entity2D.class.getSimpleName() + " already has a parent. Must un-parent first.");
        if (this.children != null && this.children.contains(child,true)) throw new ECSException(Entity2D.class.getSimpleName() + " child is already a child of this " + Entity2D.class.getSimpleName());
        if (this.children == null) this.children = new Array<>(false, 1);
        this.children.add(child);
        child.parent = this;
    }

    @Override protected          ComponentTransform2D createComponentTransform() { return transform; }
    @Override protected abstract ComponentAudio       createComponentAudio();
    @Override protected abstract ComponentRender2D    createComponentRender();
    @Override protected abstract ComponentCamera2D    createComponentCamera();
    @Override protected abstract ComponentPhysics2D   createComponentPhysics();
    @Override protected abstract ComponentLogics      createComponentLogics();
    @Override protected abstract ComponentRegion      createComponentRegion();

    @Override public final ComponentTransform2D getComponentTransform() {
        return transform;
    }

    public abstract @NotNull EntityLayer2D getLayer();

}
