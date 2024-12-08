package com.heavybox.jtix.ui;

import java.util.HashSet;
import java.util.Set;

public abstract class UIContainer extends UI {

    protected Set<UI> children = new HashSet<>();

    protected UIContainer(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        super(x, y, deg, sclX, sclY, bounds);
    }

    private void addChild(UI element) {
        if (element == null)                                                         throw new UserInterfaceException(UI.class.getSimpleName() + " element cannot be null");
        if (element == this)                                                         throw new UserInterfaceException("Trying to parent a " + UI.class.getSimpleName() + " to itself.");
        if (element.belongsTo(this))                                        throw new UserInterfaceException(UI.class.getSimpleName() + " element is already a descendant of parent.");
        if (element instanceof UIContainer && this.belongsTo((UIContainer) element)) throw new UserInterfaceException("Trying to create circular dependency in UI elements tree");

        if (element.parent != null) element.parent.removeChild(element);
        if (children == null) children = new HashSet<>();
        children.add(element);
        element.parent = this;
    }

    public void removeChild(UI element) {
        if (element.parent != this) throw new UserInterfaceException(UI.class.getSimpleName() + " element is not a child of this element to detach.");
        element.parent = null;
        children.remove(element);
    }

    public void getAllDescendants(Set<UI> elements) {
        elements.addAll(children);
        for (UI child : children) {
            if (child instanceof UIContainer) {
                UIContainer childContainer = (UIContainer) child;
                childContainer.getAllDescendants(elements);
            }
        }
    }

}
