package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Keyboard;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;
import com.heavybox.jtix.graphics.Font;

public class SceneTest_Fonts_4 implements Scene {


    private Renderer2D renderer2D = new Renderer2D();


    private String fontPath = "assets/fonts/OpenSans-Italic.ttf";

    Font font;
    Font font2;

    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.finishLoading();


    }

    Vector2[] positions;
    Color[] colors;
    @Override
    public void start() {

        font = Assets.get("assets/fonts/OpenSans-Regular.ttf");
        font2 = Assets.get("C:\\Windows\\Fonts\\ahronbd.ttf");

        positions = new Vector2[40];
        colors = new Color[40];
        for (int i = 0; i < 40; i++) {
            positions[i] = new Vector2(MathUtils.randomUniformFloat(-640,640), MathUtils.randomUniformFloat(-480,480));
            colors[i] = new Color(MathUtils.randomUniformFloat(0f,1f), MathUtils.randomUniformFloat(0,1), MathUtils.randomUniformFloat(0,1), 1);
        }
    }


    float scale = 1;
    int index = 0;

    StringBuffer text = new StringBuffer();

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
            text.append((char)  codepoint);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.A)) {
            index++;
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
            Graphics.setCursorResizeNESW();
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.R)) {
            Graphics.setCursorResizeNWSE();
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.T)) {
            Graphics.setCursorResizeAll();
        }

        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            Graphics.setCursorResizeVertical();

        }
        if (Input.mouse.isButtonClicked(Mouse.Button.MIDDLE)) {
            Graphics.setCursorNone();
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            scale += 0.003f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            scale -= 0.003f;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();


        //renderer2D.drawTextLine("מה נשמע", 54, font2, true,0, 0);
        //renderer2D.drawTextLine("Hello world", 54, font, true,0, -54);
        renderer2D.drawTextLine(text.toString(), 64, font, true,0, 0, false);

        for (int i = 0; i < 15; i++) {
            renderer2D.setColor(colors[i]);
            renderer2D.drawCircleFilled(60,40,positions[i].x,positions[i].y,0,1,1);
        }

        renderer2D.pushPixelBounds(0,0,400,400);
        for (int i = 15; i < 25; i++) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawCircleFilled(60,40,positions[i].x,positions[i].y,0,1,1);
        }
        renderer2D.popPixelBounds();

        for (int i = 25; i < 40; i++) {
            renderer2D.setColor(colors[i]);
            renderer2D.drawCircleFilled(60,40,positions[i].x,positions[i].y,0,1,1);
        }


//        renderer2D.setColor(0.1686f, 0.1686f,0.1686f,1);
//        renderer2D.drawRectangleFilled(36, 36,0,Graphics.getWindowHeight()/2f - 36 /2f,0,1,1);
//        renderer2D.setColor(1,1,1,1);
//        renderer2D.drawLineFilled(-8,0,8,0,1,0,Graphics.getWindowHeight()/2f - 36/2f,45,1,1);
//        renderer2D.drawLineFilled(-8,0,8,0,1,0,Graphics.getWindowHeight()/2f - 36/2f,-45,1,1);
//
//        renderer2D.setColor(0.1686f, 0.1686f,0.1686f,1);
//        renderer2D.drawRectangleFilled(36, 36,-36,0,0,1,1);
//        renderer2D.setColor(1,1,1,1);
//        renderer2D.drawRectangleThin(12f,12f,-36,0,0,1,1);

//
//        renderer2D.setColor(0.1686f, 0.1686f,0.1686f,1);
//        renderer2D.drawRectangleFilled(36, 36,-72,0,0,1,1);
//        renderer2D.setColor(1,1,1,1);
//        renderer2D.drawLineFilled(-8,0,8,0,1,-72,0,0,1,1);

        //renderer2D.drawString("Lorem Ipsum is simply dummy text of the printing and typesetting aaaaaaaaaa", font, 0,0,0);

//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);
//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);
//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);
//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);
//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);
//        renderer2D.drawString("What, Cunt?!", font, 0,0,0,1,1, scale);


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
