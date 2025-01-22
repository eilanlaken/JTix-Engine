package com.heavybox.jtix;

import com.heavybox.jtix.z_deprecated.z_old_application.ApplicationScreen;
import com.heavybox.jtix.z_deprecated.z_old_assets.AssetStore;
import com.heavybox.jtix.z_deprecated.z_ecs_old_1.ComponentTransform;
import com.heavybox.jtix.ecs.*;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.z_deprecated.z_graphics_old.Renderer;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.z_deprecated.z_old_input.Keyboard;
import com.heavybox.jtix.z_deprecated.z_old_input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_deprecated.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.lang.System;
import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_ECS_1 extends ApplicationScreen {

    private Renderer renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;


    ComponentTransform t = new ComponentTransform();

    private EntityContainer container;
    Entity2D child5;

    public SceneRendering2D_ECS_1() {
        container = new EntityContainer() {
            @Override
            public void prepare() {

            }

            @Override
            public void start() {

            }
        };
        renderer2D = new Renderer();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        yellowSquare = AssetStore.get("assets/textures/yellowSquare.jpg");

        Entity2D parent = ECS.createDebugEntity();
        for (int i = 0; i < 10; i++) {
            Entity2D e = ECS.createDebugEntity();
            parent.addChild(e,false);
            if (i == 4) child5 = e;
        }

        Entity2D x = ECS.createDebugEntity();
        child5.addChild(x, false);



        container.createEntity(parent);

    }

    @Override
    public void refresh() {

        container.update();

        if (Keyboard.isKeyJustPressed(Keyboard.Key.U)) {
            System.out.println(container);
        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.N)) {
            container.destroyEntity(child5);
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

        if (Keyboard.isKeyPressed(Keyboard.Key.UP)) {
            t.translateGlobalAxisY(10);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            t.translateGlobalAxisY(-10);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            t.translateGlobalAxisX(-10);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            t.translateGlobalAxisX(10);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            t.scale(1.01f, 1, 1);
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
        renderer2D.drawTexture(yellowSquare, t);
        renderer2D.setTint(Color.BLUE);
        renderer2D.drawCircleFilled(50f, baseR + dr, t);
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
