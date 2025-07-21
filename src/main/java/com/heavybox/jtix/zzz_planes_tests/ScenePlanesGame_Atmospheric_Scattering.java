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

import java.util.Arrays;

// TODO:
// see here:
// https://github.com/ebruneton/precomputed_atmospheric_scattering
// https://threejs.org/examples/?q=sky#webgl_shaders_sky
// https://github.com/mrdoob/three.js/blob/master/examples/webgl_shaders_sky.html
// https://github.com/mrdoob/three.js/blob/master/examples/jsm/objects/Sky.js
public class ScenePlanesGame_Atmospheric_Scattering implements Scene {

    private Camera camera;

    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    ModelMesh box = ModelMesh.createCubeInverted(40000,40000,40000);
    Shader skyShader;
    ModelMaterial skyMaterial;

    public ScenePlanesGame_Atmospheric_Scattering() {

    }

    @Override
    public void setup() {

        String skyVertex = Assets.getFileContent("assets/game-shaders/sky.vert");
        String skyFragment = Assets.getFileContent("assets/game-shaders/sky.frag");
        skyShader = new Shader(skyVertex, skyFragment);

        skyMaterial = ModelMaterial.create();
        skyMaterial.materialAttributes.put("turbidity", 2f);
        skyMaterial.materialAttributes.put("rayleigh", 1f);
        skyMaterial.materialAttributes.put("mieCoefficient", 0.005f);
        skyMaterial.materialAttributes.put("mieDirectionalG", 0.8f);
        skyMaterial.materialAttributes.put("sunPosition", new Vector3()); // (0, 0, 0) by default
        skyMaterial.materialAttributes.put("up", new Vector3(0, 0, 1));
        skyMaterial.shader = skyShader;


        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight(), true);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        Graphics.setTargetFps(120);
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 400000, 75);
        camera.position.set(0, -0, 0);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        float delta = Graphics.getDeltaTime();
        //System.out.println(camera.position.z);
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
        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);
        Renderer3D.drawModel(skyShader, box, skyMaterial, new Matrix4x4());
        Renderer3D.end();

        FrameBufferBinder.bind();
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderer2D.begin();
        renderer2D.setShader(postProcessingHDR);
        renderer2D.drawTexture(sceneFrameBuffer.getColorAttachment0(), 0,0,0,1,-1);
        renderer2D.end();
    }


}
