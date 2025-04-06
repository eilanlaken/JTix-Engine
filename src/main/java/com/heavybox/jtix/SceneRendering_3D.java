package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.physics2d.Body2D;
import com.heavybox.jtix.physics2d.World2D;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering_3D implements Scene {

    private Camera camera;

    public Model model;
    public Matrix4x4 transform = new Matrix4x4();

    public SceneRendering_3D() {

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
        model = new Model(meshes);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 640f/32, 480f/32, 1, 0, 100, 75);
        camera.update();


    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.S)) {
            //world.createConstraintDistance(body_a, body_b, 4);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Renderer3D.begin(camera);
        Renderer3D.drawModel_tmp(model, transform);
        Renderer3D.end();
    }



}
