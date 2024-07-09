package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_4 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float green = new Color(0,1,0,1f).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();
    private float white = new Color(1,1,1,0.5f).toFloatBits();
    private float yellow = new Color(1,1,0,0.3f).toFloatBits();

    private ShaderProgram shaderYellow;

    public SceneRendering2D_Shapes_4() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();
        shaderYellow = AssetStore.get("assets/shaders/graphics-2d-shader-yellow");

    }

    float ay = 0;
    @Override
    protected void refresh() {
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        camera.lens.unproject(screen);

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
        GL11.glClearColor(1f,1f,0f,1);

        renderer2D.begin(camera);
        renderer2D.setTint(blue);

        //renderer2D.drawCurveFilled(1f, new Vector2(-4,0), new Vector2(0,0), new Vector2(4,4));

        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += 1f;
        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= 1f;

        renderer2D.setTint(new Color(1,0,0,0.2f));

        Vector2 last = new Vector2(0.8f,0);
        last.rotateDeg(dy);

        // https://math.stackexchange.com/questions/15815/how-to-union-many-polygons-efficiently
        if (true) {
            Array<Vector2> vertices = renderer2D.drawCurveFilled_new(1f, 10, new Vector2(-3,0), new Vector2(0,0), last);

            for (int i = 0; i < vertices.size - 1; i += 2) {
                Vector2 v_up = vertices.get(i);
                Vector2 v_down = vertices.get(i + 1);
                renderer2D.setTint(Color.RED);
                renderer2D.drawCircleFilled(0.05f, 10, v_up.x, v_up.y, 0,0,0,1,1);
                renderer2D.setTint(Color.BLUE);
                renderer2D.drawCircleFilled(0.05f, 10, v_down.x, v_down.y, 0,0,0,1,1);
            }

            for (int i = 0; i < Renderer2D.dots.size; i ++) {
                Vector2 dot = Renderer2D.dots.get(i);
                renderer2D.setTint(Color.BLACK);
                //renderer2D.drawCircleFilled(0.1f, 10, dot.x, dot.y, 0,0,0,1,1);
            }

            for (int i = 0; i < Renderer2D.ends.size; i++) {
                Vector2 end = Renderer2D.ends.get(i);
                renderer2D.setTint(new Color(0,1,0, 1f / i));
                //renderer2D.drawCircleFilled(0.1f, 10, end.x, end.y, 0,0,0,1,1);
            }

        } else {
            renderer2D.drawCurveFilled_final(1f, 2, new Vector2(-3,0), new Vector2(0,0), new Vector2(-3,0));
        }

        renderer2D.end();
    }

    float dy = 0;

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

        requiredAssets.put("assets/shaders/graphics-2d-shader-yellow", ShaderProgram.class);

        return requiredAssets;
    }

}
