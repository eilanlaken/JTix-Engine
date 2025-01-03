package com.heavybox.jtix.z_old_widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

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
        renderer2D.drawStringLine(text, 18, null, true, screenX, screenY, true);
    }

}
