package com.heavybox.jtix.ecs_3;

import org.jetbrains.annotations.NotNull;

public abstract class Entity {

    protected EntityContainer container;

    protected int handle;
    protected int componentsBitmask;
    public final int categoryBitmask;

    protected final ComponentTransform transform;

    protected Entity(final ComponentTransform transform) {
        this.categoryBitmask = getLayer().categoryBitmask;
        this.transform = transform;
    }

    public abstract @NotNull EntityLayer getLayer();

    protected abstract ComponentAudio   createComponentAudio();
    protected abstract ComponentRender  createComponentRender();
    protected abstract ComponentCamera  createComponentCamera();
    protected abstract ComponentPhysics createComponentPhysics();
    protected abstract ComponentLogics  createComponentLogics();
    protected abstract ComponentSignals createComponentSignals();
    protected abstract ComponentRegion  createComponentRegion();

    public final ComponentTransform createComponentTransform() { return transform;}
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
