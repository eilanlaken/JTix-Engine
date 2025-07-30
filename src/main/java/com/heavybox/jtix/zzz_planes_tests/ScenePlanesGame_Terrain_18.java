package com.heavybox.jtix.zzz_planes_tests;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.zzz_project.GameObjectTerrainTile;
import com.heavybox.jtix.zzz_project.GameObjectTerrainWater;
import org.lwjgl.opengl.GL11;

// TODO:
// see here:
// https://github.com/ebruneton/precomputed_atmospheric_scattering
// https://threejs.org/examples/?q=sky#webgl_shaders_sky
// https://github.com/mrdoob/three.js/blob/master/examples/webgl_shaders_sky.html
// https://github.com/mrdoob/three.js/blob/master/examples/jsm/objects/Sky.js

// TERRAIN:
// https://tangrams.github.io/heightmapper/#6.04167/-19.436/350.455

// https://manticorp.github.io/unrealheightmap/#latitude/28.101057958669458/longitude/87.47314453125/zoom/10/outputzoom/13/width/8192/height/8192
public class ScenePlanesGame_Terrain_18 implements Scene {

    private Camera camera;

    GameObjectTerrainTile[][] tiles = new GameObjectTerrainTile[64][64];
    GameObjectTerrainWater[][] water = new GameObjectTerrainWater[64][64];
    GameObjectTerrainWater w = new GameObjectTerrainWater(0,0);

    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    ModelMesh box = ModelMesh.createCubeInverted(40000,40000,40000);
    Shader skyShader;
    ModelMaterial skyMaterial;

    Texture heightmap;

    public ScenePlanesGame_Terrain_18() {
    }

    @Override
    public void setup() {

        heightmap = new Texture("assets/game-maps/heightmap-16b-gscale-8k.png", null, false);

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                // tiles[i][j] = new GameObjectTerrainTile(i,j, -32 * 1000 + 500 + j * 1000, 32 * 1000 - 500 - i * 1000);
                tiles[i][j] = new GameObjectTerrainTile(i,j, -32 * 2000 + 1000 + j * 2000, 32 * 2000 - 1000 - i * 2000, heightmap);
                water[i][j] = new GameObjectTerrainWater(i,j, -32 * 2000 + 1000 + j * 2000, 32 * 2000 - 1000 - i * 2000);
            }
        }


        String skyVertex = Assets.getFileContent("assets/game-shaders/sky.vert");
        String skyFragment = Assets.getFileContent("assets/game-shaders/sky.frag");
        skyShader = new Shader(skyVertex, skyFragment);

        skyMaterial = ModelMaterial.create();
        // low
        skyMaterial.materialAttributes.put("turbidity", 0.02f);
        skyMaterial.materialAttributes.put("rayleigh", 2f);
        skyMaterial.materialAttributes.put("mieCoefficient", 0.1f);
        skyMaterial.materialAttributes.put("mieDirectionalG", 0.8f);

        // heigh
//        skyMaterial.materialAttributes.put("turbidity", 0.002f);
//        skyMaterial.materialAttributes.put("rayleigh", 0.1f);
//        skyMaterial.materialAttributes.put("mieCoefficient", 0.1f);
//        skyMaterial.materialAttributes.put("mieDirectionalG", 0.8f);

        float phi = (90 - 2) * MathUtils.degreesToRadians;



		float theta = 180 * MathUtils.degreesToRadians;
        Vector3 sun = new Vector3().setFromSphericalRad( 1, theta, phi );

        skyMaterial.materialAttributes.put("sunPosition", sun); // (0, 0, 0) by default
        skyMaterial.materialAttributes.put("up", new Vector3(0, 0, 1));
        skyMaterial.shader = skyShader;


        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight(), true);
    }

    @Override
    public void finish() {
        heightmap.delete();
    }

    @Override
    public void start() {
        Graphics.setTargetFps(60);
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 400000, 75);
        camera.position.set(0, -800, 1000);
        camera.lookAt(0,-1,1000);
        camera.update();
    }

    // gameplay
    float correctionF = 0;
    float correctionP = 0;
    private float speed = 1;

    private void update_gameplay() {
        float delta = Graphics.getDeltaTime();
        Vector3 velocity = new Vector3(camera.forward).scl(speed);
        System.out.println(speed);
        camera.position.add(delta * velocity.x, delta * velocity.y, delta * velocity.z);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.KEY_1)) speed = 343;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.KEY_2)) speed = 343 * 2;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) speed += delta * 500;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Z)) speed -= delta * 500;
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            camera.rotateAroundForward(delta * -90);
            correctionF -= delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            camera.rotateAroundForward(delta * 90);
            correctionF += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            camera.rotateAroundRight(delta * -90);
            correctionP += delta * 90;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            camera.rotateAroundRight(delta * 90);
            correctionP -= delta * 90;
        }
    }

    @Override
    public void update() {
        float delta = Graphics.getDeltaTime();
//        System.out.println(camera.position.z);
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
        update_gameplay();


        if (Input.keyboard.isKeyPressed(Keyboard.Key.F)) {
            Renderer3D.lightDir.rotate(1f,1,0,0);
        }


        Color sky = Color.valueOf("#87CEEB");
        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(sky.r,sky.g,sky.b,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);
        //Renderer3D.drawModel(skyShader, box, skyMaterial, new Matrix4x4());
        w.update(delta);
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                //water[i][j].update(delta);
                //Renderer3D.drawModel(water[i][j].shader, water[i][j].mesh, water[i][j].material, water[i][j].transform);
                Renderer3D.drawModel(tiles[i][j].shader, tiles[i][j].mesh, tiles[i][j].material, tiles[i][j].transform);
            }
        }
        //Renderer3D.drawModel(w.shader, w.mesh, w.material, new Matrix4x4().translateGlobalAxisXYZ(1000,0,0));
        //Renderer3D.drawModel(w.shader, w.mesh, w.material, new Matrix4x4().translateGlobalAxisXYZ(3000,0,0));

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
