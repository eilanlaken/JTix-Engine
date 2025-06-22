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

public class ScenePlanesGame_Terrain_New implements Scene {

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
    public Matrix4x4 transform_terrain = new Matrix4x4().translateGlobalAxisXYZ(0,0,5);

    public Shader mountainShader;

    public Model waterModel;
    public Shader waterShader;
    public Matrix4x4 transformWater = new Matrix4x4();

    public ScenePlanesGame_Terrain_New() {

    }

    @Override
    public void setup() {

        String terrainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/terrain.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/terrain.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String mountainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.vert");
        String mountainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/mountain-shader.frag");
        this.mountainShader = new Shader(mountainVertexShaderSrc, mountainFragmentShaderSrc);

        String vertexShaderSrc = Assets.getFileContent("assets/game-shaders/water.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/game-shaders/water.frag");
        this.waterShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        // load terrain
        Assets.loadModel("assets/game-models/plane-grid-512.fbx");
        Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/heightmap-test.jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/grass-bright.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/rock.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water-dark.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-snow.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();


        terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
        terrainHeightMap = Assets.get("assets/app-textures/heightmap-test.jpg");
        terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        terrainGrass = Assets.get("assets/game-maps/grass-bright.jpg");
        terrainStone = Assets.get("assets/game-maps/rock.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water-dark.jpg");
        terrainSnow = Assets.get("assets/app-textures/terrain-snow.jpg");

        terrain = Assets.get("assets/game-models/plane-grid-512.fbx");
        terrain.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);
        terrain.materials[0].shader = terrainShader;

        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.finishLoading();

        waterModel = Assets.get("assets/models/terrain-block.fbx");
        waterModel.materials[0].materialAttributes.put("time", 0.0f);
        waterModel.materials[0].materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        //terrain.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#9bd8c0"));
        waterModel.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        waterModel.materials[0].materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        waterModel.materials[0].materialAttributes.put("uWavesAmplitude", 1.6f);
        waterModel.materials[0].materialAttributes.put("uWavesSpeed", 0.3f);
        waterModel.materials[0].materialAttributes.put("uWavesFrequency", 0.002f);
        waterModel.materials[0].materialAttributes.put("uWavesPersistence", 1);
        waterModel.materials[0].materialAttributes.put("uWavesLacunarity", 2.4f);
        waterModel.materials[0].materialAttributes.put("uWavesIterations", 3);

        waterModel.materials[0].materialAttributes.put("uTroughThreshold", 0f);
        waterModel.materials[0].materialAttributes.put("uTroughTransition", 8f);
        waterModel.materials[0].materialAttributes.put("uPeakThreshold", 22);
        waterModel.materials[0].materialAttributes.put("uPeakTransition", 0.1f);
        waterModel.materials[0].shader = waterShader;
        waterModel.materials[0].transparent = true;
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
        float time = (float) waterModel.materials[0].materialAttributes.get("time");
        time += Graphics.getDeltaTime();
        waterModel.materials[0].materialAttributes.put("time", time);

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

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            waterModel.materials[0].materialAttributes.put("uWavesSpeed", 0.0f);
        }

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        Renderer3D.begin(camera);
        Renderer3D.drawModel(terrain, transform_terrain);
        Renderer3D.drawModel(waterModel, transformWater);
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,0,0));
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(0,512,0));
        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,512,0));
        Renderer3D.end();
    }

}
