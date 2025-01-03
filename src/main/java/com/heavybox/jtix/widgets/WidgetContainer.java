package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Renderer2D;
import org.jetbrains.annotations.NotNull;

public abstract class WidgetContainer extends Widget {

    protected WidgetContainer(@NotNull Region region) {
        super(region);
    }

    @Override
    public void render(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY) {
        renderContainerBackground(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
        if (style.overflow == Style.Overflow.TRIM) renderer2D.pushPixelBounds(0, 0, 1800, 1800); // TODO: calc min and max.
        for (Widget widget : children) {
            widget.render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
        if (style.overflow == Style.Overflow.TRIM) renderer2D.popPixelBounds();
        renderContainerForeground(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    @Override
    protected void update(float delta) {
        setChildrenPositions();
        setChildrenBoxDimensions();
    }

    protected abstract void renderContainerBackground(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY);
    protected abstract void renderContainerForeground(Renderer2D renderer2D, float screenX, float screenY, float screenDeg, float screenSclX, float screenSclY);

    protected abstract void setChildrenPositions();
    protected abstract void setChildrenBoxDimensions();

}
