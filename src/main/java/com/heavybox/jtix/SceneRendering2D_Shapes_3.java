package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.assets.AssetStore;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D_old;
import com.heavybox.jtix.graphics.ShaderProgram;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_3 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private ShaderProgram shaderYellow;

    public SceneRendering2D_Shapes_3() {
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
    protected void refresh() {
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        componentGraphicsCamera.lens.unProject(screen);

        if (Mouse.isButtonPressed(Mouse.Button.LEFT)) {
            ay++;
        }

        if (Mouse.isButtonClicked(Mouse.Button.RIGHT)) {

        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.S)) {

        }

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0f,0f,0f,1);

        renderer2DOld.begin(componentGraphicsCamera);
        renderer2DOld.setTint(blue);

        //renderer2D.drawCurveFilled(1f, new Vector2(-4,0), new Vector2(0,0), new Vector2(4,4));

        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += 1f;
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= 1f;

        renderer2DOld.setTint(new Color(1,0,0,0.2f));

        Vector2 last = new Vector2(1f,0);
        last.rotateDeg(dy);

        Vector2[] verts = new Vector2[] {new Vector2(-6,0), new Vector2(0,0), last};
        //Vector2[] verts = new Vector2[] {new Vector2(-6,0), new Vector2(0,0), last};


        // https://math.stackexchange.com/questions/15815/how-to-union-many-polygons-efficiently
        if (false) {

            for (int i = 0; i < verts.length - 1; i += 2) {
                Vector2 v_up = verts[i];
                Vector2 v_down = verts[i + 1];
                renderer2DOld.setTint(Color.RED);
                renderer2DOld.drawCircleFilled(0.05f, 10, v_up.x, v_up.y, 0,0,0,1,1);
                renderer2DOld.setTint(Color.BLUE);
                renderer2DOld.drawCircleFilled(0.05f, 10, v_down.x, v_down.y, 0,0,0,1,1);
            }

        } else {
            //renderer2D.drawCurveFilled_final(1f, 2, new Vector2(-3,0), new Vector2(0,0), new Vector2(-3,0));
        }

        renderer2DOld.end();
    }

    float dy = 0;

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

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", ShaderProgram.class);

        return requiredAssets;
    }

}
