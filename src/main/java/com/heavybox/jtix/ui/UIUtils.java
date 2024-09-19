package com.heavybox.jtix.ui;

public final class UIUtils {

    private UIUtils() {}

    public static boolean connected(UIElement a, UIElement b) {
        return descendant(a, b) || descendant(b, a);
    }

    public static boolean descendant(UIElement ancestor, UIElement descendant) {
        if (!(ancestor instanceof UIElementContainer)) return false;
        UIElementContainer container = (UIElementContainer) ancestor;
        if (container.children.contains(descendant)) return true;
        boolean result = false;
        for (UIElement child : container.children) {
            result = result || descendant(child, descendant);
        }
        return result;
    }

}
