package com.heavybox.jtix.zzz_planes_tests;

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

    public Scene3D volcano;
    public Scene3D.Node volcanoLava;
    public Scene3D.Node volcanoMountain;
    public ModelMaterial lavaMaterial;
    public float time = 0;
    public Shader volcanoLavaShader;
    public Shader volcanoMountainShader;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainSnow;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;

    public Matrix4x4 transform_terrain_1 = new Matrix4x4().translateGlobalAxisXYZ(-256,256,0);
    public Matrix4x4 transform_terrain_2 = new Matrix4x4();
    public Matrix4x4 transform_terrain_3 = new Matrix4x4();
    public Matrix4x4 transform_terrain_4 = new Matrix4x4();

    public SceneRendering3D_ModelsViewer_Volcano() {

    }

    @Override
    public void setup() {

        String terrainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-shader.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-shader.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        // TODO: move to game-textures
        Assets.loadTexture("assets/game-maps/tile[3][8].jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/blendmap-all-earth.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/heightmap-sea-level.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
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
        Assets.loadModel("assets/game-models/terrain-block.fbx");

        Assets.finishLoading();

        volcano = Assets.get("assets/game-models/map-1-mountain-volcano.fbx");

        volcanoLavaShader = Assets.get("volcano-lava-shader");

        volcanoMountain = volcano.namedNodes.get("volcano");
        ModelMaterial volcanoMountainMaterial = volcanoMountain.model.materials[0];

        terrainBlendMap = Assets.get("assets/game-maps/blendmap-all-earth.png");
        terrainHeightMap = Assets.get("assets/app-textures/heightmap-sea-level.png");
        Texture terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        Texture terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        Texture terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        Texture terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");
        Texture terrainSnow = Assets.get("assets/app-textures/terrain-snow.jpg");
        volcanoMountainMaterial.materialAttributes.put("u_texture_grass", terrainGrass);
        volcanoMountainMaterial.materialAttributes.put("u_texture_stone", terrainStone);
        volcanoMountainMaterial.materialAttributes.put("u_texture_snow", terrainSnow);
        volcanoMountainMaterial.shader = volcanoMountainShader;

        volcanoLava = volcano.namedNodes.get("lava");
        lavaMaterial = volcanoLava.model.materials[0];
        lavaMaterial.materialAttributes.put("u_time", 0f);
        lavaMaterial.shader = volcanoLavaShader;

        terrain = Assets.get("assets/game-models/terrain-block.fbx");
        terrain.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);
        terrain.materials[0].shader = terrainShader;
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


        Scene3D.Node nodeHouse = volcano.namedNodes.get("volcano");
        Matrix4x4 transformHouse = nodeHouse.localTransform;
        transformHouse.idt();
        Model modelHouse = nodeHouse.model;
        volcanoLava = volcano.namedNodes.get("lava");
        Matrix4x4 transformBalloon = volcanoLava.localTransform;
        Matrix4x4 t = new Matrix4x4(transformBalloon).mulLeft(transformHouse); // to apply the transform, multiply from the left

        Renderer3D.drawModel(volcanoLava.model, t);
        Renderer3D.drawModel(modelHouse, transformHouse);
        Renderer3D.drawModel(terrain, transform_terrain_1);
        Renderer3D.end();
    }



}
