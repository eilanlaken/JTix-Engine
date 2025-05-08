package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Trains_1 implements Scene {

    private Camera camera;

    public Model model_import;
    public Model model_train;
    public Matrix4x4 transform_import = new Matrix4x4();
    public Matrix4x4 transform_floor = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_Trains_1() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/app-models/train-car_1.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/train-rails-block.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        model_train = Assets.get("assets/app-models/train-car_1.fbx");
        model_import = Assets.get("assets/app-models/train-rails-block.fbx");

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
            System.out.println(Renderer3D.lightDir);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,1,1,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);


        for (int i = 0; i < model_train.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_train.meshes[i], model_train.materials[i], transform_floor);
        }

        for (int i = 0; i < model_import.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_import.meshes[i], model_import.materials[i], transform_import);
        }

        Renderer3D.end();
    }


    class Path {

        public Array<Vector3> positions = new Array<>();
        public Array<Quaternion> rotations = new Array<>();

        public void setToLine(Vector3 start, Vector3 end) {
            positions.clear();

            recalculateRotations();
        }

        public void setToCircle(Vector3 center, Vector3 up, float r, float angle) {
            positions.clear();

            recalculateRotations();
        }

        private void recalculateRotations() {
            rotations.clear();
        }

    }


}
