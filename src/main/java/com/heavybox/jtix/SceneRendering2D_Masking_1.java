package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.application.ApplicationUtils;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.ecs.ComponentTransform;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Masking_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    Texture yellowSquare;

    ComponentTransform t = new ComponentTransform();

    ComponentTransform[] transforms = new ComponentTransform[10];

    public SceneRendering2D_Masking_1() {
        renderer2D = new Renderer2D();
        System.out.println(GraphicsUtils.getContentScaleX());
    }

    @Override
    public void show() {
        camera = new Camera(GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight(), 1);
        camera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");
        for (int i = 0; i < 10; i++) {
            transforms[i] = new ComponentTransform();
            transforms[i].x = MathUtils.randomUniformFloat(-GraphicsUtils.getWindowWidth() / 2.0f, GraphicsUtils.getWindowWidth() / 2.0f);
            transforms[i].y = MathUtils.randomUniformFloat(-GraphicsUtils.getWindowHeight() / 2.0f, GraphicsUtils.getWindowHeight() / 2.0f);

        }
    }

    @Override
    protected void refresh() {

        if (Keyboard.isKeyJustPressed(Keyboard.Key.I)) {
            ApplicationUtils.windowSetIcon("assets/textures/icon-128.png");
            ApplicationUtils.windowSetTitle("Planes IO");
            System.out.println(GraphicsUtils.getWindowPositionX());
            System.out.println(GraphicsUtils.getWindowPositionY());
        }

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

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);
        renderer2D.begin(null);
        renderer2D.setTint(null);
        //renderer2D.drawTexture(yellowSquare, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);

        renderer2D.setTint(null);

        for (int i = 0; i < 10; i++) {
            renderer2D.drawTexture(yellowSquare, transforms[i].x, transforms[i].y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);
        }
        renderer2D.drawCurveFilled(40,20,new Vector2(-400,0), new Vector2(-200,200), new Vector2(0,0),
                new Vector2(200,-200), new Vector2(400,-400));

        renderer2D.setTint(Color.BLUE);
        renderer2D.drawCurveFilled(-400,400,40,40,20, x -> 400 * MathUtils.sinDeg(x));

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
        requiredAssets.put("assets/textures/yellowSquare.jpg", Texture.class);
        return requiredAssets;
    }

}
