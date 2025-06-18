package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_ModelsViewer_Volcano implements Scene {

    private Camera camera;

    public Scene3D scene3D;
    public Scene3D.Node volcanoLava;
    public Scene3D.Node volcanoMountain;
    public ModelMaterial lavaMaterial;
    public float time = 0;

    public Shader volcanoLavaShader;
    public Shader volcanoMountainShader;

    public SceneRendering3D_ModelsViewer_Volcano() {

    }

    @Override
    public void setup() {

        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-snow.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());

        Assets.loadShader("volcano-lava-shader", "assets/game-shaders/map-1-volcano-lava-shader.vert", "assets/game-shaders/map-1-volcano-lava-shader.frag");

        String mountainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.vert");
        String mountainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.frag");
        volcanoMountainShader = new Shader(mountainVertexShaderSrc, mountainFragmentShaderSrc);

        Assets.loadScene("assets/game-models/map-1-mountain-volcano.fbx", "assets/game-textures");
        Assets.finishLoading();

        scene3D = Assets.get("assets/game-models/map-1-mountain-volcano.fbx");

        volcanoLavaShader = Assets.get("volcano-lava-shader");

        volcanoMountain = scene3D.namedNodes.get("volcano");
        ModelMaterial volcanoMountainMaterial = volcanoMountain.model.materials[0];

        Texture terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        Texture terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        Texture terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        Texture terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");
        Texture terrainSnow = Assets.get("assets/app-textures/terrain-snow.jpg");
        volcanoMountainMaterial.materialAttributes.put("u_texture_grass", terrainStone);
        volcanoMountainMaterial.materialAttributes.put("u_texture_stone", terrainStone);
        volcanoMountainMaterial.materialAttributes.put("u_texture_snow", terrainSnow);
        volcanoMountainMaterial.shader = volcanoMountainShader;

        volcanoLava = scene3D.namedNodes.get("lava");
        lavaMaterial = volcanoLava.model.materials[0];
        lavaMaterial.materialAttributes.put("u_time", 0f);
        lavaMaterial.shader = volcanoLavaShader;
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -540, 260);

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

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        time += Graphics.getDeltaTime() * 0.04f;
        lavaMaterial.materialAttributes.put("u_time", time);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        Renderer3D.begin(camera);


        Scene3D.Node nodeHouse = scene3D.namedNodes.get("volcano");
        Matrix4x4 transformHouse = nodeHouse.localTransform;
        transformHouse.idt();
        //transformHouse.rotateLocalAxisZ(30);
        Model modelHouse = nodeHouse.model;

        for (int i = 0; i < modelHouse.meshes.length; i++) {
                //Renderer3D.drawModel_tmp_6(modelHouse.meshes[i], modelHouse.materials[i], transformHouse);
        }

        volcanoLava = scene3D.namedNodes.get("lava");
        Matrix4x4 transformBalloon = volcanoLava.localTransform;
        Matrix4x4 t = new Matrix4x4(transformBalloon).mulLeft(transformHouse); // to apply the transform, multiply from the left
        // TODO: need to consider entire tree
        Model model = volcanoLava.model;
        for (int i = 0; i < model.meshes.length; i++) {
            //Renderer3D.drawModel_tmp_6(model.meshes[i], model.materials[i], t);
        }

        Renderer3D.drawModel(volcanoLava.model, t);
        Renderer3D.drawModel(modelHouse, transformHouse);
        if (volcanoMountainShader.getUniformValue("u_texture_grass") != null)
        System.out.println(volcanoMountainShader.getUniformValue("u_texture_grass").getClass().getSimpleName());

        Renderer3D.end();
    }



}
