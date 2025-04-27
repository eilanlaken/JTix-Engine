package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
// https://www.youtube.com/watch?v=sNXj0RN09ps
public class SceneRendering3D_Transparency_1 implements Scene {

    private Camera camera;

    public Model modelCubeSolid;
    public Model modelCubeRedAlpha;
    public Model modelCubeGreenAlpha;
    public Model modelCubeBlueAlpha;

    public Matrix4x4[] transformSolids = new Matrix4x4[4];
    public Matrix4x4[] transformAlphas = new Matrix4x4[4];

    public SceneRendering3D_Transparency_1() {

        for (int i = 0; i < transformSolids.length; i++) {
            transformSolids[i] = new Matrix4x4().translateGlobalAxisXYZ(0, i*3, 0);
        }

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/app-models/cube-solid.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/cube-red-alpha.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/cube-green-alpha.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/cube-blue-alpha.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        modelCubeSolid = Assets.get("assets/app-models/cube-solid.fbx");
        modelCubeRedAlpha = Assets.get("assets/app-models/cube-red-alpha.fbx");
        modelCubeGreenAlpha = Assets.get("assets/app-models/cube-green-alpha.fbx");
        modelCubeBlueAlpha = Assets.get("assets/app-models/cube-blue-alpha.fbx");
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0.1f, 10000, 75);
        camera.position.set(0, -10, 0);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        float scroll = Input.mouse.getVerticalScroll();
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.INSERT)) {
            if (camera.mode == Camera.Mode.ORTHOGRAPHIC) camera.mode = Camera.Mode.PERSPECTIVE;
            else camera.mode = Camera.Mode.ORTHOGRAPHIC;
        }
        if (Input.mouse.getVerticalScroll() != 0) {
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
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }
        camera.update();


//        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0,-0.05f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0,0.05f);
//        }
//
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,-0.05f, 0);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0.05f,0);
//        }

//        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) {
//            transformCloud_1.translateGlobalAxisXYZ(-0.05f,0, 0);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.X)) {
//            transformCloud_1.translateGlobalAxisXYZ(0.05f,0,0);
//        }
//
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
//            camera.rotateAroundForward(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
//            camera.rotateAroundForward(-1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
//            camera.rotateAroundUp(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
//            camera.rotateAroundUp(-1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
//            camera.rotateAroundRight(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
//            camera.rotateAroundRight(-1f);
//        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1,1,0,0);
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0.247f,0.247f,1);

        Renderer3D.begin(camera);

        for (Matrix4x4 transformSolid : transformSolids) {
            for (int j = 0; j < modelCubeSolid.meshes.length; j++) {
                Renderer3D.drawModel_tmp_5(modelCubeSolid.meshes[j], modelCubeSolid.materials[j], transformSolid);
            }
        }


        Vector3 position_o1 = new Vector3();
        Vector3 position_o2 = new Vector3();
//        Collections.sort(transformClouds, (o1, o2) -> {
//            float d1 = camera.position.dst2(o1.getPosition(position_o1));
//            float d2 = camera.position.dst2(o2.getPosition(position_o2));
//            return Float.compare(d2, d1); // farthest first
//        });

        // TODO: sort by distance to camera!
        //GL11.glDisable(GL11.GL_CULL_FACE); //
        //GL20.glDepthMask(true);
        //GL20.glDisable(GL20.GL_DEPTH_TEST);
        for (int i = 0; i < modelCubeSolid.meshes.length; i++) {
            for (int j = 0; j < transformSolids.length; j++) {
                //Renderer3D.drawModel_cloud_shader_2(cloudShader, modelCubeSolid.meshes[i], modelCubeSolid.materials[i], transformSolids[j], j);
            }
        }

        GL20.glEnable(GL20.GL_DEPTH_TEST);
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE); //

        Renderer3D.end();

    }




}
