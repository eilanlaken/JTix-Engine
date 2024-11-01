package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.z_old_assets.AssetStore;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D_old;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.ecs.ComponentCamera2D;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_1 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentCamera2D componentGraphicsCamera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private Shader shaderYellow;

    public SceneRendering2D_Shapes_1() {
        renderer2DOld = new Renderer2D_old();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentCamera2D(640f/32,480f/32);
        componentGraphicsCamera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

    }

    float ay = 0;
    @Override
    public void refresh() {
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
        GL11.glClearColor(1f,1f,0,1);

        renderer2DOld.begin(componentGraphicsCamera);
        renderer2DOld.setTint(blue);
//        renderer2D.drawCircleFilled(3, 3, 0, 8, 0, 0, 0, 1, 1);
//        renderer2D.drawCircleFilled(3, -3, 0, 8, 0, 0, 0, 1, 1);
//        renderer2D.drawCircleBorder(1, 0.2f,60, 0,-3, ay,0,0,1, 1);
//        //renderer2D.drawCircleFilled(3, -4, 0, 30,25, 0, 0, ay, 1, 1);
//
//        renderer2D.drawCircleThin(1, 20, 0, 0, 0, ay, 0, 1, 1);
//        renderer2D.setTint(red);
//        renderer2D.drawRectangleThin(5,5, 1f, 5,4,-2,0,ay,0,1,1);
//        renderer2D.drawRectangleThin(0,0, 2,0,2,2,0,2);
//        renderer2D.drawCircleBorder(1, 0.2f, 90, 30, 0, 3,ay,0,45,1, 1);
        //renderer2D.setTint(yellow);


        //renderer2D.drawCurveThin(new Vector2(0,0), new Vector2(4,4));
        //renderer2D.drawLineFilled(-3,0,3,0, 8f, 18);
        //renderer2D.drawLineFilled(-3,0,3,3, 0.2f, 18);
        //renderer2D.drawCurveFilled(0.1f,new Vector2(-3,0), new Vector2(0,0), new Vector2(3,0), new Vector2(3,-3), new Vector2(0,-1));


//        renderer2D.setTint(red);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.0f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(green);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.5f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(blue);
//        renderer2D.drawCircleFilled(0.1f, 0.21827313f,0.21827313f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(Color.WHITE);
//        renderer2D.drawCircleFilled(0.1f, -0.13528025f,0.5718265f, 11, 0, 0, 0, 1, 1);
//        renderer2D.setTint(Color.BLACK);
//        renderer2D.drawCircleFilled(0.1f, -0.30868483f,0.74523115f, 11, 0, 0, 0, 1, 1);


        //renderer2D.drawCurveFilled(1f, new Vector2(-4,0), new Vector2(0,0), new Vector2(4,4));

        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += Graphics.getDeltaTime();
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= Graphics.getDeltaTime();


        if (false) {
            // ~
            //renderer2D.drawCurveFilled_1(0.6f, 3, new Vector2(-4,4 + dy), new Vector2(0,0), new Vector2(4,4 + dy));

            // V
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-2,4), new Vector2(0,0), new Vector2(2,4));

            // happy
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,4), new Vector2(0,0), new Vector2(4,4));

            // sad
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,0), new Vector2(4,-4));

            // Nike
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,-4), new Vector2(4,0));

            // ---
            //renderer2D.drawCurveFilled(0.6f, 3, new Vector2(-4,-4), new Vector2(0,0), new Vector2(4,4));
        }
        else {
            //renderer2D.drawCurveFilled(1f, new Vector2(-4, 0), new Vector2(0, 0), new Vector2(4, -4 + dy * 2));

            // V
            renderer2DOld.setTint(red);
//            renderer2D.drawCircleFilled(0.1f, 10, -1.7316718f, 4.134164f, 0, 0, 0, 1,1);
//            renderer2D.drawCircleFilled(0.1f, 10, -1.9820062f, 4.29946f, 0, 0, 0, 1,1);
//            renderer2D.drawCircleFilled(0.1f, 10, -2.250364f, 4.165281f, 0, 0, 0, 1,1);
//            renderer2D.drawCircleFilled(0.1f, 10, -2.2683282f, 3.865836f, 0, 0, 0, 1,1);

            renderer2DOld.setTint(Color.WHITE);
            //renderer2D.drawCircleFilled(0.1f, 10, -2.0f, 4.0f, 0, 0, 0, 1,1);



            //renderer2D.drawCurveFilled(0.6f, new Vector2(-2,4), new Vector2(0,0), new Vector2(2,4));
            renderer2DOld.setTint(new Color(0,0,0,0.2f));
            //renderer2D.drawCurveFilled(0.6f, 3,  new Vector2(-2,4 + dy), new Vector2(0,0), new Vector2(2,4 + dy));
            //renderer2D.drawCurveFilled(0.6f, 12,  new Vector2(-2,4 + dy - 3), new Vector2(0,0 - 3), new Vector2(2,4 + dy - 3));

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

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", Shader.class);

        return requiredAssets;
    }

}
