package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D_3;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.InputMouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.ecs.ComponentCamera2D;
import org.lwjgl.opengl.GL11;

public class SceneTest_2 implements Scene {

    Texture yellow;
    Texture pattern;
    private Renderer2D_3 renderer2D = new Renderer2D_3();
    private ComponentCamera2D componentGraphicsCamera;

    @Override
    public void setup() {
        Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadTexture("assets/textures/pattern.png");
        Assets.finishLoading();
    }

    @Override
    public void start() {
        yellow = Assets.get("assets/textures/yellowSquare.jpg");
        pattern = Assets.get("assets/textures/pattern.png");
        componentGraphicsCamera = new ComponentCamera2D(Graphics.getWindowWidth(), Graphics.getWindowHeight());
        componentGraphicsCamera.update();

    }

    float x = 0, y = 0;

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        if (Input.mouse.isButtonClicked(InputMouse.Button.LEFT)) {
            //Application.windowSetSizeLimits(100, 100, 200, 200);
            //Application.windowFlash();
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;

            //Application.setWindowPosition(300,300);
            System.out.println(Application.getWindowPosX());
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        renderer2D.begin(componentGraphicsCamera.lens.combined);

        renderer2D.drawLineThin(0,0,400,400);
        renderer2D.drawTexture(yellow,x,y,0,1,1);
        renderer2D.drawTexture(pattern,x-200,y,0,1,1);

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
    public void windowFocused(boolean focus) {
        System.out.println("focus: " + focus);
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
