package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.MathUtils;

public class ToolWheatField extends Tool {

    public Color currentColor = new Color();
    public float[] polygon;
    public float[] outline;

    public boolean lines = true;
    public float linesAngle = 30;

    public boolean renderOutline = true;
    public float outlineThickness = 2;

    private final Texture wheatBase;
    private Texture wheatLines;

    public ToolWheatField(Texture wheatBase, Texture wheatLines) {
        this.wheatBase = wheatBase;
        this.wheatLines = wheatLines;
        polygon = new float[] {
                -100,40,
                -100,-40,
                100,-20,
                100,20
        };
        outline = new float[polygon.length + 2];
        for (int i = 0; i < polygon.length; i++) {
            outline[i] = polygon[i];
        }
        outline[outline.length - 2] = polygon[0];
        outline[outline.length - 1] = polygon[1];

        float tint = MathUtils.randomUniformFloat(-0.15f,0);
//        currentColor.r = 1 + tint;
//        currentColor.g = 1 + tint;
//        currentColor.b = 1 + tint;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // draw base
        renderer2D.setColor(1f, 1f, 1f, 0.8f);
        renderer2D.drawPolygonFilled(polygon, wheatBase, x, y, deg, sclX, sclY);
        // draw lines
        renderer2D.setColor(1,1,1,1);
        renderer2D.drawPolygonFilled(outline, wheatLines, uv -> uv.rotateDeg(linesAngle), x, y, deg, sclX, sclY);

        // draw outline
        renderer2D.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        renderer2D.drawCurveFilled(wheatBase, 3, 5, outline, x, y, deg, sclX, sclY);
        renderer2D.setColor(1,1,1,1);
    }

}
