package com.heavybox.jtix.ecs_3;

public interface System {

    boolean shouldProcess(final Entity entity);

    void add(Entity entity);
    void remove(Entity entity);

    void frameUpdate(float delta);
    void fixedUpdate(float delta);

}
