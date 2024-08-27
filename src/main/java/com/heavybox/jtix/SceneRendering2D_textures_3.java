package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_textures_3 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private TexturePack pack;

    TexturePack.Region region_red;
    TexturePack.Region region_green;
    TexturePack.Region region_blue;

    public SceneRendering2D_textures_3() {
        renderer2D = new Renderer2D();

    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        pack = AssetStore.get("assets/atlases/spots.yml");
        region_red = pack.getRegion("assets/textures/red30x30.png");
        region_green = pack.getRegion("assets/textures/green25x25.png");
        region_blue = pack.getRegion("assets/textures/blue100x100.png");
    }

    @Override
    protected void refresh() {



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

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);
        renderer2D.begin(null);
        renderer2D.drawTextureRegion(region_blue, 0,0,0,0,0,1,1);
        renderer2D.drawTextureRegion(region_red, 0,0,0,0,0,1,1);
        renderer2D.drawTextureRegion(region_green, 0,0,0,0,0,1,1);
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


        requiredAssets.put("assets/atlases/spots.yml", TexturePack.class);


        return requiredAssets;
    }

}
