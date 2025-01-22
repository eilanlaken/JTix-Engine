package com.heavybox.jtix.ecs;

import com.heavybox.jtix.z_deprecated.z_graphics_old.Renderer3D;
import com.heavybox.jtix.math.Matrix4x4;

public abstract class ComponentRender3D implements ComponentRender {

    public abstract void render(Renderer3D renderer3D, final Matrix4x4 transform);

}
