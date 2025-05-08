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

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
public class SceneRendering3D_Clouds_1 implements Scene {

    private Camera camera;

    public Model modelSimplex;
    public Model modelCloud;
    public Matrix4x4 transformSimplex = new Matrix4x4();
    public Matrix4x4 transformCloud = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public Shader cloudShader;

    public SceneRendering3D_Clouds_1() {

    }

    @Override
    public void setup() {

        Assets.loadModel("assets/models/simplex.fbx");
        Assets.loadModel("assets/models/cube-blue.fbx");
        Assets.finishLoading();

        modelSimplex = Assets.get("assets/models/simplex.fbx");
        modelCloud = Assets.get("assets/models/cube-blue.fbx");

        String vertexShaderSrc = Assets.getFileContent("assets/shaders/cloud.vert.glsl");
        String fragmentShaderSrc = Assets.getFileContent("assets/shaders/cloud.frag.glsl");

        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, -6, 0);

        camera.lookAt(0,0,0);

        camera.update();

        transformSimplex.translateGlobalAxisXYZ(0,6,0);
        transformCloud.translateGlobalAxisXYZ(3,0,0);

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


        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            transformSimplex.translateGlobalAxisXYZ(0,0,-0.05f);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            transformSimplex.translateGlobalAxisXYZ(0,0,0.05f);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            transformSimplex.translateGlobalAxisXYZ(0,-0.05f, 0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            transformSimplex.translateGlobalAxisXYZ(0,0.05f,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            transformSimplex.rotateGlobalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transformSimplex.rotateGlobalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transformSimplex.rotateGlobalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transformSimplex.rotateGlobalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transformSimplex.rotateGlobalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transformSimplex.rotateGlobalAxisX(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.H)) {
            transformSimplex.translateGlobalAxisXYZ(0,1,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0.247f,0.247f,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        for (int i = 0; i < modelSimplex.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(modelSimplex.meshes[i], modelSimplex.materials[i], transformSimplex);
        }

        for (int i = 0; i < modelCloud.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(cloudShader, modelCloud.meshes[i], modelCloud.materials[i], transformCloud);
        }
//        for (int i = 0; i < modelCloud.meshes.length; i++) {
//            Renderer3D.drawModel_tmp_5(modelCloud.meshes[i], modelCloud.materials[i], transformCloud);
//        }
        Renderer3D.end();
    }

    // KEYWORDS: decals, billboards
    private void orient_billboard() {
        System.out.println("===============\n");
        System.out.println(transformCloud);
        Vector3 position = transformCloud.getTranslation(new Vector3());
        Vector3 desiredDir = new Vector3(camera.position).sub(position).nor();
        System.out.println("billboard -> camera = " + desiredDir);
        System.out.println("camera forward: " + camera.forward);
        System.out.println("camera up: " + camera.up);
        System.out.println("camera right: " + camera.right);

        Vector3 bx = new Vector3();
        transformCloud.getBasisX(bx);
        System.out.println("basis x: " + bx);
        Vector3 by = new Vector3();
        transformCloud.getBasisY(by);
        System.out.println("basis y: " + by);
        Vector3 bz = new Vector3();
        transformCloud.getBasisZ(bz);
        System.out.println("basis z: " + bz);

        Vector3 scale = new Vector3();
        transformCloud.getScale(scale);

        by.set(desiredDir); // TODO: preserve original scale. Due to floating point rounding errors, scale isn't preserved 100% which will accumulate.
        bz.set(by).crs(bx).nor();
        transformCloud.setFromBasis(bx, by, bz, position);

        System.out.println(transformCloud.getScale(new Vector3()));
    }

}
