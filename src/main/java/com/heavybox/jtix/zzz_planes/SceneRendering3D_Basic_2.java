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
public class SceneRendering3D_Basic_2 implements Scene {

    private Camera camera;

    public Model modelCloud;
    public Texture cloudAtlas;
    public Shader cloudShader;

    public Model modelCockpit;
    public Matrix4x4 transformCockpit = new Matrix4x4();

    public Array<Entity> entities = new Array<>();
    public Array<RenderUnit> rendrables_opaque = new Array<>();
    public Array<RenderUnit> rendrables_transparent = new Array<>();

    public SceneRendering3D_Basic_2() {
    }

    @Override
    public void setup() {

        Assets.loadTexture("assets/app-textures/cloud-fade.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/cloud-atlas.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadModel("assets/app-models/plane.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/cockpit-demo-noncommercial.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        modelCloud = Assets.get("assets/app-models/plane.fbx");
        modelCockpit = Assets.get("assets/app-models/cockpit-demo-noncommercial.fbx");
        cloudAtlas = Assets.get("assets/app-textures/cloud-atlas.png");
        // set the attributes
        modelCloud.materials[0].materialAttributes.put("u_time", 0.0f);
        modelCloud.materials[0].materialAttributes.put("u_frame", 0);
        modelCloud.materials[0].materialAttributes.put("u_texture_atlas", cloudAtlas);

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader-2.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/cloud-shader-2.frag");
        cloudShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0.1f, 10000, 75);
        camera.position.set(0, -10, 0);
        camera.lookAt(0,0,0);
        camera.update();

        Entity cockpit = new Entity();
        cockpit.transform = new Matrix4x4().translateGlobalAxisXYZ(0, -150, 0);
        cockpit.model = modelCockpit;
        entities.add(cockpit);

        final float range = 130;
        for (int i = 0; i < 500; i++) {
            Entity entity = new Entity();
            entity.transform = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range,range), MathUtils.randomUniformFloat(-range/10,range/10));
            entity.transform.scale(30,30,30);
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
                if (model == modelCockpit) rendrables_opaque.add(renderUnit);
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


        for (int i = 1; i < entities.size; i++) {
            Entity cloud = entities.get(i);
            orient(cloud.transform);
        }

        // rendering system
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0f,0f,1);
        Renderer3D.begin(camera);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDepthMask(true);

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
        int index = 0;
        for (RenderUnit renderUnit : rendrables_transparent) {
            Renderer3D.drawModel_cloud_shader_2(cloudShader, renderUnit.mesh, renderUnit.material, renderUnit.transform, renderUnit.userData);
            index++;
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
        Quaternion q_rotation = new Quaternion().setFromSourceToTarget(new Vector3(0,-1,0), target_orientation);
        transform.setToPositionRotationScaling(position, q_rotation, scale);
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
