package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
public class SceneRendering3D_Billboards_2 implements Scene {

    private Camera camera;

    public Model modelSimplex;
    public Model modelCloud;
    public Matrix4x4 transformSimplex = new Matrix4x4();
    public Matrix4x4 transformCloud_1 = new Matrix4x4();
    public Matrix4x4 transformCloud_2 = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public Shader cloudShader;

    public SceneRendering3D_Billboards_2() {

    }

    @Override
    public void setup() {

        Assets.loadTexture("assets/app-models/textures/FX_CloudAlpha03.png", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadModel("assets/models/simplex.fbx");
        Assets.loadModel("assets/app-models/cloud-billboard.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        modelSimplex = Assets.get("assets/models/simplex.fbx");
        modelCloud = Assets.get("assets/app-models/cloud-billboard.fbx");
        modelCloud.materials[0].materialAttributes.put("u_time", 0.0f);

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader.frag");

        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 100, 75);
        camera.position.set(0, 0, 5);

        camera.lookAt(0,0,0);

        camera.update();

        transformSimplex.translateGlobalAxisXYZ(0,0,-6);

        transformCloud_1.translateGlobalAxisXYZ(0,0.3f,0);
        transformCloud_2.translateGlobalAxisXYZ(0.1f,-0.1f,0);
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
            transformCloud_1.translateGlobalAxisXYZ(0,0,-0.05f);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            transformCloud_1.translateGlobalAxisXYZ(0,0,0.05f);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            transformCloud_1.translateGlobalAxisXYZ(0,-0.05f, 0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            transformCloud_1.translateGlobalAxisXYZ(0,0.05f,0);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) {
            transformCloud_1.translateGlobalAxisXYZ(-0.05f,0, 0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.X)) {
            transformCloud_1.translateGlobalAxisXYZ(0.05f,0,0);
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



        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0.247f,0.247f,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        float time = (float) modelCloud.materials[0].materialAttributes.get("u_time");
        time += Graphics.getDeltaTime();
        modelCloud.materials[0].materialAttributes.put("u_time", time);

        Renderer3D.begin(camera);

        // TODO: draw opaque objects first!
        for (int i = 0; i < modelSimplex.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(modelSimplex.meshes[i], modelSimplex.materials[i], transformSimplex);
        }

        // TODO: sort by distance to camera!
        GL11.glDisable(GL11.GL_CULL_FACE); // TODO: enable!
        GL20.glDepthMask(false);
        for (int i = 0; i < modelCloud.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(cloudShader, modelCloud.meshes[i], modelCloud.materials[i], transformCloud_1);
            Renderer3D.drawModel_custom_shader_2(cloudShader, modelCloud.meshes[i], modelCloud.materials[i], transformCloud_2);
        }
        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: enable!
        GL20.glDepthMask(true);




        Renderer3D.end();

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.M)) {
            orient_4();
        }
    }

    private void orient_2() {
        Vector3 cloudPosition = transformCloud_1.getTranslation(new Vector3());

        Vector3 basisY = transformCloud_1.getBasisY(new Vector3());
        Vector3 basisZ = new Vector3(camera.position).sub(cloudPosition).nor();
        Vector3 basisX = new Vector3(basisY).crs(basisZ).nor();
        System.out.println(basisX);

        transformCloud_1.setFromBasis(basisX, basisY, basisZ, cloudPosition);

    }

    private void orient_3() {
        Vector3 cloudPosition = transformCloud_1.getTranslation(new Vector3());

        Vector3 basisY = transformCloud_1.getBasisY(new Vector3());
        Vector3 basisZ = new Vector3(camera.forward).negate();
        Vector3 basisX = new Vector3(basisY).crs(basisZ).nor();
        System.out.println(basisX);

        transformCloud_1.setFromBasis(basisX, basisY, basisZ, cloudPosition);

    }

    // TODO: scale is not taken into account.
    private void orient_4() {
        Vector3 scale = transformCloud_1.getScale(new Vector3()); // TODO.

        Vector3 basisZ = transformCloud_1.getBasisZ(new Vector3()).nor();
        Vector3 cloudPosition = transformCloud_1.getTranslation(new Vector3());
        Vector3 basisZNew = new Vector3(camera.position).sub(cloudPosition).nor();
        // if identity, return
        Quaternion q = new Quaternion().setFromSourceToTarget(basisZ, basisZNew);
        Matrix4x4 rotation = new Matrix4x4(q);
        Vector3 basisY = transformCloud_1.getBasisY(new Vector3()).nor().rot(rotation);
        Vector3 basisX = new Vector3(basisY).crs(basisZNew).nor();
        transformCloud_1.setFromBasis(basisX, basisY, basisZNew, cloudPosition);


//        Vector3 basisZ = transformCloud.getBasisZ(new Vector3()).nor();
//        Vector3 cloudPosition = transformCloud.getPosition(new Vector3());
//        Vector3 basisZNew = new Vector3(1,0,0.1f).nor();
//        // if identity, return
//        Quaternion q = new Quaternion().setFromCross(basisZ, basisZNew);
//        Matrix4x4 rotation = new Matrix4x4(q);
//        Vector3 basisY = transformCloud.getBasisY(new Vector3()).rot(rotation);
//        Vector3 basisX = new Vector3(basisY).crs(basisZNew).nor();
//        transformCloud.setFromBasis(basisX, basisY, basisZNew, cloudPosition);
//
//        System.out.println(transformCloud.getBasisX(new Vector3()));
//        System.out.println(transformCloud.getBasisY(new Vector3()));
//        System.out.println(transformCloud.getBasisZ(new Vector3()));

    }

    // KEYWORDS: decals, billboards
    private void orient_1() {
        //System.out.println("===============\n");
        //System.out.println(transformCloud);
        Vector3 position = transformCloud_1.getTranslation(new Vector3());
        Vector3 desiredDir = new Vector3(camera.position).sub(position).nor();
        //System.out.println("billboard -> camera = " + desiredDir);
        //System.out.println("camera forward: " + camera.forward);
        //System.out.println("camera up: " + camera.up);
        //System.out.println("camera right: " + camera.right);

        Vector3 bx = new Vector3();
        transformCloud_1.getBasisX(bx);
        //System.out.println("basis x: " + bx);
        Vector3 by = new Vector3();
        transformCloud_1.getBasisY(by);
        //System.out.println("basis y: " + by);
        Vector3 bz = new Vector3();
        transformCloud_1.getBasisZ(bz);
        //System.out.println("basis z: " + bz);

        Vector3 scale = new Vector3();
        transformCloud_1.getScale(scale);

        by.set(desiredDir); // TODO: preserve original scale. Due to floating point rounding errors, scale isn't preserved 100% which will accumulate.
        bz.set(by).crs(bx).nor();
        transformCloud_1.setFromBasis(bx, by, bz, position);

        //System.out.println(transformCloud.getScale(new Vector3()));
    }

}
