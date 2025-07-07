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

// TODO:
/*
Implement:
- relative velocity field
- rain drops
- lightnings
- HDR
- bloom
- explosions
- God rays (radial blur)
 */
public class SceneRendering3D_VFX_2 implements Scene {

    private Camera camera;

    public Model model_1;
    public Model model_2;
    public int currentNodeIndex = 0;
    Renderer2D renderer2D = new Renderer2D();
    FrameBuffer sceneFrameBuffer;
    Shader postProcessingHDR;

    public SceneRendering3D_VFX_2() {

    }

    @Override
    public void setup() {

        String ppHDRVertex = Assets.getFileContent("assets/game-shaders/post-processing-HDR.vert");
        String ppHDRFragment = Assets.getFileContent("assets/game-shaders/post-processing-HDR.frag");
        postProcessingHDR = new Shader(ppHDRVertex, ppHDRFragment);

        Assets.loadModel("assets/engine-tests/cube-metal-criss.fbx", "assets/engine-tests");
        Assets.loadModel("assets/engine-tests/cube-wood.fbx", "assets/engine-tests");
        Assets.finishLoading();

        model_1 = Assets.get("assets/engine-tests/cube-wood.fbx");
        model_2 = Assets.get("assets/engine-tests/cube-metal-criss.fbx");
        sceneFrameBuffer = new FrameBuffer(Graphics.getWindowWidth(), Graphics.getWindowHeight(), 2);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.PERSPECTIVE, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 1, 10000, 75);
        camera.position.set(0, -5, 5);
        camera.lookAt(0,0,0);
        camera.update();
    }

    float angleZ = 0;
    float p1x = 0, p1y = 0;
    float p2x = 0, p2y = 0;
    float vx = 4, vy = 4;
    Matrix4x4 boxTransform = new Matrix4x4();

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
            boxTransform.rotateLocalAxis(1,1,1,2);
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            Renderer3D.lightDir.rotate(2,1,0,0);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            currentNodeIndex--;
        }
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) {
            currentNodeIndex++;
        }

        FrameBufferBinder.bind(sceneFrameBuffer);
        GL11.glClearColor(1,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Renderer3D.begin(camera);
        Renderer3D.drawModel(model_1, boxTransform);
        Renderer3D.drawModel(model_2, new Matrix4x4(boxTransform).translateGlobalAxisXYZ(-3,0,0));
        Renderer3D.end();

        FrameBufferBinder.bind();
        GL11.glClearColor(1,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderer2D.begin();
        renderer2D.setShader(postProcessingHDR);
        renderer2D.drawTexture(sceneFrameBuffer.getColorAttachment1(), 0,0,0,1,-1);
        renderer2D.end();


        renderer2D.begin();
        p1x += vx;
        p1y += vy;
        p2x += 2 * vx;
        p2y += 2 * vy;
        // TODO: RELATIVE VELOCITY.
        renderer2D.drawLineFilled(p1x,p1y, p2x,p2y,2f);
        renderer2D.end();
    }



}
