package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_4 implements Scene {

    private Camera camera;

    public Model model;
    public Matrix4x4 transform = new Matrix4x4();

    public SceneRendering3D_4() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/models/head2.fbx");
        Assets.finishLoading();

        model = Assets.get("assets/models/head2.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, -10, 0);

        camera.lookAt(0,0,0);

        camera.update();

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

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

//        renderer2D.begin();
//        renderer2D.drawTexture(t, 0,0,0,1,1);
//        renderer2D.end();

        Renderer3D.begin(camera);
        //Renderer3D.drawModel_tmp(model_1, transform);
        //Renderer3D.drawModel_tmp_2(model_2.meshes[0], transform);
        //Renderer3D.drawModel_tmp_2(model.meshes[0], transform);
        Renderer3D.drawModel_tmp_3(model.meshes[0], model.materials[0], transform);
        Renderer3D.drawModel_tmp_3(model.meshes[1], model.materials[1], transform);
        Renderer3D.drawModel_tmp_3(model.meshes[2], model.materials[2], transform);
        Renderer3D.drawModel_tmp_3(model.meshes[3], model.materials[3], transform);
        Renderer3D.end();
    }



}
