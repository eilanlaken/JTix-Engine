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
public class SceneRendering3D_ModelsViewer_Scenes_1 implements Scene {

    private Camera camera;

    public Model model_import;
    public Model city_base;
    public Model model_tree;
    public Matrix4x4 transform_import = new Matrix4x4();
    public Matrix4x4 transform_floor = new Matrix4x4();
    public Matrix4x4 transform_ball = new Matrix4x4();

    public SceneRendering3D_ModelsViewer_Scenes_1() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/models/floor.fbx");
        Assets.loadModel("assets/app-models/tree-green_1.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/plane_2.fbx", "assets/app-models/textures");
        Assets.loadScene("assets/app-models/city_scene_demo.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        model_import = Assets.get("assets/app-models/plane_2.fbx");
        model_tree = Assets.get("assets/app-models/tree-green_1.fbx");
        city_base = Assets.get("assets/app-models/city_scene_demo.fbx");

        for (int i = 0; i < model_tree.meshes.length; i++) {
            ModelMaterial material = model_tree.materials[i];
            System.out.println(material.transparent);
        }
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -7, 5);

        camera.lookAt(0,0,0);

        camera.update();

        transform_import.translateGlobalAxisXYZ(0,0,3);
        transform_ball.translateGlobalAxisXYZ(0,0,2);

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


        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            transform_import.translateGlobalAxisXYZ(0,0,-0.05f);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            transform_import.translateGlobalAxisXYZ(0,0,0.05f);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            transform_import.translateGlobalAxisXYZ(0,-0.05f, 0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            transform_import.translateGlobalAxisXYZ(0,0.05f,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            transform_import.rotateLocalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transform_import.rotateLocalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform_import.rotateLocalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform_import.rotateLocalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform_import.rotateLocalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform_import.rotateLocalAxisX(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.H)) {
            transform_import.translateGlobalAxisXYZ(0,1,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Renderer3D.begin(camera);

        for (int i = 0; i < city_base.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(city_base.meshes[i], city_base.materials[i], transform_floor);
        }

        for (int i = 0; i < model_import.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_import.meshes[i], model_import.materials[i], transform_import);
        }

        for (int i = 0; i < model_tree.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_tree.meshes[i], model_tree.materials[i], new Matrix4x4());
        }

        Renderer3D.end();
    }



}
