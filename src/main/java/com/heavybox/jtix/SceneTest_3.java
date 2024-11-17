package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input_2.Input;
import com.heavybox.jtix.input_2.InputKeyboard;
import com.heavybox.jtix.input_2.InputMouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

public class SceneTest_3 implements Scene {

    Texture yellow;
    Texture pattern;
    private Renderer2D_3 renderer2D = new Renderer2D_3();
    private ComponentGraphicsCamera componentGraphicsCamera;
    TexturePack pack;

    float[] polygon = new float[] {-100,-100,
            0,-100, // a degenerate (co-linear) vertex.
            100,-100,
            100,100,
            -100,100
    };
    int[] polygonTriangles;

    @Override
    public void setup() {
        Assets.loadTexture("assets/textures/yellowSquare.jpg");
        Assets.loadTexture("assets/textures/pattern.png");
        Assets.loadTexturePack("assets/atlases/spots.yml");
        Assets.finishLoading();
    }

    @Override
    public void start() {
        yellow = Assets.get("assets/textures/yellowSquare.jpg");
        pattern = Assets.get("assets/textures/pattern.png");
        pack = Assets.get("assets/atlases/spots.yml");
        polygonTriangles = MathUtils.polygonTriangulate(polygon);
        componentGraphicsCamera = new ComponentGraphicsCamera(Graphics.getWindowAspectRatio() * 10,10, 1);
        componentGraphicsCamera.update();

    }

    float x = 0, y = 0;
    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float deg = 270;
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
        if (Input.keyboard.isKeyPressed(InputKeyboard.Key.W)) {
            u2 += 0.001f;
            v2 += 0.001f;
            deg += 1;
        }
        if (Input.keyboard.isKeyPressed(InputKeyboard.Key.S)) {
            u2 += 0.001f;
            v2 += 0.001f;
            deg -= 1;
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        renderer2D.begin(componentGraphicsCamera.lens.combined);
        renderer2D.setTint(Color.LIGHT_GRAY);
        //renderer2D.drawTexture(yellow, 10,5,50,300,deg,1,1);

        renderer2D.setTint(Color.GREEN);
        //renderer2D.drawCircleBorder(2,0.1f, deg + 40,44,0,0,0,1,1);

        renderer2D.setTint(new Color(1,0,0,0.2f));
        //renderer2D.drawCircleFilled(4,20,deg + 30,-2,0,0,1,1);

        renderer2D.setTint(Color.CYAN);
        renderer2D.drawRectangleBorder(200,100,30,5,300,-deg,1,1);


        renderer2D.setTint(null);
        //renderer2D.drawTexture(yellow,u1,v1,u2,v2,400,-200,0,0,30,1,1);

        renderer2D.setTint(Color.LIGHT_GRAY);
        //renderer2D.drawRectangleFilled(3,1,0.1f,5,0,-3,deg * 2,1,1);

        renderer2D.setTint(Color.BROWN);
        //renderer2D.drawLineFilled(0,0,6,3,0.3f,0,0,deg,1,1);

        renderer2D.drawLineThin(0,0,4,-4);

        renderer2D.setTint(new Color(1,0,0,0.4f));
        renderer2D.drawCurveFilled(0.5f, 10, new Vector2[]{
                new Vector2(-3, 0),
                new Vector2(0, 0),
                new Vector2(0.2f, 0).rotateDeg(deg),

        });


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
