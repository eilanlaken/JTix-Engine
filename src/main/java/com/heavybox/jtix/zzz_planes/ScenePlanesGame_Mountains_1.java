package com.heavybox.jtix.zzz_planes;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScenePlanesGame_Mountains_1 implements Scene {

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


    private final Array<TerrainToken> tokens = new Array<>(false, 10);

    public Model mountain;
    public Shader mountainShader;


    public ScenePlanesGame_Mountains_1() {

    }

    @Override
    public void setup() {

        String terrainVertexShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String mountainVertexShaderSrc = Assets.getFileContent("assets/app-shaders/mountain-shader.vert");
        String mountainFragmentShaderSrc = Assets.getFileContent("assets/app-shaders/mountain-shader.frag");
        this.mountainShader = new Shader(mountainVertexShaderSrc, mountainFragmentShaderSrc);

        // load fields
        for (String fieldsPath : Constants.MODELS_FILE_PATH_FIELDS) {
            Assets.loadModel(fieldsPath, Constants.MODELS_TEXTURES_PATH);
        }

        // load terrain
        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.loadModel("assets/app-models/mountains.fbx");
        Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/heightmap-sea-level.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-snow.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();

        terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
        terrainHeightMap = Assets.get("assets/app-textures/heightmap-sea-level.png");
        terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");
        terrainSnow = Assets.get("assets/app-textures/terrain-snow.jpg");

        //model = Assets.get("assets/models/plane_demo.fbx");
        terrain = Assets.get("assets/models/terrain-block.fbx");
        terrain.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);

        mountain = Assets.get("assets/app-models/mountains.fbx");
        mountain.materials[0].materialAttributes.put("u_texture_stone", terrainStone);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -40, 0);
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

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.X)) {
            if (tokens.size != 0) tokens.removeIndex(tokens.size - 1);
        }

        //update_gameplay();

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        Renderer3D.begin(camera);
        //System.out.println("----");
        for (int i = 0; i < terrain.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(terrainShader, terrain.meshes[i], terrain.materials[i], transform_terrain);
        }

        for (int i = 0; i < mountain.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(mountainShader, mountain.meshes[i], mountain.materials[i], new Matrix4x4().translateGlobalAxisXYZ(0,100,0));
        }

        for (TerrainToken terrainToken : tokens) {
            Model model = terrainToken.model;
            Matrix4x4 transform = terrainToken.transform;
            for (int i = 0; i < model.meshes.length; i++) {
                Renderer3D.drawModel_tmp_5(model.meshes[i], model.materials[i], transform);
            }
        }

        Renderer3D.end();
    }

}
