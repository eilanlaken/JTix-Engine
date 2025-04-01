package com.heavybox.jtix.zzz.v1;

public abstract class Command {

    public boolean isAnchor = false;
    public float x, y, deg, sclX = 1, sclY = 1;

    protected abstract void execute();
    protected abstract void undo();

}
