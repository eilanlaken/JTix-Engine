package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_1 implements Scene {

    private Camera camera;

    public Model model;
    public Matrix4x4 transform = new Matrix4x4();

    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_1() {

    }

    @Override
    public void setup() {
        float[] positions = {
          // Left bottom triangle
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                // Right top triangle
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };

        ModelMesh[] meshes = new ModelMesh[1];
        meshes[0] = new ModelMesh(positions);
        model = new Model(meshes, null);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, 0, 3);
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
            transform.rotateLocalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transform.rotateLocalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform.rotateLocalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform.rotateLocalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform.rotateLocalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform.rotateLocalAxisX(-1);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        renderer2D.begin();
        renderer2D.drawCircleFilled(400,20,200,200,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        //Renderer3D.drawModel_tmp(model, transform);
        Renderer3D.end();
    }



}
