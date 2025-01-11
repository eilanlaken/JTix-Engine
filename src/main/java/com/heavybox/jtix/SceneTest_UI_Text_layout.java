package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.ui_2.Style;
import com.heavybox.jtix.ui_2.UI;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_Text_layout implements Scene {


    private Renderer2D renderer2D = new Renderer2D();


    Font font;
    Font font2;

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.finishLoading();


    }

    Style style = new Style();

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

        text.append("      hellow");


    }


    float scale = 1;
    int index = 0;
    ArrayInt arr = new ArrayInt();
    Array<String> lines = new Array<>();

    StringBuffer text = new StringBuffer();

    float x = 0, y = 0, deg = 0, sclX = 1, sclY = 1;

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



        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            y += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            y -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            deg += 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            deg -= 3;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.T)) {
            sclX *= 1.01f;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Y)) {
            sclX *= 0.99f;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();
        renderer2D.setColor(Color.MAROON);
        renderer2D.drawRectangleBorder(200,150,2,0,0,0,1,1);

        renderer2D.setColor(Color.CYAN);
        renderer2D.setFont(font);

        //UI.calculateLineBreakdown(text.toString(), 200, style, lines);

        String[] ls = UI.wordWrap(text.toString(), 200, style);



        for (int i = 0; i < ls.length; i++) {
            String line = ls[i];
            renderer2D.drawStringLine(line, style.textSize, style.textAntialiasing, 0, line.length(), 0, 0, 0, -style.textLineHeight * style.textSize * i, 0,1,1);
        }
//        renderer2D.drawStringLine(text.toString(), style.fontSize, style.fontAntialiasing, 0, text.length(), 0, 0, 0, 0,0,1,1);

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
