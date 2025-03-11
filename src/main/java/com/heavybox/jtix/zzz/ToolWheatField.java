package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.MathUtils;

public class ToolWheatField extends Tool {

    public Color currentColor = new Color();
    public ArrayFloat currentPolygon = new ArrayFloat(true, 16);

    public boolean lines = true;
    public float linesAngle = 30;

    public boolean outline = true;
    public float outlineThickness = 2;

    private Texture wheatBase;
    private Texture wheatLine;

    public ToolWheatField(Texture wheatBase, Texture wheatLines) {
        currentPolygon.add(-100,40);
        currentPolygon.add(-100,-40);
        currentPolygon.add(100,-20);
        currentPolygon.add(100,20);
        float tint = MathUtils.randomUniformFloat(-0.15f,0);
//        currentColor.r = 1 + tint;
//        currentColor.g = 1 + tint;
//        currentColor.b = 1 + tint;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

}
