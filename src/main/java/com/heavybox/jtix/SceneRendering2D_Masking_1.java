package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.application.ApplicationUtils;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.z_graphics_old.Renderer;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.z_old_input.Keyboard;
import com.heavybox.jtix.z_old_input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_ecs_old.ComponentTransform;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Masking_1 extends ApplicationScreen {

    private Renderer renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;

    ComponentTransform t = new ComponentTransform();

    ComponentTransform[] transforms = new ComponentTransform[10];

    public SceneRendering2D_Masking_1() {
        renderer2D = new Renderer();
        System.out.println(Graphics.getContentScaleX());
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");
        for (int i = 0; i < 10; i++) {
            transforms[i] = new ComponentTransform();
            transforms[i].x = MathUtils.randomUniformFloat(-Graphics.getWindowWidth() / 2.0f, Graphics.getWindowWidth() / 2.0f);
            transforms[i].y = MathUtils.randomUniformFloat(-Graphics.getWindowHeight() / 2.0f, Graphics.getWindowHeight() / 2.0f);
        }
    }

    @Override
    public void refresh() {

        if (Keyboard.isKeyJustPressed(Keyboard.Key.I)) {
            ApplicationUtils.windowSetIcon("assets/textures/icon-128.png");
            ApplicationUtils.windowSetTitle("Planes IO");
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            t.x -= 10f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) {
            t.x += 10f;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            t.y += 10f;
            step++;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            t.y -= 10f;
            step--;
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
        renderer2D.drawCurveFilled(-400,400,step,40,20, x -> 400 * MathUtils.sinDeg(x));

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
