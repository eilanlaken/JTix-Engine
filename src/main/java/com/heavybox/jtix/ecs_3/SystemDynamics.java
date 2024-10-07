package com.heavybox.jtix.ecs_3;

public class SystemDynamics implements System {

    private final EntityContainer container;

    SystemDynamics(final EntityContainer container) {
        this.container = container;
    }

    @Override
    public boolean shouldProcess(Entity entity) {
        return false;
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
