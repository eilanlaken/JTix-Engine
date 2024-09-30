package com.heavybox.jtix.ecs_2;

public abstract class System {

    protected final EntityContainer container;

    public boolean active = true;

    protected System(final EntityContainer container) {
        this.container = container;
    }

    protected abstract boolean shouldProcess(final Entity entity);
    protected abstract void add(Entity entity);
    protected abstract void remove(Entity entity);
    protected abstract void frameUpdate(float delta);
    protected abstract void fixedUpdate(float delta);

}
