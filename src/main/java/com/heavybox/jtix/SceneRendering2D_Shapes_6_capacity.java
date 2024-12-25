package com.heavybox.jtix;

import com.heavybox.jtix.z_old_application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.z_old_input.Keyboard;
import com.heavybox.jtix.z_old_input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_6_capacity extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.4f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private Shader shaderYellow;

    public SceneRendering2D_Shapes_6_capacity() {
        renderer2DOld = new Renderer2D_old();

    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

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

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {
            ay++;
        }


        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0,1);

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            dr++;
        }
        if (Keyboard.isKeyPressed(Keyboard.Key.F)) {
            dr--;
        }

        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        if (Mouse.isButtonClicked(Mouse.Button.LEFT)) {
            System.out.println(baseR + dr);
            componentGraphicsCamera.lens.unProject(screen);
            x = screen.x;
            y = screen.y;
        }

        renderer2DOld.begin(componentGraphicsCamera);
        renderer2DOld.setTint(red);
        //renderer2D.drawCircleFilled(1f, 1400, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, 1498, 0, 0, 0,0,0,1,1);
        //renderer2D.drawCircleFilled(1f, baseR + dr, 0, 0, 0,0,0,1,1);
        renderer2DOld.drawCircleFilled(1f, baseR + dr, x, y, 0,0,0,1,1);
        renderer2DOld.drawCircleFilled(1f, baseR + dr, -x, y, 0,0,0,1,1);
        renderer2DOld.drawCircleFilled(1f, baseR + dr, -x, -y, 0,0,0,1,1);
        renderer2DOld.drawCircleFilled(1f, baseR + dr, x, -y, 0,0,0,1,1);

        //System.out.println(baseR + dr);

        //renderer2D.drawCircleFilled(1f, 1498, -2, 0, 0,0,0,1,1);
        renderer2DOld.end();
    }

    float dx = 0;
    float dy = 0;
    int dr = 0;
    int baseR = 274;
    float x, y;

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

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", Shader.class);

        return requiredAssets;
    }

}
