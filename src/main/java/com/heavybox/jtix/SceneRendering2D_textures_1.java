package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_textures_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Texture texture;

    private TexturePack pack;

    public SceneRendering2D_textures_1() {
        renderer2D = new Renderer2D();

    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        texture = AssetStore.get("assets/textures/yellowSquare.png");
    }

    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1f,0f,0,1);

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {

        }
        if (Keyboard.isKeyPressed(Keyboard.Key.F)) {
            u2 += 0.001f;
            v2 += 0.001f;
            a += 0.1f;
        }

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {

        }

        //renderer2D.begin(camera);
        renderer2D.begin(null);
        //renderer2D.drawTexture(texture, 0,0,0,0,0,1,1);
        renderer2D.drawTexture(texture,u1,v1,u2,v2,0,-200,0,0,a,1,1);
        renderer2D.drawTexture(texture,0,0,1f,1f,0,200,0,0,0,1,1);
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

        requiredAssets.put("assets/atlases/marks.yml", TexturePack.class);


        return requiredAssets;
    }

}