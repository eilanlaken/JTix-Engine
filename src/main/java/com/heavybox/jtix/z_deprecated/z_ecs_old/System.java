package com.heavybox.jtix.z_deprecated.z_ecs_old;

public abstract class System {

    public final EntityContainer container;
    protected boolean enabled;

    public System(final EntityContainer container) {
        this.container = container;
    }

    protected abstract boolean shouldProcess(final Entity entity);
    protected abstract void    add(Entity entity);
    protected abstract void    remove(Entity entity);
    protected abstract void    frameUpdate(float delta);
    protected abstract void    fixedUpdate(float delta);

}
