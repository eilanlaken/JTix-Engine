package com.heavybox.jtix.z_ecs_old_2;

import org.jetbrains.annotations.NotNull;

public abstract class Entity {

    protected EntityContainer container;

    protected int handle;
    protected int componentsBitmask;

    private final ComponentTransform transform;
    public final int categoryBitmask;

    protected Entity() {
        this(0,0,0,0,0,0,1,1,1);
    }

    protected Entity(float x, float y, float z, float degZ) {
        this(x, y, z, 0, 0, degZ, 1, 1, 1);
    }

    protected Entity(float x, float y, float z, float degZ, float sclX, float sclY) {
        this(x, y, z, 0, 0, degZ, 1, sclX, sclY);
    }

    protected Entity(
                     float x, float y, float z,
                     float degX, float degY, float degZ,
                     float sclX, float sclY, float sclZ) {
        this.categoryBitmask = getLayer().categoryBitmask;
        this.transform = new ComponentTransform(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    final              ComponentTransform createComponentTransform() { return transform;}
    protected abstract ComponentAudio     createComponentAudio();
    protected abstract ComponentRender    createComponentRender();
    protected abstract ComponentCamera    createComponentCamera();
    protected abstract ComponentPhysics   createComponentPhysics();
    protected abstract ComponentLogics    createComponentLogics();
    protected abstract ComponentSignals   createComponentSignals();
    protected abstract ComponentRegion    createComponentRegion();

    public abstract @NotNull EntityLayer getLayer();

    public final ComponentTransform getComponentTransform() {
        return transform;
    }

    public final ComponentAudio getComponentAudio() {
        if (handle == -1) return null;
        return container.audios.get(handle);
    }

    public final ComponentRender getComponentRender() {
        if (handle == -1) return null;
        return container.renders.get(handle);
    }

    public final ComponentCamera getComponentCamera() {
        if (handle == -1) return null;
        return container.cameras.get(handle);
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

    public final Component getComponent(@NotNull Component.Type type) {
        if (handle == -1) return null;

        return switch (type) {
            case AUDIO     -> container.audios.get(handle);
            case RENDER    -> container.renders.get(handle);
            case CAMERA    -> container.cameras.get(handle);
            case LOGICS    -> container.logics.get(handle);
            case PHYSICS   -> container.physics.get(handle);
            case REGION    -> container.regions.get(handle);
            case SIGNALS   -> container.signals.get(handle);
            case TRANSFORM -> container.transforms.get(handle);
        };
    }

}
