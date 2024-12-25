package com.heavybox.jtix;

import com.heavybox.jtix.z_old_application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.z_ecs_old_1.ComponentTransform;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.z_graphics_old.Renderer;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.z_old_input.Keyboard;
import com.heavybox.jtix.z_old_input.Mouse;
import com.heavybox.jtix.math.Quaternion;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Transform_2 extends ApplicationScreen {

    private Renderer renderer2D;
    private ComponentGraphicsCamera componentGraphicsCamera;

    Texture yellowSquare;


    ComponentTransform t = new ComponentTransform();
    ComponentTransform t2 = new ComponentTransform();
    ComponentTransform t3 = new ComponentTransform();

    public SceneRendering2D_Transform_2() {
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
    public void refresh() {


        Vector2[] vs = new Vector2[3];
        vs[0] = new Vector2(-2,-2);
        vs[1] = new Vector2(0,0);
        vs[2] = new Vector2(2,0);
        for (Vector2 v : vs) {
            v.rotateRad(dy);
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {
            dy += Graphics.getDeltaTime();
            dx += Graphics.getDeltaTime();
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) {
            dx -= Graphics.getDeltaTime();
            dy -= Graphics.getDeltaTime();
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


        Vector3 position = new Vector3();
        Quaternion rotation = new Quaternion();
        Vector3 scale = new Vector3();
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            t.getPosition(position);
            t.getRotation(rotation);
            t.getScale(scale);

            System.out.println(rotation.getPitchDeg());
            System.out.println(rotation.getYawDeg());
            System.out.println(rotation.getRollDeg());

            t2.idt();
            t2.scale(scale);
            t2.rotateLocalAxis(rotation);
            t2.translateGlobalAxisXYZ(position);

            t3.setToPositionEulerScaling(
                    position.x, position.y, position.z,
                    rotation.getPitchDeg(), rotation.getYawDeg(), rotation.getRollDeg(),
                    scale.x, scale.y, scale.z);
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
            //System.out.println(baseR + dr);
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;
        }

        renderer2D.begin(null);
        //renderer2D.setTint(red);
        //renderer2D.drawCircleFilled(1f, 1400, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, 1498, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, baseR + dr, 0, 0, 0,0,0,1,1);
        //renderer2DOld.drawCircleFilled(1f, baseR + dr, x, y, 0,0,0,1,1);
        renderer2D.drawTexture(yellowSquare, t);
        renderer2D.setTint(Color.RED);
        renderer2D.drawCircleFilled(50f, baseR + dr, t);
        renderer2D.setTint(Color.BLUE);
        renderer2D.drawCircleFilled(50f, baseR + dr, t2);

        renderer2D.setTint(Color.MAGENTA);
        renderer2D.drawCircleFilled(50f, baseR + dr, t3);


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
