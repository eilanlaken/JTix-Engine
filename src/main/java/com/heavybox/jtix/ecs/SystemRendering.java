package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class SystemRendering implements System {

    public static final int SYSTEM_RENDERING_BITMASK = Component.Type.RENDER.bitmask | Component.Type.CAMERA.bitmask;
    private final EntityContainer container;

    private final Array<Entity2D> renders_2d = new Array<>(false, 10);
    private final Array<Entity2D> cameras_2d = new Array<>(false, 10);
    private final Array<Entity3D> renders_3d = new Array<>(false, 10);
    private final Array<Entity3D> cameras_3d = new Array<>(false, 10);

    SystemRendering(final EntityContainer container) {
        this.container = container;
    }

    @Override
    public boolean shouldProcess(Entity entity) {
        return (entity.bitmask & SYSTEM_RENDERING_BITMASK) > 0;
    }

    @Override
    public void add(Entity entity) {

    }

    @Override
    public void remove(Entity entity) {

    }

    @Override
    public void frameUpdate(float delta) {

    }

    @Override
    public void fixedUpdate(float delta) {

    }

}
