package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.Keyboard;
import com.heavybox.jtix.input_2.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

public class SceneTest_Physics_1 implements Scene {

    Shader alwaysYellow;
    Texture yellow;
    Texture pattern;
    private Renderer2D_3 renderer2D = new Renderer2D_3();
    private Matrix4x4 combined = new Matrix4x4();
    TexturePack pack;

    float[] polygon = new float[] {-100,-100,
            0,-100, // a degenerate (co-linear) vertex.
            100,-100,
            100,100,
            -100,100
    };
    int[] polygonTriangles;

    Vector2[] positions = new Vector2[10];

    @Override
    public void setup() {
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
        polygonTriangles = MathUtils.polygonTriangulate(polygon);

        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector2(MathUtils.randomUniformFloat(-4,4), MathUtils.randomUniformFloat(-4,4));
        }
    }

    float x = 0, y = 0;
    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float deg = 270;
    float zoom = 1;

    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getCursorX(), Input.mouse.getCursorY(), 0);
        float viewportHeight = 15;
        float viewportWidth = Graphics.getWindowAspectRatio() * 15;
        combined.setToOrthographicProjection(zoom * viewportWidth, zoom * viewportHeight);

        if (Input.mouse.isButtonPressed(Mouse.Button.LEFT)) {
            //Application.windowSetSizeLimits(100, 100, 200, 200);
            //Application.windowFlash();
            x = screen.x;
            y = screen.y;
            zoom += 0.01f;

            //Application.setWindowPosition(300,300);
            System.out.println(Application.getWindowPosX());
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            u2 += 0.001f;
            v2 += 0.001f;
            deg += 3f;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            u2 += 0.001f;
            v2 += 0.001f;
            deg -= 3f;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        renderer2D.begin(combined);

        renderer2D.drawRectangleFilled(3,1,0.1f,5,0,-3,deg * 2,1,1);


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
