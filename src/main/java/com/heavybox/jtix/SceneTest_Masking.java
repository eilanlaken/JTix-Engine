package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class SceneTest_Masking implements Scene {


    private final Renderer2D renderer2D = new Renderer2D();

    Texture flower;
    TexturePack pack;

    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 70);


    @Override
    public void setup() {


        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);
        Assets.finishLoading();

    }

    @Override
    public void start() {

        pack = Assets.get("assets/atlases/spots.yml");
        flower = new Texture("assets/textures/flower.png");//Assets.get("assets/textures/flower.png");

    }

    int level = 0;
    @Override
    public void update() {



        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.D)) {
            level++;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.A)) {
            level--;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {

        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        // render font
        renderer2D.begin();

        renderer2D.beginStencil();
        renderer2D.setStencilModeIncrement();
        renderer2D.setColor(0.8f,1,0.1f,0.2f);
        renderer2D.drawCircleFilled(50,10,0,0,0,1,1);
        renderer2D.setColor(0.8f,0.1f,1f,0.2f);
        renderer2D.setStencilModeIncrement();
        renderer2D.drawCircleFilled(50,10,49,0,0,1,1);
        renderer2D.endStencil();
//
//        renderer2D.stencilMaskBegin();
//        renderer2D.drawCircleFilled(50,10,0,0,0,1,1);
//        renderer2D.drawCircleFilled(50,10,25,0,0,1,1);
//        renderer2D.stencilMaskEnd();
//
//        renderer2D.applyMaskBegin(5);
//        renderer2D.setColor(Color.MAROON);
//        renderer2D.drawRectangleFilled(200,150,0,0,0,1,1);
//        renderer2D.applyMaskEnd();
//
        renderer2D.enableMasking();
        renderer2D.setMaskingFunctionLessEquals(level);
        //renderer2D.setColor(Color.RED);
        //renderer2D.drawRectangleFilled(200,150,0,0,0,1,1);
        renderer2D.setColor(null);
        renderer2D.drawTexture(flower,0,0,0,1,1);
        renderer2D.disableMasking();

        renderer2D.end();

    }

    @Override
    public void finish() {

    }

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        Scene.super.windowFilesDraggedAndDropped(filePaths);
    }

}
