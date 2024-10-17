package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Renderer2D_2;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.z_old_assets.AssetStore;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Renderer2D_2 extends ApplicationScreen {

    private Renderer2D_2 renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;
    float x, y;

    public SceneRendering2D_Renderer2D_2() {
        renderer2D = new Renderer2D_2();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");

    }

    @Override
    protected void refresh() {


        if (Keyboard.isKeyPressed(Keyboard.Key.X)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Z)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.UP)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.DOWN)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {

        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            //System.out.println(baseR + dr);
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;
        }

        renderer2D.begin(null);
        //renderer2D.drawTexture(yellowSquare,0,0,0,1,1);
        renderer2D.drawTexture_new(yellowSquare,0,0,0,1,1);
        System.out.println("hi");
        renderer2D.end();
    }

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
