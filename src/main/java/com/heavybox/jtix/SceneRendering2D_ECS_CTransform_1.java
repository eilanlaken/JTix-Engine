package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.assets.AssetUtils;
import com.heavybox.jtix.ecs.ComponentTransform;
import com.heavybox.jtix.ecs.ComponentTransform_$;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_ECS_CTransform_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private TexturePack pack;

    TexturePack.Region region_red;
    TexturePack.Region region_green;
    TexturePack.Region region_blue;

    Texture yellowSquare;

    ComponentTransform t = new ComponentTransform();

    public SceneRendering2D_ECS_CTransform_1() {
        renderer2D = new Renderer2D();

    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.png");
        pack = AssetStore.get("assets/atlases/spots.yml");
        region_red = pack.getRegion("assets/textures/red30x30.png");
        region_green = pack.getRegion("assets/textures/green25x25.png");
        region_blue = pack.getRegion("assets/textures/blue100x100.png");
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
            System.out.println("========================");
            System.out.println("matrix:");
            Vector3 position = new Vector3();
            m.getTranslation(position);
            Quaternion rotation = new Quaternion();
            m.getRotation(rotation);
            Vector3 scale = new Vector3();
            m.getScale(scale);
            System.out.println("from matrix:");
            System.out.println(position);
            System.out.println("rotation: x: " + rotation.getPitchDeg() + ", y: " + rotation.getYawDeg() + ", z: " + rotation.getRollDeg());
            System.out.println(scale);

            System.out.println("from vals:");
            System.out.println(t.x + ", " + t.y + ", " + t.z);
            System.out.println(t.angleXDeg + ", " + t.angleYDeg + ", " + t.angleZDeg);
            System.out.println(t.scaleX + ", " + t.scaleY + ", " + t.scaleZ);
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
        renderer2D.drawTexture(yellowSquare, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);

        renderer2D.setTint(null);
        renderer2D.drawTextureRegion(region_blue, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,2*t.scaleX,2*t.scaleY);
        renderer2D.drawTextureRegion(region_red, 0,0,0,0,0,1,1);
        renderer2D.drawTextureRegion(region_green, 0,0,0,0,0,1,1);

        renderer2D.drawMeshFilled(mesh, null, 0,0,0,0,0,1,1);

        renderer2D.drawTexture(yellowSquare, 20, 10, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);
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
        requiredAssets.put("assets/textures/yellowSquare.png", Texture.class);
        requiredAssets.put("assets/atlases/spots.yml", TexturePack.class);
        return requiredAssets;
    }

}