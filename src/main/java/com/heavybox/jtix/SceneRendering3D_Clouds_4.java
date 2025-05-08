package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
// https://www.youtube.com/watch?v=sNXj0RN09ps
public class SceneRendering3D_Clouds_4 implements Scene {

    private Camera camera;

    public Model modelCloud;
    public Matrix4x4[] transformClouds = new Matrix4x4[500];
    public Texture cloudOpacity;
    public Texture cloudAtlas;
    public Shader cloudShader;

    public SceneRendering3D_Clouds_4() {

    }

    @Override
    public void setup() {

        Assets.loadTexture("assets/app-textures/cloud-fade.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/cloud-atlas.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadModel("assets/app-models/plane.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        modelCloud = Assets.get("assets/app-models/plane.fbx");
        cloudOpacity = Assets.get("assets/app-textures/cloud-fade.png");
        cloudAtlas = Assets.get("assets/app-textures/cloud-atlas.png");
        // set the attributes
        modelCloud.materials[0].materialAttributes.put("u_time", 0.0f);
        modelCloud.materials[0].materialAttributes.put("u_frame", 0);
        modelCloud.materials[0].materialAttributes.put("u_texture_opacity", cloudOpacity);
        modelCloud.materials[0].materialAttributes.put("u_texture_atlas", cloudAtlas);

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader.frag");
        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0.1f, 10000, 75);
        camera.position.set(0, -250, 0);

        camera.lookAt(0,0,0);

        camera.update();

        final float range = 130;
        for (int i = 0; i < transformClouds.length; i++) {
            this.transformClouds[i] = new Matrix4x4();
            transformClouds[i].scale(30,30,30);
            transformClouds[i].translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range/10,range/10));
        }

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


//        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0,-0.05f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0,0.05f);
//        }
//
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,-0.05f, 0);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
//            transformCloud_1.translateGlobalAxisXYZ(0,0.05f,0);
//        }

//        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) {
//            transformCloud_1.translateGlobalAxisXYZ(-0.05f,0, 0);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.X)) {
//            transformCloud_1.translateGlobalAxisXYZ(0.05f,0,0);
//        }
//
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
//            camera.rotateAroundForward(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
//            camera.rotateAroundForward(-1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
//            camera.rotateAroundUp(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
//            camera.rotateAroundUp(-1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
//            camera.rotateAroundRight(1f);
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
//            camera.rotateAroundRight(-1f);
//        }

        for (int i = 0; i < transformClouds.length; i++) {
            orient_2(transformClouds[i]);
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0.247f,0.247f,1);

        Vector3 position_o1 = new Vector3();
        Vector3 position_o2 = new Vector3();

        Renderer3D.begin(camera);
//        Collections.sort(transformClouds, (o1, o2) -> {
//            float d1 = camera.position.dst2(o1.getPosition(position_o1));
//            float d2 = camera.position.dst2(o2.getPosition(position_o2));
//            return Float.compare(d2, d1); // farthest first
//        });
        // TODO: sort by distance to camera!
        //GL11.glDisable(GL11.GL_CULL_FACE); // TODO: enable!
        GL20.glDepthMask(false);
        GL20.glDisable(GL20.GL_DEPTH_TEST);
        for (int i = 0; i < modelCloud.meshes.length; i++) {
            for (int j = 0; j < transformClouds.length; j++) {
                Renderer3D.drawModel_cloud_shader_2(cloudShader, modelCloud.meshes[i], modelCloud.materials[i], transformClouds[j], j);
            }
        }
        GL20.glEnable(GL20.GL_DEPTH_TEST);
        GL20.glDepthMask(true);


        Renderer3D.end();
        update_gameplay();

    }

    private void orient_2(Matrix4x4 transform) {
        Vector3 scale = transform.getScale(new Vector3());
        Vector3 position = transform.getTranslation(new Vector3());
        Vector3 target_orientation = new Vector3(camera.position).sub(position).nor();
        Quaternion q_rotation = new Quaternion().setFromSourceToTarget(new Vector3(0,-1,0), target_orientation);
        transform.setToTranslationRotationScaling(position, q_rotation, scale);
    }

    private void orient_billboard(Matrix4x4 transform) {
        Quaternion rotation = new Quaternion();
        Vector3 position = new Vector3(transform.getTranslation(new Vector3()));
        Vector3 scaling = new Vector3(3,3,3);
        Vector3 tmp = new Vector3();
        Vector3 tmp2 = new Vector3();
        Vector3 up = new Vector3(camera.position).sub(position).nor();
        Vector3 dir = new Vector3(camera.up);
        tmp.set(dir).crs(up).nor();

        // libGDX:
//        Vector3 dir = new Vector3(camera.position).sub(position).nor();
//        Vector3 up = new Vector3(camera.up);
//        tmp.set(up).crs(dir).nor();

        tmp2.set(dir).crs(tmp).nor();
        rotation.setFromAxes(tmp.x, tmp2.x, dir.x, tmp.y, tmp2.y, dir.y, tmp.z, tmp2.z, dir.z);
        transform.setToTranslationRotationScaling(position, rotation, scaling);
    }

    private float speed = 1;

    private void update_gameplay() {
        float delta = Graphics.getDeltaTime();
        Vector3 velocity = new Vector3(camera.forward).scl(speed);
        camera.position.add(delta * velocity.x, delta * velocity.y, delta * velocity.z);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) speed += delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) speed -= delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) camera.rotateAroundForward(delta * -90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) camera.rotateAroundForward(delta * 90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) camera.rotateAroundRight(delta * -90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) camera.rotateAroundRight(delta * 90);
    }


}
