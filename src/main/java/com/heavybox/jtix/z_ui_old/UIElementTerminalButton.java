package com.heavybox.jtix.z_ui_old;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.z_graphics_old.Font;
import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.graphics.Texture;

public class UIElementTerminalButton extends UIElementTerminal {

    // in case of a text
    public Font font;
    public String text;

    // in case of a textured button
    public Texture texture;
    public Color tint;

    // in case of a complex mesh
    public float[] mesh;

    // in case of rectangular button (can be deformed into an ellipse).
    public float width;
    public float height;
    public float rTopLeft;
    public float nTopLeft;
    public float rBottomLeft;
    public float nBottomLeft;
    public float rBottomRight;
    public float nBottomRight;
    public float rTopRight;
    public float nTopRight;

    protected UIElementTerminalButton(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, Font font, String text, Texture texture, Color tint, float[] mesh, float width, float height, float rTopLeft, float nTopLeft, float rBottomLeft, float nBottomLeft, float rBottomRight, float nBottomRight, float rTopRight, float nTopRight) {
        super(x, y, z, degX, degY, degZ, sclX, sclY);
        this.font = font;
        this.text = text;
        this.texture = texture;
        this.tint = tint;
        this.mesh = mesh;
        this.rTopLeft = rTopLeft;
        this.nTopLeft = nTopLeft;
        this.rBottomLeft = rBottomLeft;
        this.nBottomLeft = nBottomLeft;
        this.rBottomRight = rBottomRight;
        this.nBottomRight = nBottomRight;
        this.rTopRight = rTopRight;
        this.nTopRight = nTopRight;
    }

    @Override
    public void onMouseEnter() {

    }

    @Override
    public void onMouseExit() {

    }

    @Override
    public void onMouseLeftDown() {

    }

    @Override
    public void onMouseLeftUp() {

    }

    @Override
    public void onMouseMiddleDown() {

    }

    @Override
    public void onMouseMiddleUp() {

    }

    @Override
    public void onMouseRightDown() {

    }

    @Override
    public void onMouseRightUp() {

    }

    @Override
    public void onMouseScroll() {

    }

    @Override
    public void draw(Renderer2D_old renderer) {
        if (mesh != null) {
            renderer.drawMeshFilled(mesh, texture, x, y, degX, degY, degZ, sclX, sclY);
        }

        if (width != 0 && height != 0) {
            renderer.setTint(tint);
            //renderer.drawRectangleFilled(width, height, );
        }

        if (font != null && text != null) {

            //renderer.drawText();
        }
    }
}
