package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

public class WidgetButton extends Widget {

    public Color bgColor;
    public String text;
    public Color textColor;

    public WidgetButton(float width, float height, String text) {
        super(Widgets.regionCreateRectangle(width, height));
        this.width = width;
        this.height = height;
        this.text = text;
        bgColor = Color.RED;
        textColor = Color.WHITE;
    }

    @Override
    protected void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY) {
        renderer2D.setColor(bgColor);
        renderer2D.drawRectangleFilled(width, height, screenX, screenY, screenDeg, screenSclX, screenSclY);
        renderer2D.setColor(textColor);
        renderer2D.drawStringLine(text, 16, null, true, screenX, screenY, true);
    }

}
