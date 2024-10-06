package com.heavybox.jtix.ecs_3;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class ComponentRender2D implements ComponentRender {

    public abstract void render(Renderer2D renderer2D, ComponentTransform2D transform2D);

}
