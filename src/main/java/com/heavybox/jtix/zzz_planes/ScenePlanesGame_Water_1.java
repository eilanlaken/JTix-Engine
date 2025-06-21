package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class ScenePlanesGame_Water_1 implements Scene {

    private Camera camera;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;
    public Matrix4x4 transform_terrain = new Matrix4x4();


    public ScenePlanesGame_Water_1() {

    }

    @Override
    public void setup() {

        String vertexShaderSrc = Assets.getFileContent("assets/game-shaders/water.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/game-shaders/water.frag");
        this.terrainShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.finishLoading();

        terrain = Assets.get("assets/models/terrain-block.fbx");
        terrain.materials[0].materialAttributes.put("time", 0.0f);
        terrain.materials[0].materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        //terrain.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#9bd8c0"));
        terrain.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        terrain.materials[0].materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        terrain.materials[0].materialAttributes.put("uWavesAmplitude", 2f);
        terrain.materials[0].materialAttributes.put("uWavesSpeed", 0.3f);
        terrain.materials[0].materialAttributes.put("uWavesFrequency", 0.002f);
        terrain.materials[0].materialAttributes.put("uWavesPersistence", 44f);
        terrain.materials[0].materialAttributes.put("uWavesLacunarity", 3.0f);
        terrain.materials[0].materialAttributes.put("uWavesIterations", 3);

        terrain.materials[0].materialAttributes.put("uTroughThreshold", -0.5f);
        terrain.materials[0].materialAttributes.put("uTroughTransition", 8f);
        terrain.materials[0].materialAttributes.put("uPeakThreshold", 333);
        terrain.materials[0].materialAttributes.put("uPeakTransition", 2);



        terrain.materials[0].shader = terrainShader;
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 5000, 75);
        camera.position.set(0, 0, 150);

        camera.lookAt(0,50,0);

        camera.update();

    }


    @Override
    public void update() {
        float time = (float) terrain.materials[0].materialAttributes.get("time");
        time += Graphics.getDeltaTime();
        terrain.materials[0].materialAttributes.put("time", time);

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        float scroll = 3 * Input.mouse.getVerticalScroll();
        if (Input.mouse.getVerticalScroll() != 0) {
            camera.translateForward(scroll * 10);
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta();
            float panVertical = Input.mouse.getYDelta();
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panVertical = Input.mouse.getYDelta();
            camera.translateForward(-panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            //camera.rotateAroundUp(panHorizontal * 5);
            //camera.rotateAroundRight(panVertical * 5);
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }

        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            transform_terrain.translateGlobalAxisXYZ(0,0,1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        Renderer3D.begin(camera);
        Renderer3D.drawModel(terrain, transform_terrain);
        Renderer3D.drawModel(terrain, new Matrix4x4(transform_terrain).translateGlobalAxisXYZ(512,0,0));
        Renderer3D.end();
    }

}
