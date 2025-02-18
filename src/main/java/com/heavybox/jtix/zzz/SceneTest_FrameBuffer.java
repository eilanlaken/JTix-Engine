package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.widgets_4.Node;
import com.heavybox.jtix.widgets_4.Polygon;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class SceneTest_FrameBuffer implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    private Texture outline;

    private Shader outlineShader;

    private FrameBuffer frameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight());

    @Override
    public void setup() {
        Assets.loadShader("outline", "assets/shaders/graphics-2d-shader-outline.vert", "assets/shaders/graphics-2d-shader-outline.frag");
        Assets.loadTexture("assets/textures/test_outline.png");
        Assets.finishLoading();

        outlineShader = Assets.get("outline");

        //outlineShader = new Shader("assets/shaders/graphics-2d-shader-outline.vert", "assets/shaders/graphics-2d-shader-outline.frag");
        outline = Assets.get("assets/textures/test_outline.png");

    }

    @Override
    public void start() {


    }

    int n = 6;

    @Override
    public void update() {

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) n += 2;

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearColor(1f,1f,1f,1);


        // render font
        FrameBufferBinder.bind(frameBuffer);
        renderer2D.begin();
        renderer2D.setColor(1,0,0,1);
        renderer2D.drawCircleFilled(36, 30,0, 0, 0, 1, 1);
        renderer2D.end();

        FrameBufferBinder.bind(null);
        renderer2D.begin();
        renderer2D.setShader(outlineShader);
        renderer2D.setShaderAttribute("n", n);
        renderer2D.drawTexture(frameBuffer.getColorAttachment(), 0, 0, 0, 1, 1);
        renderer2D.end();


    }

    @Override
    public void finish() {
        frameBuffer.delete();
    }

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }

    static class NodeCircle extends Node {

        Color color = Color.YELLOW.clone();
        float r;

        NodeCircle(float r) {
            this.r = r;
        }

        @Override
        protected void fixedUpdate(float delta) {

        }

        @Override
        protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
            renderer2D.setColor(color);
            renderer2D.drawCircleFilled(r, 15, x, y, deg, sclX, sclY);
        }

        @Override
        public float calculateWidth() {
            return r * 2;
        }

        @Override
        public float calculateHeight() {
            return r * 2;
        }

        public void setPolygon(final Polygon polygon) {
            polygon.setToCircle(r, 15);
        }

    }

}
