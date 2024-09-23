package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.ecs.ComponentGraphicsCamera;
import com.heavybox.jtix.ecs.ComponentTransform;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Fonts_1 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;

    ComponentTransform t = new ComponentTransform();

    Texture fontMap;

    public SceneRendering2D_Fonts_1() {
        renderer2DOld = new Renderer2D_old();

    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();

        fontMap = AssetStore.get("assets/fonts/fontBitmap.png");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("assets/fonts/fontBitmap"))) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void refresh() {

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }




        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);
        renderer2DOld.begin(null);
        renderer2DOld.setTint(null);

        renderer2DOld.drawTexture(fontMap, 0,0,0,0,0,1,1);
        renderer2DOld.end();



    }



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
        requiredAssets.put("assets/fonts/fontBitmap.png", Texture.class);
        return requiredAssets;
    }

}
