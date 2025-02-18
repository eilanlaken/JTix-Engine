package com.heavybox.jtix.zzz;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class SceneTest_Outline_Shader implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    private Texture outline;

    Shader outlineShader;

    @Override
    public void setup() {
        Assets.loadShader("outline", "assets/shaders/graphics-2d-shader-outline.vert", "assets/shaders/graphics-2d-shader-outline.frag");
        Assets.loadTexture("assets/textures/test_outline.png");
        Assets.finishLoading();

        outlineShader = Assets.get("outline");
        //outlineShader = new Shader("assets/shaders/graphics-2d-shader-outline.vert", "assets/shaders/graphics-2d-shader-outline.frag");
        outline = Assets.get("assets/textures/test_outline.png");

        System.out.println(Arrays.toString(outlineShader.uniformNames));

    }

    @Override
    public void start() {


    }

    @Override
    public void update() {



        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearColor(1f,1f,1f,1);


        // render font
        renderer2D.begin();
        renderer2D.setShader(outlineShader);
        outlineShader.bindUniform("n", 26);
        renderer2D.drawTexture(outline, 0, 0, 0, 1, 1);
        renderer2D.end();

    }

    float phase;
    @Override
    public void finish() {

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
