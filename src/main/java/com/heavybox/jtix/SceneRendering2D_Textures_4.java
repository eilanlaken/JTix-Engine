package com.heavybox.jtix;

import com.heavybox.jtix.z_old_application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.z_old_input.Keyboard;
import com.heavybox.jtix.z_old_input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_ecs_old.ComponentTransform;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Textures_4 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;

    ComponentTransform t = new ComponentTransform();

    public SceneRendering2D_Textures_4() {
        renderer2DOld = new Renderer2D_old();

    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");
    }

    @Override
    public void refresh() {


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
        renderer2DOld.begin(null);
        renderer2DOld.setTint(null);
        renderer2DOld.drawTexture(yellowSquare, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);

        renderer2DOld.setTint(null);

        renderer2DOld.drawTexture(yellowSquare, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);
        renderer2DOld.setTint(Color.BLUE);
        //renderer2D.drawRectangleFilled(400,200, t.x,t.y,t.angleXDeg,t.angleYDeg,t.angleZDeg,t.scaleX,t.scaleY);


        renderer2DOld.end();



    }


    float u1,v1,u2 = 0.5f,v2 = 0.5f;
    float a = 0;

    @Override
    public void resize(int width, int height) { }
    @Override
    public void hide() {
        renderer2DOld.deleteAll();
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
