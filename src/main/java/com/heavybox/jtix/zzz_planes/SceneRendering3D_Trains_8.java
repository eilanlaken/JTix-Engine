package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Model;
import com.heavybox.jtix.graphics.Renderer3D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Trains_8 implements Scene {

    private Camera camera;

    public Model model_train;
    public Matrix4x4 transform_train = new Matrix4x4();
    public Model model_tracks;

    Path path = new Path();

    float current_t = 0;
    float speed = 10;
    float acceleration = 0;

    public SceneRendering3D_Trains_8() {

        path
                .begin()
                .connect(Path.ofBezierLinear(new Vector3(0,0, 0), new Vector3(20,0, 0), 3))
                .connect(Path.ofBezierQuadratic(new Vector3(20,0, 0), new Vector3(20,-15, 0), new Vector3(10,-15, 0), 10))
                .connect(Path.ofBezierQuadratic(new Vector3(10,-15, 0), new Vector3(0,-15, 0), new Vector3(0,0,0), 10))
                .end(true);

        transform_train.setTranslation(path.points.get(0));
    }

    @Override
    public void setup() {

        Assets.loadModel("assets/app-models/train-car_1.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/train-rails-block.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        model_train = Assets.get("assets/app-models/train-car_1.fbx");
        model_tracks = Assets.get("assets/app-models/train-rails-block.fbx");
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, 0, 50);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        updateCamera();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            current_t += Graphics.getDeltaTime();
            Vector3 position = new Vector3();
            path.getPosition(current_t, position);
            transform_train.setTranslation(position);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            float distance = speed * Graphics.getDeltaTime();
            current_t = path.advance(current_t, distance);
            Vector3 position = new Vector3();
            path.getPosition(current_t, position);
            transform_train.setTranslation(position);
            //System.out.println(current_t);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,1,1,1);

        Renderer3D.begin(camera);

        for (int i = 0; i < model_train.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_train.meshes[i], model_train.materials[i], transform_train);
        }

        for (int i = 0; i < path.points.size; i++) {
            Matrix4x4 transform = new Matrix4x4();
            Vector3 position = path.points.get(i);
            Vector3 direction = path.dirs.get(i);
            Vector3 up = Vector3.Z_UNIT;
            Vector3 b1 = new Vector3(direction).crs(up);

            transform.setFromBasis(b1, direction, up, position);
            for (int j = 0; j < model_tracks.meshes.length; j++) {
                Renderer3D.drawModel_tmp_5(model_tracks.meshes[j], model_tracks.materials[j], transform);
            }
        }


        Renderer3D.end();
    }

    private void updateCamera() {
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
    }

}
