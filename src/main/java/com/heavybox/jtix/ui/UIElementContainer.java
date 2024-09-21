package com.heavybox.jtix.ui;

import java.util.HashSet;
import java.util.Set;

public abstract class UIElementContainer extends UIElement {

    protected Set<UIElement> children;

    public UIElementContainer(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY) {
        super(x, y, z, degX, degY, degZ, sclX, sclY);
    }

    private void addChild(UIElement element) {
        if (element == null) throw new UIException(UIElement.class.getSimpleName() + " element cannot be null");
        if (element == this) throw new UIException("Trying to parent a " + UIElement.class.getSimpleName() + " to itself.");
        if (element.descendantOf(this)) throw new UIException(UIElement.class.getSimpleName() + " element is already a descendant of parent.");
        if (this.descendantOf(element)) throw new UIException("Trying to create circular dependency in UI elements tree");

        if (element.parent != null) element.parent.removeChild(element);
        if (children == null) children = new HashSet<>();
        children.add(element);
        element.parent = this;
    }

    public void removeChild(UIElement element) {
        if (element.parent != this) throw new UIException(UIElement.class.getSimpleName() + " element is not a child of this element to detach.");
        element.parent = null;
        children.remove(element);
    }



}
