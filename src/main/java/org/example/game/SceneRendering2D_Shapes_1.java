package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_Shapes_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private float red = new Color(1,0,0,1).toFloatBits();
    private float blue = new Color(0,0,1,0.5f).toFloatBits();

    private ShaderProgram shaderYellow;

    public SceneRendering2D_Shapes_1() {
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
        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputMouse.isButtonPressed(InputMouse.Button.LEFT)) {
            ay++;
        }

        if (InputMouse.isButtonClicked(InputMouse.Button.RIGHT)) {

        }

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.S)) {

        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin(camera);
        renderer2D.setTint(blue);
        //renderer2D.drawCircleFilled(3, 3, 0, 8, 0, 0, 0, 1, 1);
        //renderer2D.drawCircleFilled(3, -3, 0, 8, 0, 0, 0, 1, 1);
        renderer2D.drawCircleBorder(1, 0.2f,60, 0,-3, ay,0,0,1, 1);
        //renderer2D.drawCircleFilled(3, -4, 0, 30,25, 0, 0, ay, 1, 1);

        renderer2D.drawCircleThin(1, 20, 0, 0, 0, ay, 0, 1, 1);
        renderer2D.setTint(red);
        renderer2D.drawRectangleThin(0,0, 2,0,2,2,0,2);
        renderer2D.drawCircleBorder(1, 0.2f, 90, 30, 0, 3,ay,0,45,1, 1);
        renderer2D.setTint(blue);
        renderer2D.drawRectangleThin(4,2, 0,0,0,0,ay,1,1);

        renderer2D.drawRectangleFilled(0-3,0-3, 2-3,0-3,2-3,2-3,0-3,2-3);

        renderer2D.drawRectangleFilled(4,3, 4,-2,0,ay,0,1,1);

        //renderer2D.drawCircleBorder(1, 0.2f,-3, 0, 6,ay,0,0,1, 1);

//        renderer2D.setTint(red);
//        renderer2D.setTint(blue);
//
        renderer2D.end();
    }

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
