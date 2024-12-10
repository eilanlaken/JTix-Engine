package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Keyboard;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SceneTest_Fonts_1 implements Scene {


    private Renderer2D_3 renderer2D = new Renderer2D_3();

    Shader alwaysYellow;
    Texture yellow;
    Texture pattern;
    private ComponentGraphicsCamera componentGraphicsCamera;
    TexturePack pack;

    Font font;


    @Override
    public void setup() {
        Assets.loadFont("assets/fonts/OpenSans-Italic-32.yml");
        Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadTexture("assets/textures/pattern.png");
        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.loadShader("alwaysYellow", "assets/shaders/graphics-2d-shader-yellow.vert", "assets/shaders/graphics-2d-shader-yellow.frag");
        Assets.finishLoading();
    }

    @Override
    public void start() {
        alwaysYellow = Assets.get("alwaysYellow");
        yellow = Assets.get("assets/textures/yellowSquare.jpg");
        pattern = Assets.get("assets/textures/pattern.png");
        pack = Assets.get("assets/atlases/spots.yml");
        font = Assets.get("assets/fonts/OpenSans-Italic-32.yml");
    }


    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            //Application.windowSetSizeLimits(100, 100, 200, 200);
            //Application.windowFlash();


        }

        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            Assets.showFileDialogLoad("hello", path -> {
                Path fullPath = Paths.get(path);
                String loaded = Assets.getFileContent(fullPath.toString());
                System.out.println(loaded);
            });
        }
        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            Assets.showFileDialogSave("hello", path -> {
                // Convert the full path to a Path object
                Path fullPath = Paths.get(path);
                // Get the directory (parent)
                Path directory = fullPath.getParent();
                // Get the filename
                Path fileName = fullPath.getFileName();

                System.out.println(directory.toString());
                System.out.println(fileName.toString());

                try {
                    Assets.saveFile(directory.toString(), fileName.toString(), "abc");
                } catch (Exception e) {
                    // ignore.
                }
            });
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);


        // render font
        renderer2D.begin();
        renderer2D.drawTexture(Input.webcam.getFeed(), 0,0,0,1,1);
        renderer2D.drawString("What, Cunt?!", font, 0,0,0);
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
