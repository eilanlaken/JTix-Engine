package com.heavybox.jtix.zzz_project;

import com.heavybox.jtix.math.Matrix4x4;

public abstract class GameObject {

    public boolean isStatic; // TODO: make final
    public Matrix4x4 transform = new Matrix4x4();

    public void update(float delta) {}
    public void render() {}

}
