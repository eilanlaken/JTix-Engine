package com.heavybox.jtix.ui;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class UIElement {

    protected UIElementContainer parent;

    public boolean descendantOf(UIElement ancestor) {
        if (!(ancestor instanceof UIElementContainer)) return false;
        UIElementContainer container = (UIElementContainer) ancestor;
        if (container.children.contains(this)) return true;
        boolean result = false;
        for (UIElement child : container.children) {
            result = result || descendantOf(child);
        }
        return result;
    }

    public abstract void onClick();
    public abstract void onPress();


    public abstract void draw(Renderer2D renderer);

}
