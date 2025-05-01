package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
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
public class SceneRendering3D_Trailer_1 implements Scene {

    private Camera camera;

    // models sky: clouds
    public Model modelCloud;
    public Texture cloudAtlas;
    public Shader cloudShader;
    // models sky: skybox
    public Model modelSkybox;
    public Matrix4x4 transformSkybox = new Matrix4x4();

    public Model model_demo;
    public Matrix4x4 transformCity = new Matrix4x4();

    public Model modelCockpit;

    public Array<Entity> entities = new Array<>();
    public Array<RenderUnit> rendrables_opaque = new Array<>();
    public Array<RenderUnit> rendrables_transparent = new Array<>();

    public Entity cockpit;
    public Vector3 CAMERA_OFFSET_DEFAULT = new Vector3(0, -0.0f, 1.1f);
    public Vector3 COCKPIT_LOOK_AT_TARGET = new Vector3(0, 1.78f, 1.05f);

    public SceneRendering3D_Trailer_1() {
    }

    @Override
    public void setup() {

        // load sky: clouds
        Assets.loadTexture("assets/app-textures/cloud-atlas.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
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
        Assets.loadModel("assets/app-models/city-block_1.fbx", "assets/app-models/textures");

        Assets.finishLoading();

        // setup clouds
        modelCloud = Assets.get("assets/app-models/plane-2.fbx");
        modelCockpit = Assets.get("assets/app-models/cockpit-template.fbx");
        cloudAtlas = Assets.get("assets/app-textures/cloud-atlas.png");
        modelCloud.materials[0].materialAttributes.put("u_time", 0.0f);
        modelCloud.materials[0].materialAttributes.put("u_frame", 0);
        modelCloud.materials[0].materialAttributes.put("u_texture_atlas", cloudAtlas);
        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader-2.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader-2.frag");
        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);
        // setup skybox
        modelSkybox = Assets.get("assets/app-models/skybox_1.fbx");
        transformSkybox.setToScaling(4000,4000,4000);
        model_demo = Assets.get("assets/app-models/city-block_1.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0.1f, 10000, 85);
        camera.position.set(0, -0.5f, 0.4f);
        camera.lookAt(0,0,0);
        camera.update();

        cockpit = new Entity();
        cockpit.transform = new Matrix4x4().translateGlobalAxisXYZ(0, -50, 50);
        cockpit.model = modelCockpit;
        entities.add(cockpit);

        Entity city = new Entity();
        city.transform = new Matrix4x4();
        city.model = model_demo;
        entities.add(city);

        final int CLOUDS_COUNT = 500;
        final float range = 0.26f * CLOUDS_COUNT;
        final float scale = 10.4f * (CLOUDS_COUNT / range);

        for (int i = 0; i < CLOUDS_COUNT; i++) {
            Entity entity = new Entity();
            entity.transform = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range,range), 60 + MathUtils.randomUniformFloat(-range/4,range/4));
            entity.transform.scale(scale,scale,scale);
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
        update_cockpit();

        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (entity.model != modelCloud) continue;
            //camera.orientBillboard(cloud.transform);
            orient(entity.transform);
        }

        // rendering system
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0f,0f,1);
        Renderer3D.begin(camera);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDepthMask(true);
        for (int i = 0; i < modelSkybox.meshes.length; i++) {
            transformSkybox.setTranslation(camera.position);
            Renderer3D.drawModel_custom_unlit_shader(modelSkybox.meshes[i], modelSkybox.materials[i], transformSkybox);
        }
        for (RenderUnit renderUnit : rendrables_opaque) {
            Renderer3D.drawModel_tmp_5(renderUnit.mesh, renderUnit.material, renderUnit.transform);
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL20.glDepthMask(false);

        Vector3 position_o1 = new Vector3();
        Vector3 position_o2 = new Vector3();
        Collections.sort(rendrables_transparent, (o1, o2) -> {
            Matrix4x4 t1 = o1.transform;
            Matrix4x4 t2 = o2.transform;
            float d1 = camera.position.dst2(t1.getPosition(position_o1));
            float d2 = camera.position.dst2(t2.getPosition(position_o2));
            return Float.compare(d2, d1); // farthest first
        });
        for (RenderUnit renderUnit : rendrables_transparent) {
            Renderer3D.drawModel_cloud_shader_2(cloudShader, renderUnit.mesh, renderUnit.material, renderUnit.transform, renderUnit.userData);
        }


        GL11.glEnable(GL11.GL_CULL_FACE);
        GL20.glEnable(GL20.GL_DEPTH_TEST);
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE); //

        Renderer3D.end();
    }

    private void orient(Matrix4x4 transform) {
        Vector3 scale = transform.getScale(new Vector3());
        Vector3 position = transform.getPosition(new Vector3());
        Vector3 target_orientation = new Vector3(camera.position).sub(position).nor();
        Quaternion q_rotation = new Quaternion().setFromSourceToTarget(new Vector3(0,0,1), target_orientation);
        transform.setToPositionRotationScaling(position, q_rotation, scale);
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
        Vector3 cockpitPosition = cockpit.transform.getPosition(new Vector3());

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
