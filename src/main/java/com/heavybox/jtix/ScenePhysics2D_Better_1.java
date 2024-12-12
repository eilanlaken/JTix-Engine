package com.heavybox.jtix;

import com.heavybox.jtix.application.ApplicationScreen;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.z_graphics_old.Renderer2D_old;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.physics2d.Body2D;
import com.heavybox.jtix.physics2d.World2D;
import com.heavybox.jtix.z_ecs_old.ComponentGraphicsCamera;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_Better_1 extends ApplicationScreen {

    private Renderer2D_old renderer2DOld;
    private ComponentGraphicsCamera componentGraphicsCamera;
    private World2D world = new World2D();

    private Body2D body_a;
    private Body2D body_b;

    public ScenePhysics2D_Better_1() {
        renderer2DOld = new Renderer2D_old();
    }

    @Override
    public void show() {
        componentGraphicsCamera = new ComponentGraphicsCamera(640f/32,480f/32, 1);
        componentGraphicsCamera.update();

        world.createBodyRectangle(null, Body2D.MotionType.STATIC,
                -2.5f, 4,-30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Body2D.MotionType.STATIC,
                2.5f, 0f,30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Body2D.MotionType.STATIC,
                0, -5,0,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                10, 0.5f, 0, 0, 0);

        world.createForceField((body, force) -> { force.set(0, -9.8f / body.invM); });
    }


    @Override
    public void refresh() {
        world.update(Graphics.getDeltaTime());
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        componentGraphicsCamera.lens.unProject(screen);
        world.castRay((intersections) -> {

        }, 0,0, screen.x, screen.y, Float.POSITIVE_INFINITY);

        if (Keyboard.isKeyJustPressed(Keyboard.Key.A)) {
            body_a = world.createBodyCircle(null, Body2D.MotionType.NEWTONIAN,
                    screen.x, screen.y, 0,
                    0f, 0f, 0,
                    1, 1, 1,0.2f, false, 1,
                    0.5f);
        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.S)) {
            body_b = world.createBodyRectangle(null, Body2D.MotionType.NEWTONIAN,
                    screen.x, screen.y, -30,
                    0f, 0f, 0,
                    1, 1, 1, 0.2f, false, 1,
                    1, 1f, 0, 0, 0);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2DOld.begin(componentGraphicsCamera);
        world.render(renderer2DOld);
        renderer2DOld.end();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2DOld.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

}
