package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_deprecated.z_graphics_old.Renderer2D_4;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_1 implements Scene {


    private Renderer2D_4 renderer2D = new Renderer2D_4();


    private String fontPath = "assets/fonts/OpenSans-Italic.ttf";

    Texture flower;

    Font font;
    Font font2;

    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.loadTexture("assets/textures/flower.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, 1);

        Assets.finishLoading();


    }

    Vector2[] positions;
    Color[] colors;
    @Override
    public void start() {

        flower = Assets.get("assets/textures/flower.png");
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

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
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
        //GL11.glClearColor(0.129f, 0.129f, 0.129f,1);


        // render font
        renderer2D.begin();


        //renderer2D.drawTextLine("מה נשמע", 54, font2, true,0, 0);
        //renderer2D.drawTextLine("Hello world", 54, font, true,0, -54);


        renderer2D.setColor(Color.WHITE);
        renderer2D.drawCircleFilled(15,40,0,200,0,1,1);



        renderer2D.setColor(0.090f, 0.090f, 0.090f,1);
        //renderer2D.drawRectangleFilled(250,Graphics.getWindowHeight() * 0.9f,40,1,0,0,0,1,1);
        if (false) renderer2D.drawRectangleFilled(250,Graphics.getWindowHeight() * 0.9f, flower,
                0,
                0,
                0,
                0,

                1,
                2,
                20,
                2,

                0,0,0,1,1);

        renderer2D.setColor(Color.RED);
        renderer2D.drawTextLine(text.toString(), 64, null, true,0, 0, false);

        renderer2D.setColor(Color.WHITE);
        //renderer2D.drawTexture(flower, 0,0,0,1,1);

        renderer2D.setColor(Color.WHITE);
//        renderer2D.drawPolygonFilled(new float[] {
//                -300, 0,
//                -100, -100,
//                -200, -300,
//                0, -200,
//                0, -200,
//                100, -100,
//                300, 0,
//                100, 100,
//                0, 300,
//                -100, 100
//        }, flower, 200,0,0,1,1);


        renderer2D.drawRectangleFilled(250,100, flower,
                0,
                20,
                0,
                0,

                1,
                2,
                20,
                2,

                0,0,0,1,1);

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
