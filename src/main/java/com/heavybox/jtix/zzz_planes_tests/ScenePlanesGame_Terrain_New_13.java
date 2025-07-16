package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.zzz_project.GameObjectTerrainLandBlock;
import com.heavybox.jtix.zzz_project.GameObjectTerrainWaterBlock;
import org.lwjgl.opengl.GL11;

public class ScenePlanesGame_Terrain_New_13 implements Scene {

    private Camera camera;

    private GameObjectTerrainLandBlock[][] terrain = new GameObjectTerrainLandBlock[8][8];
    private GameObjectTerrainWaterBlock[][] water = new GameObjectTerrainWaterBlock[8][8];

    private GameObjectTerrainLandBlock a, b;
    private GameObjectTerrainWaterBlock w1;

    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    // gameplay
    float correctionF = 0;
    float correctionP = 0;
    private float speed = 1;


    public ScenePlanesGame_Terrain_New_13() {

    }

    @Override
    public void setup() {

        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                terrain[i][j] = new GameObjectTerrainLandBlock(i,j);
            }
        }

        for (int i = 0; i < water.length; i++) {
            for (int j = 0; j < water[0].length; j++) {
                water[i][j] = new GameObjectTerrainWaterBlock(i,j);
            }
        }
//
//        a = new GameObjectTerrainLandBlock(5, 2);
//        b = new GameObjectTerrainLandBlock(6, 2);
//        w1 = new GameObjectTerrainWaterBlock(0,0);
//



        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight());
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -550, 250);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        float delta = Graphics.getDeltaTime();
        update_gameplay();
        System.out.println(speed);
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        float scroll = Input.mouse.getVerticalScroll();
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
            if (camera.mode == Camera.Mode.ORTHOGRAPHIC) camera.mode = Camera.Mode.PERSPECTIVE;
            else {
                camera.mode = Camera.Mode.ORTHOGRAPHIC;
                camera.forward.set(0,0,-1);
                camera.up.set(0,1,0);
                camera.update();
                camera.position.set(0, 0, 30);
                camera.lookAt(0,0,0);
                camera.update();
            }
        } else if (Input.mouse.getVerticalScroll() != 0) {
            if (camera.mode == Camera.Mode.PERSPECTIVE) camera.translateForward(scroll * 30);
            else camera.zoom += 0.04f * scroll;
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta();
            float panVertical = Input.mouse.getYDelta();
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panVertical = Input.mouse.getYDelta();
            camera.translateForward(-panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && camera.mode == Camera.Mode.PERSPECTIVE) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }
        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }


        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);

        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                terrain[i][j].render();
            }
        }

        for (int i = 0; i < water.length; i++) {
            for (int j = 0; j < water[0].length; j++) {
                water[i][j].update(delta);
                water[i][j].render();
            }
        }

        Renderer3D.end();

        FrameBufferBinder.bind();
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderer2D.begin();
        renderer2D.setShader(postProcessingHDR);
        renderer2D.drawTexture(sceneFrameBuffer.getColorAttachment0(), 0,0,0,1,-1);
        renderer2D.end();
    }

    private void update_gameplay() {
        float delta = Graphics.getDeltaTime();
        Vector3 velocity = new Vector3(camera.forward).scl(speed);
        camera.position.add(delta * velocity.x, delta * velocity.y, delta * velocity.z);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) speed += delta * 70;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) speed -= delta * 70;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            camera.rotateAroundForward(delta * -90);
            correctionF -= delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            camera.rotateAroundForward(delta * 90);
            correctionF += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            camera.rotateAroundRight(delta * -90);
            correctionP += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            camera.rotateAroundRight(delta * 90);
            correctionP -= delta * 90;
        }
    }

}
