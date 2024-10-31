package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D_3;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.InputMouse;
import org.lwjgl.opengl.GL11;

public class SceneTestLoading implements Scene {

    Texture yellow = null;//new Texture("assets/textures/yellowSquare.jpg");
    private Renderer2D_3 renderer2D = new Renderer2D_3();

    @Override
    public void setup() {

    }

    @Override
    public void start() {
        Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadTexture("assets/textures/pattern.png");
    }

    @Override
    public void update() {
        if (Input.mouse.isButtonClicked(InputMouse.Button.LEFT)) {
            Application.windowSetSizeLimits(100, 100, 200, 200);
            Application.windowFlash();
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        if (!Assets.isLoadingInProgress()) {
            Application.playScene(new SceneTest());
        }

    }

    @Override
    public void finish() {


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
