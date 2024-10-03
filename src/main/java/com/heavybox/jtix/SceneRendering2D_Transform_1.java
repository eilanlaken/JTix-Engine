package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.application.ApplicationUtils;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.ecs.ComponentTransform_old_2;
import com.heavybox.jtix.ecs.ComponentTransform;
import com.heavybox.jtix.graphics.GraphicsUtils;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.ecs.ComponentTransform_old_1;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Transform_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;

    ComponentTransform_old_1 t1 = new ComponentTransform_old_1();
    ComponentTransform_old_2 t2 = new ComponentTransform_old_2();
    ComponentTransform t3 = new ComponentTransform();

    public SceneRendering2D_Transform_1() {
        renderer2D = new Renderer2D();
        System.out.println(GraphicsUtils.getContentScaleX());
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight(), 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");
    }

    @Override
    protected void refresh() {

        if (Keyboard.isKeyJustPressed(Keyboard.Key.I)) {
            ApplicationUtils.windowSetIcon("assets/textures/icon-128.png");
            ApplicationUtils.windowSetTitle("Planes IO");
            System.out.println(GraphicsUtils.getWindowPositionX());
            System.out.println(GraphicsUtils.getWindowPositionY());
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            t1.x -= 10f;
            t3.translateGlobalAxisXYZ(10,0,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            t1.x += 10f;
            t3.translateGlobalAxisXYZ(-10,0,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.UP)) {
            t1.y += 10f;
            t3.translateGlobalAxisXYZ(0,-10,0);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            t1.y -= 10f;
            t3.translateGlobalAxisXYZ(0,10,0);
        }


        if (Keyboard.isKeyPressed(Keyboard.Key.X)) {
            t1.degX += 1f;
            t2.matrix.rotateDeg(Vector3.X_UNIT, 1f);
            t3.rotateDeg(Vector3.X_UNIT, 1f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
            t1.degY += 1f;
            t2.matrix.rotateDeg(Vector3.Y_UNIT, 1f);
            t3.rotateDeg(Vector3.Y_UNIT, 1f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Z)) {
            t1.degZ += 1f;
            t2.matrix.rotateDeg(Vector3.Z_UNIT, 1f);
            t3.rotateDeg(Vector3.Z_UNIT, 1f);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.P)) {
            t1.sclX *= 1.01f;
            t2.matrix.scale(1.01f, 1, 1);
            t3.scale(1.01f, 1, 1);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.O)) {
            t1.sclY *= 0.99f;
            t3.scale(1, 0.99f, 1);
        }


        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.J)) {
            t3.rotateGlobalAxisZ(1);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);
        renderer2D.begin(null);
        renderer2D.setTint(null);
        //renderer2D.drawTexture(yellowSquare, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);

        renderer2D.setTint(null);

        //renderer2D.drawTexture(yellowSquare, t.x, t.y, t.degX, t.degY, t.degZ, t.sclX, t.sclY);
        renderer2D.drawTexture(yellowSquare, t1.matrix());
        //renderer2D.drawTexture(yellowSquare, t2.matrix);
        renderer2D.drawTexture(yellowSquare, t3);


        renderer2D.end();



    }


    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float a = 0;
    float step = 20;

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
        requiredAssets.put("assets/textures/yellowSquare.jpg", Texture.class);
        return requiredAssets;
    }

}
