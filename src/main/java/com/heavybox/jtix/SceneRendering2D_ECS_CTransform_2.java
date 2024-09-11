package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.ecs.ComponentTransform;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_ECS_CTransform_2 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    ComponentTransform t = new ComponentTransform();

    public SceneRendering2D_ECS_CTransform_2() {
        renderer2D = new Renderer2D();

    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();

        ByteBuffer ttf;
        try {
            ttf = AssetUtils.ioResourceToByteBuffer("assets/fonts/OpenSans-Italic.ttf", 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        STBTTFontinfo info = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        int ascent;
        int descent;
        int lineGap;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            STBTruetype.stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }

        // https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Truetype.java
        // init

        float contentScaleX = GraphicsUtils.getContentScaleX();
        float contentScaleY = GraphicsUtils.getContentScaleY();

        int BITMAP_W = Math.round(1024 * contentScaleX);
        int BITMAP_H = Math.round(1024 * contentScaleY);

        int fontHeight = 24;

        Texture charactersTexture;
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H * 4);

        int result = STBTruetype.stbtt_BakeFontBitmap(ttf, fontHeight * contentScaleY, bitmap, BITMAP_W, BITMAP_H, 32, cdata);
        if (result <= 0) {
            throw new RuntimeException("Failed to bake font bitmap");
        }

        try {
            AssetUtils.saveImage("assets/fonts", "example", bitmap, BITMAP_W, BITMAP_H);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Override
    protected void refresh() {


        if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            t.x -= 10f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) {
            t.x += 10f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            t.y += 10f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            t.y -= 10f;
        }


        if (Keyboard.isKeyPressed(Keyboard.Key.X)) {
            t.angleXDeg += 0.5f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
            t.angleYDeg += 0.5f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Z)) {
            t.angleZDeg += 0.5f;
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.P)) {
            t.scaleX *= 1.01f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.O)) {
            t.scaleY *= 0.99f;
        }

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
            Matrix4x4 m = t.matrix();
            Vector3 position = new Vector3();
            m.getTranslation(position);
            Quaternion rotation = new Quaternion();
            m.getRotation(rotation);
            Vector3 scale = new Vector3();
            m.getScale(scale);
        }

        float[] mesh = {
                -200,300,Color.RED.toFloatBits(),0.5f,0.5f,
                -200,-300,Color.YELLOW.toFloatBits(),0.5f,0.5f,
                200,-300,Color.GREEN.toFloatBits(),0.5f,0.5f,

                200,-300,Color.BROWN.toFloatBits(),0.5f,0.5f,
                200,300,Color.MAGENTA.toFloatBits(),0.5f,0.5f,
                -200,300,Color.MAROON.toFloatBits(),0.5f,0.5f,
        };

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);
        renderer2D.begin(null);
        renderer2D.setTint(null);

        renderer2D.setTint(null);

        renderer2D.drawMeshFilled(mesh, null, 0,0,0,0,0,1,1);

        renderer2D.drawRectangleFilled(200, 300, 30, 10, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);


        renderer2D.end();



    }


    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float a = 0;

    @Override
    public void resize(int width, int height) { }
    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        return requiredAssets;
    }

}
