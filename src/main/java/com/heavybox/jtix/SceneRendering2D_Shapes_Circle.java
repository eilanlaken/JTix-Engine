package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.z_ecs_old_1.ComponentTransform;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.GraphicsUtils;
import com.heavybox.jtix.graphics.Renderer;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_Circle extends ApplicationScreen {

    private Renderer renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();

    Texture yellowSquare;


    ComponentTransform t = new ComponentTransform();

    public SceneRendering2D_Shapes_Circle() {
        renderer2D = new Renderer();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");
    }

    float ay = 0;
    @Override
    protected void refresh() {


        Vector2[] vs = new Vector2[3];
        vs[0] = new Vector2(-2,-2);
        vs[1] = new Vector2(0,0);
        vs[2] = new Vector2(2,0);
        for (Vector2 v : vs) {
            v.rotateRad(dy);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            dy += GraphicsUtils.getDeltaTime();
            dx += GraphicsUtils.getDeltaTime();
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            dx -= GraphicsUtils.getDeltaTime();
            dy -= GraphicsUtils.getDeltaTime();
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.X)) {
            t.rotateLocalAxis(1,0,0,1);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
            t.rotateLocalAxis(0,1,0,1);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.Z)) {
            t.rotateLocalAxis(0,0,1,1);
        }

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {
            ay++;
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            dr++;
            System.out.println(baseR + dr);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.F)) {
            dr--;
            System.out.println(baseR + dr);
        }

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            System.out.println(baseR + dr);
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;
        }

        renderer2D.begin(componentGraphicsCamera.lens.combined);
        //renderer2D.setTint(red);
        //renderer2D.drawCircleFilled(1f, 1400, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, 1498, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, baseR + dr, 0, 0, 0,0,0,1,1);
        //renderer2DOld.drawCircleFilled(1f, baseR + dr, x, y, 0,0,0,1,1);
        renderer2D.drawTexture(yellowSquare, t);
        renderer2D.drawCircleFilled(1, baseR + dr, t);


        //System.out.println(baseR + dr);

        //renderer2D.drawCircleFilled(1f, 1498, -2, 0, 0,0,0,1,1);
        renderer2D.end();
    }

    float dx = 0;
    float dy = 0;
    int dr = 0;
    int baseR = 5000;
    float x, y;

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
