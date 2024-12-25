package com.heavybox.jtix;

import com.heavybox.jtix.application_2.Application;
import com.heavybox.jtix.application_2.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_graphics_old.Renderer2D_4;
import org.lwjgl.opengl.GL11;

public class SceneTest_4 implements Scene {

    Texture yellow;
    Texture pattern;
    private Renderer2D_4 renderer2D = new Renderer2D_4();
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
        componentGraphicsCamera = new ComponentGraphicsCamera(Graphics.getWindowWidth() / 6f, Graphics.getWindowHeight() / 6f, 1);
        componentGraphicsCamera.update();

    }

    float x = 0, y = 0;
    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float deg = 0;
    @Override
    public void update() {

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            //Application.windowSetSizeLimits(100, 100, 200, 200);
            //Application.windowFlash();
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;

            //Application.setWindowPosition(300,300);
            System.out.println(Application.getWindowPosX());
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            u2 += 0.001f;
            v2 += 0.001f;
            deg += 1;
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f,0.1f,0.3f,1);

        renderer2D.begin(componentGraphicsCamera.lens.combined);
        renderer2D.drawRectangleThin(-200,-200,200,-200,200,200,-200,200);

        renderer2D.setColor(Color.LIGHT_GRAY);
        renderer2D.drawTexture(yellow, 10,5,50,300,deg,1,1);

        renderer2D.setColor(Color.MAGENTA);
        renderer2D.drawRectangleThin(300,150, 40, 2,0,0,deg,0.75f,1);

        renderer2D.setColor(Color.GREEN);
        renderer2D.drawCircleBorder(150,10, deg + 40,44,0,0,0,1,1);

        renderer2D.setColor(Color.CHARTREUSE);
        renderer2D.drawPolygonThin(new float[] {
                -100,-100,
                100,-100,
                100,100,
                -100,100,
        }, polygonTriangles, 0,0,deg,1,1);

        renderer2D.setColor(Color.BLACK);
        renderer2D.drawPolygonFilled(new float[] {
                -100,-100,
                100,-100,
                100,100,
                -100,100,
        }, -200,0,deg,1,1);

        renderer2D.setColor(new Color(1,0,0,0.2f));
        renderer2D.drawCircleFilled(50,44,deg + 30,-200,0,0,1,1);

        renderer2D.setColor(Color.CYAN);
        renderer2D.drawRectangleBorder(200,100,30,5,300,-deg,1,1);

        renderer2D.drawCurveThin(new Vector2[]{
                new Vector2(-200,0),
                new Vector2(-150,40),
                new Vector2(-100,80),
                new Vector2(-50,40),
                new Vector2(0,0),
                new Vector2(50,-40),
                new Vector2(100,-80),
                new Vector2(150,-40),
                new Vector2(200,0),
        }, 100 * MathUtils.cosDeg(deg), 100 * MathUtils.sinDeg(deg), 0, 1, 1);

        renderer2D.drawCurveThin(-100,100,30, x -> 50 * MathUtils.sinRad(x));

        renderer2D.setColor(null);
        //renderer2D.drawCircleFilled(30,44,0,0,deg,2,1);
        renderer2D.drawTexture(yellow,u1,v1,u2,v2,400,-200,0,0,30,1,1);

        renderer2D.setColor(Color.BLUE);
        renderer2D.drawRectangleFilled(null, 100,200,50,-200,deg,1,1);

        renderer2D.setColor(Color.LIGHT_GRAY);
        renderer2D.drawRectangleFilled(200,100,10,5,300,-300,deg * 2,1,1);

        renderer2D.setColor(Color.BROWN);
        renderer2D.drawLineFilled(0,0,-100,-100,10,0,0,deg,1,1);

        renderer2D.setColor(null);
        renderer2D.drawTextureRegion(pack.getRegion("assets/textures/red30x30.png"), 0,0,0,1,1);

        renderer2D.drawLineThin(0,0,400,-400);



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
