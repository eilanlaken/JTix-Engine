package com.heavybox.jtix.ecs;

public abstract class ComponentLogicsScript implements ComponentLogics {

    protected Entity  entity = null;
    protected boolean active = true;

    final void setEntity(final Entity entity) {
        if (this.entity != null) throw new ECSException(Entity.class.getSimpleName() + " " + this.entity + " is already set for " + ComponentLogicsScript.class.getSimpleName() + " " + this);
        else this.entity = entity;
    }

    public abstract void start();
    public abstract void frameUpdate(float delta);
    public abstract void fixedUpdate(float delta);
    public abstract void onDestroy();

    public boolean handleSignal(final Object signal) {
        return false;
    }

}
