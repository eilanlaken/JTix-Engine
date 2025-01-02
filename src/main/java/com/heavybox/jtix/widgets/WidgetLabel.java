package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetLabel extends Widget {

    public String text;

    public WidgetLabel(float width, float height, String text) {
        super(Widgets.regionCreateRectangle(width, height));
        this.text = text;
    }

    @Override
    public void render(Renderer2D renderer2D) {

    }

}
