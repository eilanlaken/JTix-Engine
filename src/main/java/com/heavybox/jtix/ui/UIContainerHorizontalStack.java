package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D_3;

import java.util.HashSet;
import java.util.Set;

public class UIContainerHorizontalStack extends UIContainer {

    private final Set<UI> left  = new HashSet<>();
    private final Set<UI> right = new HashSet<>();

    public UIContainerHorizontalStack(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        super(x, y, deg, sclX, sclY, bounds);
    }

    public void addChildToLeft(UI child) {
        super.addChild(child);
        left.add(child);
    }

    public void addChildToRight(UI child) {
        super.addChild(child);
        right.add(child);
    }


    @Override
    public void render(Renderer2D_3 renderer2D) {

    }
}
