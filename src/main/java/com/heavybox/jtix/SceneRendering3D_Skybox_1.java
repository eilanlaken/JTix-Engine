package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.zzz_planes_tests.GameObjectAirplane;
import org.lwjgl.opengl.GL11;

public class SceneRendering3D_Skybox_1 implements Scene {

    private Camera camera;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;

    public Model model;

    public Matrix4x4 transform_terrain = new Matrix4x4();
    public Matrix4x4 transform_model = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public Model model_big;
    public Matrix4x4 transform = new Matrix4x4();

    private GameObjectAirplane airplane = new GameObjectAirplane();


    public SceneRendering3D_Skybox_1() {

    }

    @Override
    public void setup() {

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/water-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/water-shader.frag");

        this.terrainShader = new Shader(vertexShaderSrc, fragmentShaderSrc);


        Assets.loadTexture("assets/app-models/textures/skybox-3/nx.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/ny.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/nz.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/px.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/py.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/app-models/textures/skybox-3/pz.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.finishLoading();

        System.out.println(Assets.getStoreCatalog());


        Assets.loadModel("assets/app-models/oil-rig.fbx", "assets/app-models/textures");
        Assets.loadModel("assets/app-models/skybox_1.fbx", "assets/app-models/textures/skybox-3");
        Assets.loadModel("assets/models/terrain-block.fbx");
        //Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        //Assets.loadTexture("assets/app-textures/heightmap-test.jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        //Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        //Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        //Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();


        //terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
        //terrainHeightMap = Assets.get("assets/app-textures/heightmap-test.jpg");
        //terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        //terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        //terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");


        terrain = Assets.get("assets/models/terrain-block.fbx");

        terrain.materials[0].materialAttributes.put("u_texture_water", terrainWater);
        terrain.materials[0].materialAttributes.put("time", 0.0f);

        model = Assets.get("assets/app-models/skybox_1.fbx");
        model_big = Assets.get("assets/app-models/oil-rig.fbx");

        transform_model.setToScaling(4000,4000,4000);


    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, 0, 150);

        camera.lookAt(0,50,0);

        camera.update();

    }


    @Override
    public void update() {
        float time = (float) terrain.materials[0].materialAttributes.get("time");
        time += Graphics.getDeltaTime();
        terrain.materials[0].materialAttributes.put("time", time);

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);


        float scroll = 3 * Input.mouse.getVerticalScroll();
        if (Input.mouse.getVerticalScroll() != 0) {
            camera.translateForward(scroll * 10);
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
            //camera.rotateAroundUp(panHorizontal * 5);
            //camera.rotateAroundRight(panVertical * 5);
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }

        camera.update();

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            transform_terrain.translateGlobalAxisXYZ(0,0,1);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        //update_gameplay();

        Color sky = Color.valueOf("#18267f");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        //System.out.println("----");
        for (int i = 0; i < terrain.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(terrainShader, terrain.meshes[i], terrain.materials[i], transform_terrain);
        }

        for (int i = 0; i < model.meshes.length; i++) {
            transform_model.setTranslation(camera.position);
            Renderer3D.drawModel_custom_unlit_shader(model.meshes[i], model.materials[i], transform_model);
        }

        for (int i = 0; i < model_big.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(model_big.meshes[i], model_big.materials[i], transform);
        }
        Renderer3D.end();
    }

    private void update_gameplay() {
        float delta = Graphics.getDeltaTime();
        Vector3 velocity = new Vector3(camera.forward).scl(airplane.speed);
        camera.position.add(delta * velocity.x, delta * velocity.y, delta * velocity.z);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) airplane.speed += delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) airplane.speed -= delta * 20;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) camera.rotateAroundForward(delta * -90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) camera.rotateAroundForward(delta * 90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) camera.rotateAroundRight(delta * -90);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) camera.rotateAroundRight(delta * 90);
    }

}
