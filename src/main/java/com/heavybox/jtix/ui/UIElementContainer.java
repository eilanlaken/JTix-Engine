package com.heavybox.jtix.ui;

import java.util.HashSet;
import java.util.Set;

public abstract class UIElementContainer extends UIElement {

    public Set<UIElement> children;

    // TODO: implement
    private void addChild(UIElement element) {
        if (element == null) throw new UIException(UIElement.class.getSimpleName() + " element cannot be null");
        if (element == this) throw new UIException("Trying to parent a " + UIElement.class.getSimpleName() + " to itself.");
        if (element.parent != null) throw new UIException(UIElement.class.getSimpleName() + " element already has a parent.");
        // .. conduct more tests: this cannot be a descendant of element etc

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
