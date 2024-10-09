package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class SystemLogics implements System {

    private final EntityContainer container;

    private Array<Entity> entities = new Array<>(false, 10);

    private Array<Object> signalsToSend  = new Array<>(false, 5);
    private Array<Entity> signalsTargets = new Array<>(false, 5);

    SystemLogics(final EntityContainer container) {
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

    public void sendSignal(final Object signal, final Entity target) {
        signalsToSend.add(signal);
        signalsTargets.add(target);
    }

}