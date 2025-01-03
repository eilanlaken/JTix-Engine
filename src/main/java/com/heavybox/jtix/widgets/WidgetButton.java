package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetButton extends Widget {

    public Color bgColor   = Color.RED;
    public String text     = "Button";
    public Color textColor = Color.WHITE;

    public WidgetButton(float width, float height, String text) {
        super(Widgets.regionCreateRectangle(width, height));
        this.width = width;
        this.height = height;
        this.text = text;
        bgColor = Color.RED;
        textColor = Color.WHITE;
    }

    public WidgetButton(Style text) {

    }

    @Override
    protected void update(float delta) {
        System.out.println("hi");
    }

    @Override
    protected void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY) {
        float width = renderer2D.getTextLineWidth(style.font, text, style.fontSize, true);
        float height = style.fontSize;

        renderer2D.setColor(bgColor);
        renderer2D.drawRectangleFilled(width, height, screenX, screenY, screenDeg, screenSclX, screenSclY);
        renderer2D.setColor(textColor);
        renderer2D.setFont(style.font);
        renderer2D.drawStringLine(text, style.fontSize, true, screenX, screenY, true);
    }

}
