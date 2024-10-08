package com.heavybox.jtix.z_ecs_old_1;

public class ComponentLogicsScript implements ComponentLogics {

    protected Entity entity;

    public final void setEntity(final Entity entity) {
        if (this.entity != null) throw new IllegalStateException(Entity.class.getSimpleName() + " " + this.entity + " is already set for " + ComponentLogicsScript.class.getSimpleName() + " " + this);
        else this.entity = entity;
    }

    public void start() {}
    public void frameUpdate(float delta) {}
    public void fixedUpdate(float delta) {}
    public void onDestroy() {}

}
