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

public class ScenePlanesGame_TileBuilder_3 implements Scene {

    private Camera camera;

    public Model terrain;
    public Shader terrainShader;
    public Texture terrainBlendMap;
    public Texture terrainHeightMap;
    public Texture terrainEarth;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;

    public Model model_gameobject;

    public Matrix4x4 transform_terrain = new Matrix4x4();
    public Matrix4x4 transform_gameobject = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    private GameObjectAirplane airplane = new GameObjectAirplane();

    private Array<TerrainToken> gameObjects = new Array<>(false, 10);

    // tools
    public Tool activeTool;
    public ToolHouseStamp toolHouseStamp;
    public ToolTreeStamp toolTreeStamp;
    public ToolPropStamp toolPropStamp;

    public ScenePlanesGame_TileBuilder_3() {

    }

    @Override
    public void setup() {

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-shader.frag");

        this.terrainShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        // load fields
        for (String fieldsPath : Constants.MODELS_FILE_PATH_FIELDS) {
            Assets.loadModel(fieldsPath, Constants.MODELS_TEXTURES_PATH);
        }

        // load rocks
        Assets.loadModel("assets/app-models/rocks-black_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-black_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-black_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-black_4.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-grey_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-grey_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-grey_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-grey_4.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-white_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-white_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-white_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rocks-white_4.fbx", Constants.MODELS_TEXTURES_PATH);

        // load trees
        for (String fieldsPath : Constants.MODELS_FILE_PATH_TREES) {
            Assets.loadModel(fieldsPath, Constants.MODELS_TEXTURES_PATH);
        }

        // load houses
        Assets.loadModel("assets/app-models/rural-house-small_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-small_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-small_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-small_4.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-small_5.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-small_6.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_4.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_5.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/rural-house-big_6.fbx", Constants.MODELS_TEXTURES_PATH);

        // load props
        Assets.loadModel("assets/app-models/prop-fence.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-satellite-dish-small.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-shipping-container_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-shipping-container_2.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-shipping-container_3.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-solar-panels.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-transmission-tower.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-transmission-tower-lines.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-wind-turbine-fan.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-wind-turbine-tower.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-windmill_1.fbx", Constants.MODELS_TEXTURES_PATH);
        Assets.loadModel("assets/app-models/prop-windmill_2.fbx", Constants.MODELS_TEXTURES_PATH);

        // load terrain
        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/heightmap-test.jpg", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();

        terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
        terrainHeightMap = Assets.get("assets/app-textures/heightmap-test.jpg");
        terrainEarth = Assets.get("assets/app-textures/terrain-earth.jpg");
        terrainGrass = Assets.get("assets/app-textures/terrain-grass.jpg");
        terrainStone = Assets.get("assets/app-textures/terrain-stone.jpg");
        terrainWater = Assets.get("assets/app-textures/terrain-water.jpg");

        //model = Assets.get("assets/models/plane_demo.fbx");
        terrain = Assets.get("assets/models/terrain-block.fbx");

        terrain.materials[0].materialAttributes.put("u_texture_background", terrainStone);
        terrain.materials[0].materialAttributes.put("u_texture_red", terrainEarth);
        terrain.materials[0].materialAttributes.put("u_texture_green", terrainGrass);
        terrain.materials[0].materialAttributes.put("u_texture_blue", terrainWater);
        terrain.materials[0].materialAttributes.put("u_texture_blend_map", terrainBlendMap);
        terrain.materials[0].materialAttributes.put("u_texture_height_map", terrainHeightMap);

        model_gameobject = Assets.get(Constants.MODELS_FILE_PATH_FIELDS[3]);
        transform_gameobject.setTranslation(0,200,0);

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        //camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 5000, 75);
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1f/4, 1, 5000, 75);
        camera.position.set(0, 0, 30);

        camera.lookAt(0,0,0);

        camera.update();

        this.toolHouseStamp = new ToolHouseStamp(camera, gameObjects, terrainHeightMap);
        this.toolTreeStamp = new ToolTreeStamp(camera, gameObjects, terrainHeightMap);
        this.toolPropStamp = new ToolPropStamp(camera, gameObjects, terrainHeightMap);

        this.activeTool = toolHouseStamp;
    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        transform_gameobject.setTranslation(screen.x, screen.y, 0);

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
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && camera.mode == Camera.Mode.PERSPECTIVE) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundAxis(panHorizontal * 5,0,0,1);
            camera.rotateAroundRight(panVertical * 5);
        }
        camera.update();

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            activeTool = toolHouseStamp;
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            activeTool = toolTreeStamp;
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3)) {
            activeTool = toolPropStamp;
        }

        // update tools
        if (activeTool != null) activeTool.update();

//        if (Input.mouse.isButtonPressed(Mouse.Button.RIGHT)) {
//            transform_gameobject.rotateLocalAxisZ(Input.mouse.getYDelta());
//        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) { // export json
//            GameObject go = new GameObject();
//            go.model = model_gameobject;
//            go.transform = transform_gameobject.cpy();
//            gameObjects.add(go);
//        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER)) {
//            toJSON();
//        }

        //update_gameplay();

        Color sky = Color.valueOf("#87CEEB");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        //System.out.println("----");
        for (int i = 0; i < terrain.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader_2(terrainShader, terrain.meshes[i], terrain.materials[i], transform_terrain);
        }

//        for (int i = 0; i < model_gameobject.meshes.length; i++) {
//            Renderer3D.drawModel_tmp_5(model_gameobject.meshes[i], model_gameobject.materials[i], transform_gameobject);
//        }

        // draw tools overlay
        if (activeTool != null) activeTool.render();

        for (TerrainToken terrainToken : gameObjects) {
            Model model = terrainToken.model;
            Matrix4x4 transform = terrainToken.transform;
            for (int i = 0; i < model.meshes.length; i++) {
                Renderer3D.drawModel_tmp_5(model.meshes[i], model.materials[i], transform);
            }
        }
        Renderer3D.end();
    }

    private void toJSON() {

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
