package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.Vector2;

public abstract class ForceField {

    protected final World world;

    protected ForceField(World world) {
        this.world = world;
    }

    public abstract void calculateForce(Body body, Vector2 out);

}
