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

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_ModelsViewer_Scenes_7 implements Scene {

    private Camera camera;

    public Scene3D scene3D;

    public SceneRendering3D_ModelsViewer_Scenes_7() {

    }

    @Override
    public void setup() {

        Assets.loadScene("assets/game-models/map-1-mountain-volcano.fbx", "assets/game-textures");
        Assets.finishLoading();

        scene3D = Assets.get("assets/game-models/map-1-mountain-volcano.fbx");

        System.out.println(scene3D);
        System.out.println(scene3D.allMaterials.length);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -40, 60);

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
            if (camera.mode == Camera.Mode.PERSPECTIVE) camera.translateForward(scroll * 10);
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
            camera.rotateAroundAxis(panHorizontal * 2,0,0,1);
            camera.rotateAroundRight(panVertical * 2);
        }
        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Renderer3D.begin(camera);


        Scene3D.Node nodeHouse = scene3D.namedNodes.get("volcano");
        Matrix4x4 transformHouse = nodeHouse.localTransform;
        transformHouse.idt();
        //transformHouse.rotateLocalAxisZ(30);
        Model modelHouse = nodeHouse.model;
        for (int i = 0; i < modelHouse.meshes.length; i++) {
                Renderer3D.drawModel_tmp_5(modelHouse.meshes[i], modelHouse.materials[i], transformHouse);
        }

        Scene3D.Node balloon = scene3D.namedNodes.get("lava");
        Matrix4x4 transformBalloon = balloon.localTransform;
        Matrix4x4 t = new Matrix4x4(transformBalloon).mulLeft(transformHouse); // to apply the transform, multiply from the left
        // TODO: need to consider entire tree
        Model model = balloon.model;
        for (int i = 0; i < model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model.meshes[i], model.materials[i], t);
        }


        Renderer3D.end();
    }



}
