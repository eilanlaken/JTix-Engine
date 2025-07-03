package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
// https://www.youtube.com/watch?v=sNXj0RN09ps
public class SceneRendering3D_NewClouds implements Scene {

    private Camera camera;

    // models sky: clouds
    public Model modelCloud;
    public Texture cloudAtlas;
    public Shader cloudShader;
    public Matrix4x4 transformCloud = new Matrix4x4();
    public Matrix4x4[] clouds = new Matrix4x4[30];
    // models sky: skybox
    public Model modelSkybox;
    public Matrix4x4 transformSkybox = new Matrix4x4();


    public Model modelCockpit;

    public Array<Entity> entities = new Array<>();
    public Array<RenderUnit> rendrables_opaque = new Array<>();
    public Array<RenderUnit> rendrables_transparent = new Array<>();

    public Entity cockpit;
    public Vector3 CAMERA_OFFSET_DEFAULT = new Vector3(0, -0.0f, 1.1f);
    public Vector3 COCKPIT_LOOK_AT_TARGET = new Vector3(0, 1.78f, 1.05f);

    public SceneRendering3D_NewClouds() {
    }

    @Override
    public void setup() {

        // load sky: clouds
        Assets.loadTexture("assets/game-textures/cloud_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadModel("assets/app-models/plane.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/plane-2.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/cockpit-template.fbx", "assets/app-models/textures");
        // load sky: skybox
        Assets.loadTexture("assets/app-models/textures/skybox-3/nx.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/ny.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/nz.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/px.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/py.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/pz.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadModel("assets/app-models/skybox_1.fbx", "assets/app-models/textures/skybox-3");
        // load cities

        Assets.finishLoading();

        // setup clouds
        modelCloud = Assets.get("assets/app-models/plane-2.fbx");
        modelCockpit = Assets.get("assets/app-models/cockpit-template.fbx");
        cloudAtlas = Assets.get("assets/game-textures/cloud_1.png");
        modelCloud.materials[0].materialAttributes.put("u_texture_atlas", cloudAtlas);
        modelCloud.materials[0].transparent = true;
        String vertexShaderSrc = Assets.getFileContent("assets/game-shaders/cloud-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/game-shaders/cloud-shader.frag");
        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);
        modelCloud.materials[0].shader = cloudShader;

        final int CLOUDS_COUNT = 500;
        final float range = 0.26f * CLOUDS_COUNT;
        final float scale = 20.4f * (CLOUDS_COUNT / range);
        transformCloud.scale(scale,scale,scale);

        System.out.println(scale);
        for (int i = 0; i < clouds.length; i++) {
            clouds[i] = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(- 30,30), MathUtils.randomUniformFloat(- 30,30), MathUtils.randomUniformFloat(- 30,30));
            clouds[i].scale(55, 55, 55);
        }

        // setup skybox
        modelSkybox = Assets.get("assets/app-models/skybox_1.fbx");
        for (ModelMaterial material : modelSkybox.materials) {
            material.useLights = false;
        }
        transformSkybox.setToScaling(4000,4000,4000);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0.1f, 10000, 75);
        camera.position.set(0, -0.5f, 0.4f);
        camera.lookAt(0,0,0);
        camera.update();

        final int CLOUDS_COUNT = 500;
        final float range = 0.26f * CLOUDS_COUNT;
        final float scale = 10.4f * (CLOUDS_COUNT / range);

        for (int i = 0; i < CLOUDS_COUNT; i++) {
            Entity entity = new Entity();
            entity.transform = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range,range), 60 + MathUtils.randomUniformFloat(-range/4,range/4));
            entity.transform.scale(scale/5,scale/5,scale/5);
            entity.model = modelCloud;
            entities.add(entity);
        }

        // here I have to create separate render units arrays for the opaque and transparent render units.
        for (Entity entity : entities) {
            Model model = entity.model;
            for (int i = 0; i < model.meshes.length; i++) {
                RenderUnit renderUnit = new RenderUnit();
                renderUnit.mesh = model.meshes[i];
                renderUnit.material = model.materials[i];
                renderUnit.transform = entity.transform;
                renderUnit.userData = MathUtils.randomUniformInt(0, 65);
                if (model != modelCloud) rendrables_opaque.add(renderUnit);
                if (model == modelCloud) rendrables_transparent.add(renderUnit);
            }
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
            float panHorizontal = Input.mouse.getXDelta() * 0.05f;
            float panVertical = Input.mouse.getYDelta() * 0.05f;
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panVertical = Input.mouse.getYDelta() * 0.05f;
            camera.translateForward(-panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }
        camera.update();
        //update_gameplay();
        //update_cockpit();



        // rendering system

        update_gameplay();
        transformSkybox.setTranslation(camera.position);
        orient3(transformCloud);
        for (Matrix4x4 cloud : clouds) {
            camera.orientBillboard(cloud);
            cloud.rotateLocalAxisZ(correctionF);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);
        Renderer3D.begin(camera);
        for (Matrix4x4 cloud : clouds) {
            Renderer3D.drawModel(modelCloud, cloud);
        }
        Renderer3D.drawModel(modelSkybox, transformSkybox);
        Renderer3D.end();
    }

    private void orient(Matrix4x4 transform) {
        Vector3 scale = transform.getScale(new Vector3());
        Vector3 position = transform.getTranslation(new Vector3());
        Vector3 target_orientation = new Vector3(camera.position).sub(position).nor();
        Quaternion q_rotation = new Quaternion().setFromSourceToTarget(new Vector3(0,0,1), target_orientation);
        transform.setToTranslationRotationScaling(position, q_rotation, scale);
    }

    // TODO: THIS IS BETTER! just by negating the target_orientation
    private void orient2(Matrix4x4 transform) {
        Vector3 scale = transform.getScale(new Vector3());
        Vector3 position = transform.getTranslation(new Vector3());
        Vector3 target_orientation = new Vector3(camera.position).sub(position).nor();
        Quaternion q_rotation = new Quaternion().setFromSourceToTarget(new Vector3(0,0,1), target_orientation.negate());
        transform.setToTranslationRotationScaling(position, q_rotation, scale);

        float angle = transform.getRotation(new Quaternion()).getAngleAroundDeg(0,0,1);
        //camera.orientBillboard(transform);
        //transform.rotateLocalAxis(target_orientation, angle);
    }

    private void orient3(Matrix4x4 transform) {
        Vector3 cloudForward = transform.getBasisZ(new Vector3());
        Vector3 scale = transform.getScale(new Vector3());
        Vector3 position = transform.getTranslation(new Vector3());
        Vector3 target_orientation_1 = new Vector3(camera.position).sub(position).nor();
        Vector3 target_orientation_2 = new Vector3(camera.position).sub(position).nor().negate();

        float d1 = cloudForward.dot(target_orientation_1);
        float d2 = cloudForward.dot(target_orientation_2);
        Quaternion rotation = new Quaternion();
        if (d1 >= d2) {
            rotation.setFromSourceToTarget(new Vector3(0,0,1), target_orientation_1);
        } else {
            rotation.setFromSourceToTarget(new Vector3(0,0,1), target_orientation_2);
        }
        transform.setToTranslationRotationScaling(position, rotation, scale);
    }

    private float speed = 1;

    private void update_cockpit() {
        float delta = Graphics.getDeltaTime();
        Vector3 cockpitForward = cockpit.transform.getBasisY(new Vector3());

        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            speed += delta * 20;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) {
            speed -= delta * 20;
        }


        Vector3 velocity = new Vector3(cockpitForward).scl(speed);
        cockpit.transform.translateGlobalAxisXYZ(velocity.x * delta, velocity.y * delta, velocity.z * delta);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            cockpit.transform.rotateLocalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            cockpit.transform.rotateLocalAxisY(1);
//            camera.rotateAroundForward(1);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            cockpit.transform.rotateLocalAxisX(-1);
            //camera.rotateAroundRight(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            cockpit.transform.rotateLocalAxisX(1);
            //camera.rotateAroundRight(1);
        }

        // Do this with the target look at as well.
        Vector3 targetUp = cockpit.transform.getBasisZ(new Vector3());
        Vector3 cockpitPosition = cockpit.transform.getTranslation(new Vector3());

        Vector3 targetLookAt = new Vector3(COCKPIT_LOOK_AT_TARGET);
        targetLookAt.rot(cockpit.transform).add(cockpitPosition);
        Vector3 cameraTargetPosition = new Vector3(CAMERA_OFFSET_DEFAULT);
        cameraTargetPosition.rot(cockpit.transform).add(cockpitPosition);

        Vector3 forward = camera.forward;
        Vector3 targetForward = new Vector3(COCKPIT_LOOK_AT_TARGET).sub(CAMERA_OFFSET_DEFAULT).rot(cockpit.transform);

        camera.position.set(cameraTargetPosition);
        camera.up.slerp(targetUp, 0.08f);
        camera.forward.slerp(targetForward, 0.08f);
        //camera.lookAt(targetLookAt.x, targetLookAt.y, targetLookAt.z);
        camera.update();
    }

    float correctionF = 0;
    float correctionP = 0;

    private void update_gameplay() {
        float delta = Graphics.getDeltaTime();
        Vector3 velocity = new Vector3(camera.forward).scl(speed);
        camera.position.add(delta * velocity.x, delta * velocity.y, delta * velocity.z);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) speed += delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) speed -= delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            camera.rotateAroundForward(delta * -90);
            correctionF -= delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            camera.rotateAroundForward(delta * 90);
            correctionF += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            camera.rotateAroundRight(delta * -90);
            correctionP += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            camera.rotateAroundRight(delta * 90);
            correctionP -= delta * 90;
        }
    }

    public static class Entity {

        public Matrix4x4 transform;
        public Model model;

    }

    public static class RenderUnit {

        public Matrix4x4 transform;
        public ModelMesh mesh;
        public ModelMaterial material;

        public int userData; // relevant only for the clouds.

    }


}
