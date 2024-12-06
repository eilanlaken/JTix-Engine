package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_LINE_WIDTH_RANGE;
import static org.lwjgl.opengl.GL11.glGetFloatv;

public class SceneTest_Fonts_3 implements Scene {


    private Renderer2D_3 renderer2D = new Renderer2D_3();

    Texture yellow;
    String font_path = "assets/fonts/OpenSans-Italic-32.yml";

    @Override
    public void setup() {
        Assets.loadTexture("assets/textures/yellowSquare.jpg");

        Assets.finishLoading();
    }

    @Override
    public void start() {
        yellow = Assets.get("assets/textures/yellowSquare.jpg");

        float[] lineWidth = new float[2];
        glGetFloatv(GL_LINE_WIDTH_RANGE, lineWidth);
        System.out.println(lineWidth[0]);
        System.out.println(lineWidth[1]);
    }


    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }
        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {

        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();

        renderer2D.drawTexture(yellow, 0,0,0,1,1);

        renderer2D.end();

    }

    @Override
    public void finish() {
        System.out.println("finish");
    }


    @Override
    public void windowResized(int width, int height) {
        System.out.println("resized: " + width + ", " + height);
    }

    @Override
    public void windowMinimized(boolean minimized) {
        System.out.println(minimized);
    }

    @Override
    public void windowMaximized(boolean maximized) {
        Scene.super.windowMaximized(maximized);
    }

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }

}
