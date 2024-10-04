package com.heavybox.jtix.ecs_2;

public interface System {

    boolean active = true;

    boolean shouldProcess(final Entity entity);

    void add(Entity entity);
    void remove(Entity entity);

    void frameUpdate(float delta);
    void fixedUpdate(float delta);

}
