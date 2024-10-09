package com.heavybox.jtix.ecs;

/*
The system responsible for updating the transforms (2d and 3d).
Updating is done according to: physics and parent-child relationships.
Note that a transform may also be manipulated from a ComponentLogics.
 */
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
