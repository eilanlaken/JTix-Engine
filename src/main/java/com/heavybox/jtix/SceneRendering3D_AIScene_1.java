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

public class SceneRendering3D_AIScene_1 implements Scene {

    private Camera camera;

    public Model model_tree_1;
    public Model model_tree_2;

    public Matrix4x4 transform_plane = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_AIScene_1() {

    }

    @Override
    public void setup() {


        //Assets.loadModel("assets/models/cube_accoustic_grid.fbx");
        //Assets.loadModel("assets/models/trees.fbx");
        Assets.loadModel("assets/models/trees.fbx", "assets/models/trees2.fbm");

        Assets.finishLoading();

        model_tree_1 = Assets.get("assets/models/trees.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 5000, 75);
        camera.position.set(0, -4, 0);

        camera.lookAt(0,0,0);

        camera.update();

    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);


        float scroll = 3 * Input.mouse.getVerticalScroll();
        if (Input.mouse.getVerticalScroll() != 0) {
            camera.translateForward(scroll);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundUp(panHorizontal * 5);
            camera.rotateAroundRight(panVertical * 5);
        }

        camera.update();


        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            transform_plane.rotateLocalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transform_plane.rotateLocalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform_plane.rotateLocalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform_plane.rotateLocalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform_plane.rotateLocalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform_plane.rotateLocalAxisX(-1);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        for (int i = 0; i < model_tree_1.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_tree_1.meshes[i], model_tree_1.materials[i], transform_plane);
        }
        Renderer3D.end();
    }



}
