package com.heavybox.jtix.zzz;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class ToolStampProps extends Tool {

    public float scale = 0.20f;
    public int index = 0;


    // TODO: in the overlay, draw a preview with 0.5f opacity.

    public void selectRandom() {
        index = MathUtils.randomUniformInt(0, MapTokenRuralProp.regionsProps.length);
    }

    public void selectNext() {
        index++;
        index %= MapTokenRuralProp.regionsProps.length;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(0,1,0,1);
        renderer2D.drawCircleThin(20, 30, x, y, deg, sclX, sclY);
        renderer2D.setColor(Color.WHITE);
    }

}
