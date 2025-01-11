package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.ui_2.Style;
import com.heavybox.jtix.ui_2.UI;
import com.heavybox.jtix.ui_3.NodeDebug;
import org.lwjgl.opengl.GL11;

public class SceneTest_UI_Borders implements Scene {


    private Renderer2D renderer2D = new Renderer2D();

    private NodeDebug nodeDebug = new NodeDebug();

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadFont("assets/fonts/OpenSans-Regular.ttf");
        Assets.loadFont("C:\\Windows\\Fonts\\ahronbd.ttf");

        Assets.finishLoading();

    }

    @Override
    public void start() {
        nodeDebug.style.cornerRadiusTopLeft = 20;
        nodeDebug.style.cornerSegmentsTopLeft = 20;

        nodeDebug.style.cornerRadiusTopRight = 10;
        nodeDebug.style.cornerSegmentsTopRight = 10;

        nodeDebug.style.cornerRadiusBottomRight = 23;
        nodeDebug.style.cornerSegmentsBottomRight = 20;

        nodeDebug.style.cornerRadiusBottomLeft = 60;
        nodeDebug.style.cornerSegmentsBottomLeft = 2;
    }

    float x = 0, y = 0, deg = 0, sclX = 1, sclY = 1;

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        ArrayInt codepointsPressed = Input.keyboard.getCodepointPressed();
        for (int i = 0; i < codepointsPressed.size; i++) {
            int codepoint = codepointsPressed.get(i);
//            text.append((char)  codepoint);
        }

        nodeDebug.fixedUpdate(Graphics.getDeltaTime());
        nodeDebug.handleInput();

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
        //nodeDebug.draw(renderer2D);

        renderer2D.setColor(Color.WHITE);
        renderer2D.drawRectangleFilled(400,200,
                40,2,
                10,10,
                40,5,
                4,30,

                0,0,0,1,1);
        renderer2D.setColor(1,0,1,0.5f);
        renderer2D.drawRectangleFilled(400,200,0,0,0,1,1);

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
