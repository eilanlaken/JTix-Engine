package com.heavybox.jtix.ecs;

public abstract class Entity {

    protected EntityContainer container;
    protected int handle;
    protected int componentsBitmask;

    private final ComponentTransform transform;
    protected final Enum category;
    protected final int categoryBitmask;

    protected Entity(Enum category) {
        this(category,0,0,0,0,0,0,1,1,1);
    }

    protected Entity(Enum category, float x, float y, float z, float degZ) {
        this(category, x, y, z, 0, 0, degZ, 1, 1, 1);
    }

    protected Entity(Enum category,
                     float x, float y, float z,
                     float degX, float degY, float degZ,
                     float sclX, float sclY, float sclZ) {
        this.category = category;
        this.categoryBitmask = ECSUtils.getCategoryBitmask(category);
        this.transform = new ComponentTransform(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    final              ComponentTransform createComponentTransform() { return transform;}
    protected abstract ComponentAudio     createComponentAudio();
    protected abstract ComponentGraphics  createComponentGraphics();
    protected abstract ComponentPhysics   createComponentPhysics();
    protected abstract ComponentLogics    createComponentLogics();
    protected abstract ComponentSignals   createComponentSignals();
    protected abstract ComponentRegion    createComponentRegion();

    public final ComponentTransform getComponentTransform() {
        return transform;
    }

    public final ComponentAudio getComponentAudio() {
        if (handle == -1) return null;
        return container.audios.get(handle);
    }

    public final ComponentGraphics getComponentGraphics() {
        if (handle == -1) return null;
        return container.graphics.get(handle);
    }

    public final ComponentPhysics getComponentPhysics() {
        if (handle == -1) return null;
        return container.physics.get(handle);
    }

    public final ComponentLogics getComponentLogics() {
        if (handle == -1) return null;
        return container.logics.get(handle);
    }

    public final ComponentSignals getComponentSignals() {
        if (handle == -1) return null;
        return container.signals.get(handle);
    }

    public final ComponentRegion getComponentRegion() {
        if (handle == -1) return null;
        return container.regions.get(handle);
    }

}
