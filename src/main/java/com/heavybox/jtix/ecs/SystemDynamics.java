package com.heavybox.jtix.ecs;

public class SystemDynamics extends System {

    SystemDynamics(final EntityContainer container) {
        super(container);
    }

    @Override
    protected boolean shouldProcess(Entity entity) {
        return false;
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
