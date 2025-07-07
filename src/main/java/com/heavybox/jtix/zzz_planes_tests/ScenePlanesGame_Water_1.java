package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

public class ScenePlanesGame_Water_1 implements Scene {

    private Camera camera;

    public Model waterModel;
    public Shader waterShader;
    public Matrix4x4 transformWater = new Matrix4x4();


    public ScenePlanesGame_Water_1() {

    }

    @Override
    public void setup() {

        String vertexShaderSrc = Assets.getFileContent("assets/game-shaders/water.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/game-shaders/water.frag");
        this.waterShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.finishLoading();

        waterModel = Assets.get("assets/models/terrain-block.fbx");
        waterModel.materials[0].materialAttributes.put("time", 0.0f);
        waterModel.materials[0].materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        //terrain.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#9bd8c0"));
        waterModel.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        waterModel.materials[0].materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        waterModel.materials[0].materialAttributes.put("uWavesAmplitude", 2);
        waterModel.materials[0].materialAttributes.put("uWavesSpeed", 0.3f);
        waterModel.materials[0].materialAttributes.put("uWavesFrequency", 0.002f);
        waterModel.materials[0].materialAttributes.put("uWavesPersistence", 44f);
        waterModel.materials[0].materialAttributes.put("uWavesLacunarity", 3.0f);
        waterModel.materials[0].materialAttributes.put("uWavesIterations", 3);

        waterModel.materials[0].materialAttributes.put("uTroughThreshold", 0f);
        waterModel.materials[0].materialAttributes.put("uTroughTransition", 8f);
        waterModel.materials[0].materialAttributes.put("uPeakThreshold", 22);
        //terrain.materials[0].materialAttributes.put("uPeakThreshold", 333);
        waterModel.materials[0].materialAttributes.put("uPeakTransition", 0.1f);
        waterModel.materials[0].shader = waterShader;
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
        float time = (float) waterModel.materials[0].materialAttributes.get("time");
        time += Graphics.getDeltaTime();
        waterModel.materials[0].materialAttributes.put("time", time);

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
            transformWater.translateGlobalAxisXYZ(0,0,1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        Renderer3D.begin(camera);
        Renderer3D.drawModel(waterModel, transformWater);
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,0,0));
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(0,512,0));
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,512,0));
        Renderer3D.end();
    }

}
