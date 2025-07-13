package com.heavybox.jtix.zzz_planes_tests;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
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

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneRendering3D_Physics_3D_1 implements Scene {

    private Camera camera;

    public Scene3D scene;
    public int currentNodeIndex = 0;
    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    private DynamicsWorld world;
    private Array<RigidBody> bodies = new Array<>();

    public SceneRendering3D_Physics_3D_1() {

    }

    @Override
    public void setup() {
        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);

        Assets.loadScene("assets/game-models/airplane_1.fbx", "assets/game-textures");
        Assets.finishLoading();

        scene = Assets.get("assets/game-models/airplane_1.fbx");
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight());
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -20, 20);
        camera.lookAt(0,0,0);
        camera.update();

        for (ModelMaterial material : scene.allMaterials) {
            //System.out.println(material);
        }
    }

    float angleZ = 0;

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
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            angleZ += 1;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentNodeIndex--;
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
            currentNodeIndex++;
        }
        Scene3D.Node node = scene.allNodes[currentNodeIndex];

        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);
        //Renderer3D.drawModel(node.model, new Matrix4x4().rotateGlobalAxisZ(angleZ));
        for (int i = 0; i < node.model.meshes.length; i++) {
            Renderer3D.drawModel(Renderer3D.defaultShaderWireframe, node.model.meshes[i], node.model.materials[i], new Matrix4x4().rotateGlobalAxisZ(angleZ));
        }
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
