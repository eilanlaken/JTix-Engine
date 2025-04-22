package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

import java.util.Map;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Simplex_2 implements Scene {

    private Camera camera;

    public Model model;
    public Matrix4x4 transform = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_Simplex_2() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/models/cube_accoustic_grid.fbx");
        Assets.finishLoading();

        model = Assets.get("assets/models/cube_accoustic_grid.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, -4, 0);

        camera.lookAt(0,0,0);

        camera.update();

        for (Map.Entry<String, Object> entry : model.materials[0].materialAttributes.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.K)) {
            //world.createConstraintDistance(body_a, body_b, 4);
            camera.position.z += 0.1f;
        }


        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            transform.translateGlobalAxisXYZ(0,0,-0.05f);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            transform.translateGlobalAxisXYZ(0,0,0.05f);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            transform.translateGlobalAxisXYZ(0,-0.05f, 0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            transform.translateGlobalAxisXYZ(0,0.05f,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            transform.rotateGlobalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transform.rotateGlobalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform.rotateGlobalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform.rotateGlobalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform.rotateGlobalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform.rotateGlobalAxisX(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.H)) {
            transform.translateGlobalAxisXYZ(0,1,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.247f,0.247f,0.247f,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        for (int i = 0; i < model.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model.meshes[i], model.materials[i], transform);
        }
        Renderer3D.end();
    }



}
