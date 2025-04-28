package com.heavybox.jtix;

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
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.zzz_planes.GameObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fmain.js%3A86%2C52
// https://codesandbox.io/p/sandbox/simondev-shader-clouds-p0slqy?file=%2Fshaders%2Foklab.glsl
// https://blog.uhawkvr.com/
// https://www.youtube.com/watch?v=sNXj0RN09ps
public class SceneRendering3D_Transparency_1 implements Scene {

    private Camera camera;

    public Model modelCubeSolid;
    public Model modelPlaneTransparentRed;
    public Model modelPlaneTransparentGreen;
    public Model modelPlaneTransparentBlue;

    //public Matrix4x4[] transformSolids = new Matrix4x4[1];
    //public Matrix4x4[] transformAlphas = new Matrix4x4[2];

    int activePlane = 0;

    public Array<Entity> entities = new Array<>();
    public Array<RenderUnit> rendrables_opaque = new Array<>();
    public Array<RenderUnit> rendrables_transparent = new Array<>();

    public SceneRendering3D_Transparency_1() {
    }

    @Override
    public void setup() {

        Assets.loadModel("assets/app-models/cube-solid.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/plane-red-transparent.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/plane-green-transparent.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/plane-blue-transparent.fbx", "assets/app-models/textures");

//        Assets.loadModel("assets/app-models/cube-transparent.fbx", "assets/app-models/textures");
//        Assets.loadModel("assets/app-models/cube-red-alpha.fbx", "assets/app-models/textures");
//        Assets.loadModel("assets/app-models/cube-green-alpha.fbx", "assets/app-models/textures");
//        Assets.loadModel("assets/app-models/cube-blue-alpha.fbx", "assets/app-models/textures");
        Assets.finishLoading();

        modelCubeSolid = Assets.get("assets/app-models/cube-solid.fbx");
        modelPlaneTransparentRed = Assets.get("assets/app-models/plane-red-transparent.fbx");
        modelPlaneTransparentGreen = Assets.get("assets/app-models/plane-green-transparent.fbx");
        modelPlaneTransparentBlue = Assets.get("assets/app-models/plane-blue-transparent.fbx");
        //modelCubeRedAlpha = Assets.get("assets/app-models/cube-red-alpha.fbx");
        //modelCubeGreenAlpha = Assets.get("assets/app-models/cube-green-alpha.fbx");
        //modelCubeBlueAlpha = Assets.get("assets/app-models/cube-blue-alpha.fbx");
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

        for (int i = 0; i < 3; i++) {
            Entity entity = new Entity();
            entity.transform = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-1,1), i*3, 0);
            entity.model = modelCubeSolid;
            entities.add(entity);
        }

        for (int i = 0; i < 2; i++) {
            Entity entity = new Entity();
            entity.transform = new Matrix4x4().translateGlobalAxisXYZ(MathUtils.randomUniformFloat(-1,1), i*3 + 1.5f, 0);
            entity.model = modelPlaneTransparentRed;
            entities.add(entity);
        }


        for (Entity entity : entities) {
            // get renderables
            Model model = entity.model;
            for (int i = 0; i < model.meshes.length; i++) {
                RenderUnit renderUnit = new RenderUnit();
                renderUnit.mesh = model.meshes[i];
                renderUnit.material = model.materials[i];
                renderUnit.transform = entity.transform;

                Float opacity = (Float) renderUnit.material.materialAttributes.get("u_prop_opacity");
                if (opacity == null) rendrables_opaque.add(renderUnit);
                else if (MathUtils.floatsEqual(opacity, 1.0f)) rendrables_opaque.add(renderUnit);
                else rendrables_transparent.add(renderUnit);
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

        if (Input.keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
            activePlane++;
            activePlane %= entities.size;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            entities.get(activePlane).transform.translateGlobalAxisXYZ(0,-0.05f,0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            entities.get(activePlane).transform.translateGlobalAxisXYZ(0,0.05f,0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            entities.get(activePlane).transform.translateGlobalAxisXYZ(-0.05f,0,0);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            entities.get(activePlane).transform.translateGlobalAxisXYZ(0.05f,0f,0);
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
//
//        for (Matrix4x4 transformSolid : transformSolids) {
//            for (int j = 0; j < modelCubeSolid.meshes.length; j++) {
//                Renderer3D.drawModel_tmp_5(modelCubeSolid.meshes[j], modelCubeSolid.materials[j], transformSolid);
//            }
//        }


        GL11.glDisable(GL11.GL_CULL_FACE);
        //GL11.glDisable(GL11.GL_DEPTH_TEST); // WE DO NOT DISABLE THE DEPTH TEST! Otherwise, this messes up the entire frame.
        GL20.glDepthMask(false); // <- We should not overwrite the depth mask, because the transparent
                                      // pixels will prevent writes to the color buffer.
                                      // Since we cannot rely on the depth mask, we have to draw from furthest to nearest
                                      // (just like in 2d rendering).
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
            Renderer3D.drawModel_custom_unlit_shader(renderUnit.mesh, renderUnit.material, renderUnit.transform);
        }


//
//        for (Matrix4x4 transformAlpha : transformAlphas) {
//            for (int j = 0; j < modelPlaneTransparentRed.meshes.length; j++) {
//                Renderer3D.drawModel_custom_unlit_shader(modelPlaneTransparentRed.meshes[j], modelPlaneTransparentRed.materials[j], transformAlpha);
//            }
//        }
        GL11.glEnable(GL11.GL_CULL_FACE);




        // TODO: sort by distance to camera!
        //GL11.glDisable(GL11.GL_CULL_FACE); //
        //GL20.glDepthMask(true);
        //GL20.glDisable(GL20.GL_DEPTH_TEST);

        GL20.glEnable(GL20.GL_DEPTH_TEST);
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE); //

        Renderer3D.end();

    }

    public static class Entity {

        public Matrix4x4 transform;
        public Model model;

    }

    public static class RenderUnit {

        public Matrix4x4 transform;
        public ModelMesh mesh;
        public ModelMaterial material;

    }


}
