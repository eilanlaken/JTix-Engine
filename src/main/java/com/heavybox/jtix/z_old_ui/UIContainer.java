package com.heavybox.jtix.z_old_ui;

import com.heavybox.jtix.z_graphics_old.Renderer2D_4;

import java.util.HashSet;
import java.util.Set;

public abstract class UIContainer extends UI {

    /* attributes */
    public Set<UI> contents   = new HashSet<>();
    public boolean resizeable = false;
    public boolean overflowScrollbarX;

    protected UIContainer(float x, float y, float deg, float sclX, float sclY, float[] bounds) {
        super(x, y, deg, sclX, sclY, bounds);
    }

    protected void insert(UI element) {
        if (element == null)                                                         throw new UserInterfaceException(UI.class.getSimpleName() + " element cannot be null");
        if (element == this)                                                         throw new UserInterfaceException("Trying to parent a " + UI.class.getSimpleName() + " to itself.");
        if (element.belongsTo(this))                                        throw new UserInterfaceException(UI.class.getSimpleName() + " element is already a descendant of parent.");
        if (element instanceof UIContainer && this.belongsTo((UIContainer) element)) throw new UserInterfaceException("Trying to create circular dependency in UI elements tree");
        if (element.container != null) element.container.remove(element);
        if (contents == null) contents = new HashSet<>();
        contents.add(element);
        element.container = this;
    }

    protected abstract void setChildrenPositions();

    protected void remove(UI element) {
        if (element.container != this) throw new UserInterfaceException(UI.class.getSimpleName() + " element is not a child of this element to detach.");
        element.container = null;
        contents.remove(element);
    }

    public void getAllDescendants(Set<UI> elements) {
        elements.addAll(contents);
        for (UI child : contents) {
            if (child instanceof UIContainer) {
                UIContainer childContainer = (UIContainer) child;
                childContainer.getAllDescendants(elements);
            }
        }
    }

    @Override
    public void render(Renderer2D_4 renderer2D) {
        if (style.overflow == Style.Overflow.TRUNCATE) {
            renderContainer(renderer2D);
            renderer2D.pushPixelBounds(min_x, min_y, max_x, max_y);
            for (UI ui : contents) {
                ui.render(renderer2D);
            }
            renderer2D.popPixelBounds();
        } else if (style.overflow == Style.Overflow.DO_NOTHING) {
            renderContainer(renderer2D);
            for (UI ui : contents) {
                ui.render(renderer2D);
            }
        } else if (style.overflow == Style.Overflow.RESIZE_TO_FIT) {
            renderContainer(renderer2D);
            // TODO...
        }
    }

    public abstract void renderContainer(Renderer2D_4 renderer2D);
}
