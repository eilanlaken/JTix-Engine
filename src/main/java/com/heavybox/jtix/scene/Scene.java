package com.heavybox.jtix.scene;

import com.heavybox.jtix.ecs.EntityContainer;

public abstract class Scene extends EntityContainer {

    public abstract void prepare();
    public abstract void start();

}
