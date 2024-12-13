package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Keyboard;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.ui.UIButton;
import com.heavybox.jtix.ui.UserInterface;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glGetFloatv;

public class SceneTest_Fonts_3 implements Scene {


    private Renderer2D renderer2D = new Renderer2D();


    private String fontPath = "assets/fonts/OpenSans-Italic.ttf";

    FontDynamic font;
    FontDynamic.Glyph g;

    @Override
    public void setup() {
        //Assets.loadTexture("assets/textures/yellowSquare.jpg");
        //Assets.loadFontStatic("assets/fonts/OpenSans-Regular-13.yml");

        Assets.finishLoading();

        font = new FontDynamic(fontPath);

    }

    @Override
    public void start() {
        g = font.getGlyph('A', 24);
    }

    float scale = 1;

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            Graphics.setCursorCustom("assets/textures/cursor-green.png");
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            Graphics.setCursorNotAllowed();
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) {
            Graphics.setCursorPointingHand();
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
        GL11.glClearColor(1f,0f,0f,1);


        // render font
        renderer2D.begin();
        renderer2D.drawTexture(g.texture, 0,0,0,1,1);

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
