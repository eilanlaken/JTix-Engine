package com.heavybox.jtix.z_ecs_old;

public final class SystemDynamics extends System {

    SystemDynamics(final EntityContainer container) {
        super(container);
    }

    @Override
    protected boolean shouldProcess(Entity entity) {
        return true;
    }

    @Override
    protected void add(Entity entity) {

    }

    @Override
    protected void remove(Entity entity) {

    }

    @Override
    protected void frameUpdate(float delta) {

    }

    @Override
    protected void fixedUpdate(float delta) {

    }

}
