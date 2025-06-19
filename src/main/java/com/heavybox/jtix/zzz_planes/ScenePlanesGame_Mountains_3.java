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

public class ScenePlanesGame_Mountains_3 implements Scene {

    private Camera camera;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainSnow;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;
    public Matrix4x4 transform_terrain = new Matrix4x4();

    public Model mountain_1;
    public Model mountain_2;
    public Shader mountainShader;

    public Scene3D volcano;
    public Scene3D.Node nodeVolcanoLava;
    public Scene3D.Node nodeVolcanoMountain;
    public ModelMaterial lavaMaterial;
    public float time = 0;
    public Shader volcanoLavaShader;
    public Shader volcanoMountainShader;

    public ScenePlanesGame_Mountains_3() {

    }

    @Override
    public void setup() {

        String terrainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-shader.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-shader.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String mountainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.vert");
        String mountainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.frag");
        this.mountainShader = new Shader(mountainVertexShaderSrc, mountainFragmentShaderSrc);

        String lavaVertexShaderSrc = Assets.getFileContent("assets/game-shaders/map-1-volcano-lava-shader.vert");
        String lavaFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/map-1-volcano-lava-shader.frag");
        volcanoLavaShader = new Shader(lavaVertexShaderSrc, lavaFragmentShaderSrc);

        String volcanoVertexShaderSrc = Assets.getFileContent("assets/game-shaders/volcano-shader.vert");
        String volcanoFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/volcano-shader.frag");
        volcanoMountainShader = new Shader(volcanoVertexShaderSrc, volcanoFragmentShaderSrc);

        // load terrain
        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.loadModel("assets/app-models/mountain_3.fbx");
        Assets.loadModel("assets/app-models/mountain_2.fbx");
        Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/heightmap-test.jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-snow.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();

        Assets.loadScene("assets/game-models/map-1-mountain-volcano.fbx", "assets/game-textures");
        Assets.finishLoading();


        terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
        terrainHeightMap = Assets.get("assets/app-textures/heightmap-test.jpg");
        terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");
        terrainSnow = Assets.get("assets/app-textures/terrain-snow.jpg");

        terrain = Assets.get("assets/models/terrain-block.fbx");
        terrain.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);
        terrain.materials[0].shader = terrainShader;

        mountain_1 = Assets.get("assets/app-models/mountain_3.fbx");
        mountain_1.materials[0].materialAttributes.put("u_texture_grass", terrainGrass);
        mountain_1.materials[0].materialAttributes.put("u_texture_stone", terrainStone);
        mountain_1.materials[0].materialAttributes.put("u_texture_snow", terrainSnow);
        mountain_1.materials[0].shader = mountainShader;

        mountain_2 = Assets.get("assets/app-models/mountain_2.fbx");
        mountain_2.materials[0].materialAttributes.put("u_texture_grass", terrainGrass);
        mountain_2.materials[0].materialAttributes.put("u_texture_stone", terrainStone);
        mountain_2.materials[0].materialAttributes.put("u_texture_snow", terrainSnow);
        mountain_2.materials[0].shader = mountainShader;

        volcano = Assets.get("assets/game-models/map-1-mountain-volcano.fbx");

        nodeVolcanoMountain = volcano.namedNodes.get("volcano");
        ModelMaterial volcanoMountainMaterial = nodeVolcanoMountain.model.materials[0];
        volcanoMountainMaterial.materialAttributes.put("u_texture_grass", terrainGrass);
        volcanoMountainMaterial.materialAttributes.put("u_texture_stone", terrainStone);
        volcanoMountainMaterial.materialAttributes.put("u_texture_snow", terrainSnow);
        volcanoMountainMaterial.shader = volcanoMountainShader;

        nodeVolcanoLava = volcano.namedNodes.get("lava");
        lavaMaterial = nodeVolcanoLava.model.materials[0];
        lavaMaterial.materialAttributes.put("u_time", 0f);
        lavaMaterial.shader = volcanoLavaShader;
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -550, 250);
        camera.lookAt(0,0,0);
        camera.update();
    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        float scroll = Input.mouse.getVerticalScroll();
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
            if (camera.mode == Camera.Mode.ORTHOGRAPHIC) camera.mode = Camera.Mode.PERSPECTIVE;
            else {
                camera.mode = Camera.Mode.ORTHOGRAPHIC;
                camera.forward.set(0,0,-1);
                camera.up.set(0,1,0);
                camera.update();
                camera.position.set(0, 0, 30);
                camera.lookAt(0,0,0);
                camera.update();
            }
        } else if (Input.mouse.getVerticalScroll() != 0) {
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
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && camera.mode == Camera.Mode.PERSPECTIVE) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }
        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }

        Matrix4x4 transformVolcano = nodeVolcanoMountain.localTransform;
        transformVolcano.idt();
        Matrix4x4 transformLava = new Matrix4x4(nodeVolcanoLava.localTransform).mulLeft(transformVolcano); // to apply the transform, multiply from the left
        time += Graphics.getDeltaTime() * 0.04f;
        lavaMaterial.materialAttributes.put("u_time", time);

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        Renderer3D.begin(camera);


        // TODO: lights
        Renderer3D.drawModel(mountain_1, new Matrix4x4().translateGlobalAxisXYZ(512+255,0,0));
        Renderer3D.drawModel(mountain_2, new Matrix4x4().translateGlobalAxisXYZ(512+255,1024,0));
        Renderer3D.drawModel(terrain, transform_terrain);

        // volcano
        Renderer3D.drawModel(nodeVolcanoMountain.model, transformVolcano);
        Renderer3D.drawModel(nodeVolcanoLava.model, transformLava);

        Renderer3D.end();
    }

}
