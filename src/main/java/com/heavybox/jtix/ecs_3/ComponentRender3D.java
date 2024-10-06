package com.heavybox.jtix.ecs_3;

import com.heavybox.jtix.graphics.Renderer3D;

public abstract class ComponentRender3D implements ComponentRender {

    public abstract void render(Renderer3D renderer3D, ComponentTransform3D transform3D);

}
