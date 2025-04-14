package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.opengl.GL11;

public class SceneRendering3D_Terrain_1 implements Scene {

    private Camera camera;

    public Model terrain;

    public Shader blendMapShader;
    public Texture terrainBlendMap;
    public Texture terrainEarth;
    public Texture terrainGrass;
    public Texture terrainStone;
    public Texture terrainWater;

    public Model plane;

    public Matrix4x4 transform_terrain = new Matrix4x4();
    public Matrix4x4 transform_plane = new Matrix4x4();
    Renderer2D renderer2D = new Renderer2D();

    public SceneRendering3D_Terrain_1() {

    }

    @Override
    public void setup() {

        String vertexShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-blend-map-shader.vert");
        String fragmentShaderSrc = Assets.getFileContent("assets/app-shaders/terrain-blend-map-shader.frag");

        this.blendMapShader = new Shader(vertexShaderSrc, fragmentShaderSrc);

        Assets.loadModel("assets/models/plane_demo.fbx");
        Assets.loadModel("assets/models/terrain-block.fbx");
        Assets.loadTexture("assets/app-textures/blendmap-test.png", null, null, Texture.Wrap.REPEAT, Texture.Wrap.REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-earth.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-grass.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-stone.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.loadTexture("assets/app-textures/terrain-water.jpg", null, null, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, Graphics.getMaxAnisotropy());
        Assets.finishLoading();

        terrainBlendMap = Assets.get("assets/app-textures/blendmap-test.png");
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

        plane = Assets.get("assets/models/plane_demo.fbx");

    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 5000, 75);
        camera.position.set(0, 0, 20);

        camera.lookAt(0,10,0);

        camera.update();

        transform_plane.translateGlobalAxisXYZ(0,0,20);
        transform_plane.rotateGlobalAxisZ(180);

    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);


        float scroll = 3 * Input.mouse.getVerticalScroll();
        if (Input.mouse.getVerticalScroll() != 0) {
            camera.translateForward(scroll);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.translateRight(-panHorizontal);
            camera.translateUp(panVertical);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE)) {
            float panHorizontal = Input.mouse.getXDelta() * 0.2f;
            float panVertical = Input.mouse.getYDelta() * 0.2f;
            camera.rotateAroundUp(panHorizontal * 5);
            camera.rotateAroundRight(panVertical * 5);
        }

        camera.update();


        if (Input.keyboard.isKeyPressed(Keyboard.Key.E)) {
            transform_plane.rotateLocalAxisY(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            transform_plane.rotateLocalAxisY(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            transform_plane.rotateLocalAxisZ(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            transform_plane.rotateLocalAxisZ(-1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            transform_plane.rotateLocalAxisX(1);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            transform_plane.rotateLocalAxisX(-1);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.247f,0.247f,0.247f,1);

        renderer2D.begin();
        //renderer2D.drawCircleFilled(300, 30,0,0,0,1,1);
        renderer2D.end();

        Renderer3D.begin(camera);
        //System.out.println("----");
        for (int i = 0; i < terrain.meshes.length; i++) {
            Renderer3D.drawModel_custom_shader(blendMapShader, terrain.meshes[i], terrain.materials[i], transform_terrain);
        }

        for (int i = 0; i < plane.meshes.length; i++) {
            Renderer3D.drawModel_tmp_5(plane.meshes[i], plane.materials[i], transform_plane);
        }
        Renderer3D.end();
        //System.out.println("----");
        Renderer3D.end();
    }



}
