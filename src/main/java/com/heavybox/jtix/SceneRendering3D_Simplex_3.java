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

import java.util.Map;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Simplex_3 implements Scene {

    private Camera camera;

    public Model model;
    public Matrix4x4 transform = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_Simplex_3() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/models/plane_demo.fbx");
        Assets.finishLoading();

        model = Assets.get("assets/models/plane_demo.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, -15, 8);

        camera.lookAt(0,0,0);

        camera.update();

        for (Map.Entry<String, Object> entry : model.materials[0].materialAttributes.entrySet()) {
            //System.out.println(entry.getKey() + " = " + entry.getValue());
        }

    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);


        float scroll = Input.mouse.getVerticalScroll();
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
