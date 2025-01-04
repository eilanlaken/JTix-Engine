package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetButton extends Widget {


    public String text     = "Button";


    public WidgetButton() {

    }

    @Deprecated
    public WidgetButton(float width, float height, String text) {
        super(Widgets.regionCreateRectangle(width, height));
        this.text = text;
    }

    public WidgetButton(Style text) {

    }

    @Override
    protected void update(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY) {
        renderer2D.setColor(style.backgroudColor);
        renderer2D.drawRectangleFilled(boxWidth, boxHeight,

                style.borderRadiusTopLeft, style.borderRefinementTopLeft,
                style.borderRadiusTopRight, style.borderRefinementTopRight,
                style.borderRadiusBottomRight, style.borderRefinementBottomRight,
                style.borderRadiusBottomLeft, style.borderRefinementBottomLeft,

                screenX, screenY, screenDeg, screenSclX, screenSclY);
        renderer2D.setColor(style.textColor);
        renderer2D.setFont(style.font);
        renderer2D.drawStringLine(text, style.fontSize, true, screenX, screenY, true);
    }

    @Override
    protected int getInnerWidth() {
        return (int) Renderer2D.getTextLineWidth(style.font, text, style.fontSize,true);
    }

    @Override
    protected int getInnerHeight() {
        return Math.max(0, style.fontSize);
    }

}
