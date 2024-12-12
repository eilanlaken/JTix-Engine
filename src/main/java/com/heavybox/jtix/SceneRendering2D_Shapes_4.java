package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_4 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.4f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private Shader shaderYellow;

    public SceneRendering2D_Shapes_4() {
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
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        componentGraphicsCamera.lens.unProject(screen);

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

        renderer2DOld.begin(componentGraphicsCamera);
        renderer2DOld.setTint(blue);



        //renderer2D.drawPolygonThin(new float[]{0,0,   1,0,  1,1,   0,1}, true, 0,0,0,0,0,1,1);
//        renderer2D.drawPolygonThin(new float[]{0,0, 0.4f,0,  1,0, 1,0,   0,1}, true, 0,0,0,0,0,1,1);
//        renderer2D.drawPolygonThin(new float[]{1.0f, 0.0f, 0.4045085f, 0.29389262f, 0.30901697f, 0.95105654f, -0.1545085f, 0.47552827f, -0.80901706f, 0.58778524f, -0.5f, 6.123234E-17f, -0.80901706f, -0.58778524f, -0.1545085f, -0.47552827f, 0.30901697f, -0.95105654f, 0.4045085f, -0.29389262f}, false, 2,2,0,0,dy*10,1,1);
//        renderer2D.drawPolygonFilled(new float[]{1.0f, 0.0f, 0.4045085f, 0.29389262f, 0.30901697f, 0.95105654f, -0.1545085f, 0.47552827f, -0.80901706f, 0.58778524f, -0.5f, 6.123234E-17f, -0.80901706f, -0.58778524f, -0.1545085f, -0.47552827f, 0.30901697f, -0.95105654f, 0.4045085f, -0.29389262f}, 2,2,0,0,dy*10,1,1);
//        renderer2D.drawPolygonFilled(new float[]{1.0f, 0.0f, 0.4045085f, 0.29389262f, 0.30901697f, 0.95105654f, -0.1545085f, 0.47552827f, -0.80901706f, 0.58778524f, -0.5f, 6.123234E-17f, -0.80901706f, -0.58778524f, -0.1545085f, -0.47552827f, 0.30901697f, -0.95105654f, 0.4045085f, -0.29389262f}, -6,2,0,0,dy*10,1,1);

        //renderer2D.drawCurveFilled(1.2f, 10, new Vector2(-3,0), new Vector2(0,0), new Vector2(3,0), new Vector2(6,2));
        //Vector2 last = new Vector2(0.6f,0); // TODO: when stroke value / 2 is greater than the step, we get problems.
        Vector2 last = new Vector2(1f,0);
        last.rotateDeg(dy * 50);
        Vector2 last2 = new Vector2(3,0);
        //last2.rotateAroundDeg(last, dy * 50);
        //renderer2D.drawCurveFilled_2(1.2f, 10, new Vector2(-3,0), new Vector2(0,0), last);

        Vector2 first = new Vector2(-0.3f,0);
        first.rotateDeg(dy * 100);

        renderer2DOld.end();
    }

    float dx = 0;
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

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", Shader.class);

        return requiredAssets;
    }

}
