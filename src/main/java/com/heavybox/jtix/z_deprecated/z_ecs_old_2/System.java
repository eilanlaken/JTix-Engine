package com.heavybox.jtix.z_deprecated.z_ecs_old_2;

public interface System {

    boolean active = true;

    boolean shouldProcess(final Entity entity);

    void add(Entity entity);
    void remove(Entity entity);

    void frameUpdate(float delta);
    void fixedUpdate(float delta);

}
