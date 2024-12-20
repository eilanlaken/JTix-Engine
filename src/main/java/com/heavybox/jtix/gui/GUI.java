package com.heavybox.jtix.gui;

public final class GUI {


    /* API for creating buttons, paragraphs etc. */
    public static UIButton createButton(float width, float height, String text) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        UIButton btn = new UIButton(text);
        return btn;
    }

    private static float[] calculateBounds() {

        return null;
    }

}
