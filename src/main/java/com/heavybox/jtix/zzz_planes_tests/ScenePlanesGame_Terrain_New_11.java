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

public class ScenePlanesGame_Terrain_New_11 implements Scene {

    private Camera camera;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainRoad;
    public Texture terrainWheatBright;
    public Texture terrainWheatDark;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;
    public Matrix4x4 transform_terrain = new Matrix4x4().translateGlobalAxisXYZ(0,0,0);

    public Model waterModel;
    public Shader waterShader;
    ModelMaterial m;
    public Matrix4x4 transformWater = new Matrix4x4();

    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    public ScenePlanesGame_Terrain_New_11() {

    }

    @Override
    public void setup() {

        String terrainVertexShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-4.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/game-shaders/terrain-4.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String vertexShaderSrc = Assets.getFileContent("assets/game-shaders/water.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/game-shaders/water.frag");
        this.waterShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        // load terrain
        Assets.loadShader("terrain", "assets/game-shaders/terrain-4.vert","assets/game-shaders/terrain-4.frag");
        Assets.loadModel("assets/game-maps/terrain-tile-257.fbx");
        Assets.loadTexture("assets/game-maps/terrain-blendmap-2048.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/heightmap-sample-2.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-stone.jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-grass-dark.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-road.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-bright.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/game-maps/terrain-wheat-dark.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();


        //terrainBlendMap = Graphics.getTextureSingleBlackPixelOpaque();//Assets.get("assets/game-maps/terrain-blendmap-demo.png");
        terrainBlendMap = Assets.get("assets/game-maps/terrain-blendmap-2048.png");
        terrainHeightMap = Assets.get("assets/game-maps/heightmap-sample-2.jpg");
        terrainStone = Assets.get("assets/game-maps/terrain-stone.jpg");
        terrainGrass = Assets.get("assets/game-maps/terrain-grass-dark.jpg"); // empty
        terrainRoad = Assets.get("assets/game-maps/terrain-road.jpg"); // r
        terrainWheatBright = Assets.get("assets/game-maps/terrain-wheat-bright.jpg"); // g
        terrainWheatDark = Assets.get("assets/game-maps/terrain-wheat-dark.jpg"); // b
        terrainWater = Assets.get("assets/game-maps/terrain-water.jpg"); // a

        terrain = Assets.get("assets/game-maps/terrain-tile-257.fbx");
        terrain.materials[0].materialAttributes.put("u_texture_steep", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_background", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainRoad);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainWheatBright);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWheatDark);
        terrain.materials[0].materialAttributes.put("u_texture_alpha", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);
        terrain.materials[0].materialAttributes.put("u_tile_index_row", 5);
        terrain.materials[0].materialAttributes.put("u_tile_index_col", 2);
        terrain.materials[0].shader = terrainShader;
        terrain.materials[0].transparent = false;

        m = terrain.materials[0].clone();
        m.materialAttributes.put("u_tile_index_row", 6);
        m.materialAttributes.put("u_tile_index_col", 2);
        Assets.loadModel("assets/models/terrain-block.fbx"); // for the water.
        Assets.finishLoading();

        waterModel = Assets.get("assets/models/terrain-block.fbx");
        waterModel.materials[0].materialAttributes.put("time", 0.0f);
        waterModel.materials[0].materialAttributes.put("uTroughColor", Color.valueOf("#186691"));
        waterModel.materials[0].materialAttributes.put("uSurfaceColor", Color.valueOf("#2a87a3"));
        waterModel.materials[0].materialAttributes.put("uPeakColor", Color.valueOf("#bbd8e0"));
        waterModel.materials[0].materialAttributes.put("uWavesAmplitude", 1.6f);
        waterModel.materials[0].materialAttributes.put("uWavesSpeed", 0.1f);
        waterModel.materials[0].materialAttributes.put("uWavesFrequency", 0.002f);
        waterModel.materials[0].materialAttributes.put("uWavesPersistence", 1);
        waterModel.materials[0].materialAttributes.put("uWavesLacunarity", 2.4f);
        waterModel.materials[0].materialAttributes.put("uWavesIterations", 3);
        waterModel.materials[0].materialAttributes.put("uTroughThreshold", 0f);
        waterModel.materials[0].materialAttributes.put("uTroughTransition", 8f);
        waterModel.materials[0].materialAttributes.put("uPeakThreshold", 22);
        waterModel.materials[0].materialAttributes.put("uPeakTransition", 0.1f);
        waterModel.materials[0].shader = waterShader;
        waterModel.materials[0].transparent = false;


        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight());
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
        time += 1 * Graphics.getDeltaTime();
        waterModel.materials[0].materialAttributes.put("time", time % 600);
//        waterModel.materials[0].materialAttributes.put("time", 3 * MathUtils.sinRad(3 * time / (MathUtils.PI_TWO)));
//        System.out.println(5 * MathUtils.sinRad(time / (MathUtils.PI_TWO)));

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

        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {

            }
        }
        //Renderer3D.drawModel(terrainShader, terrain.meshes[0], terrain.materials[0], new Matrix4x4());
        Renderer3D.drawModel(terrainShader, terrain.meshes[0], terrain.materials[0], new Matrix4x4());
        Renderer3D.drawModel(terrainShader, terrain.meshes[0], m, new Matrix4x4().translateGlobalAxisXYZ(-0,-1000,0));


        Renderer3D.drawMesh(waterShader, terrain.meshes[0], waterModel.materials[0], new Matrix4x4(transformWater).translateGlobalAxisXYZ(0,0,0));

//        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(0,0,0));
//        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,0,0));
//        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(0,512,0));
//        Renderer3D.drawModel(waterModel, new Matrix4x4(transformWater).translateGlobalAxisXYZ(512,512,0));
        Renderer3D.end();

        FrameBufferBinder.bind();
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderer2D.begin();
        renderer2D.setShader(postProcessingHDR);
        renderer2D.drawTexture(sceneFrameBuffer.getColorAttachment0(), 0,0,0,1,-1);
        renderer2D.end();
    }

}
