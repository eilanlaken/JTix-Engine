package org.example.game;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.*;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.example.engine.core.physics2d.Physics2DWorldCollisionManifold;
import org.lwjgl.opengl.GL11;

public class ScenePhysics2D_3 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2D circle;
    private Shape2D otherCircle;
    private Shape2D otherAABB;
    private Color staleTint = new Color(1,0,0,1);

    Physics2DWorld world = new Physics2DWorld();
    Physics2DBody body;
    private Shape2DPolygon contactIndicator = AlgorithmsPolygons.createPolygonCircleFilled(1, 10);

    public ScenePhysics2D_3() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        circle = new Shape2DCircle(1);
        otherCircle = new Shape2DCircle(1.5f,0,0);
        otherAABB = new Shape2DAABB(4, 2);

        camera = new Camera(640f/64,480f/64, 1);
        camera.update();

        body = world.createBody(circle, new Vector2(0,0), new Vector2(0.f, 0));
        world.createBody(otherCircle, new Vector2(3,1.5f), new Vector2(0.f, 0));
        world.createBody(otherAABB, new Vector2(-2, -2.5f), new Vector2(0.f, 0));

    }


    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);

        renderer2D.pushDebugShape(circle, null);
        renderer2D.pushDebugShape(otherCircle, staleTint);
        renderer2D.pushDebugShape(otherAABB, staleTint);

        renderer2D.end();



        world.update(GraphicsUtils.getDeltaTime());
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        body.setPosition(screen.x, screen.y);


        // render physics 2d debug:
        renderer2D.begin(camera);
        for (Physics2DWorldCollisionManifold manifold : world.collisionManifolds)
            renderManifold(manifold);
        renderer2D.end();
    }

    // TODO: refactor out into the physics debug renderer. For now, use to implement correct narrow phase.
    private void renderManifold(Physics2DWorldCollisionManifold manifold) {
        Vector2 penetration = new Vector2(manifold.normal).scl(manifold.depth);

        // calculate points scale
        final float pointPixelRadius = 6;
        float scaleX = camera.lens.getViewportWidth() * pointPixelRadius / GraphicsUtils.getWindowWidth();
        float scaleY = camera.lens.getViewportHeight() * pointPixelRadius / GraphicsUtils.getWindowHeight();

        // render contact points
        if (manifold.contactPoint1 != null)
            renderer2D.pushPolygon(contactIndicator, new Color(1,0,0,1), manifold.contactPoint1.x, manifold.contactPoint1.y, 0,0,0,scaleX,scaleY,null,null);
        if (manifold.contactPoint2 != null)
            renderer2D.pushPolygon(contactIndicator, new Color(1,0,0,1), manifold.contactPoint2.x, manifold.contactPoint2.y, 0,0,0,scaleX,scaleY,null,null);

        Vector2 contactsCenter = new Vector2();

        if (manifold.contactPoint1 != null && manifold.contactPoint2 != null) {
            contactsCenter.set(manifold.contactPoint1).add(manifold.contactPoint2).scl(0.5f);
        } else if (manifold.contactPoint1 != null) {
            contactsCenter.set(manifold.contactPoint1);
        } else if (manifold.contactPoint2 != null) {
            contactsCenter.set(manifold.contactPoint2);
        }

        Shape2DSegment segment = new Shape2DSegment(contactsCenter.x, contactsCenter.y, contactsCenter.x + penetration.x, contactsCenter.y + penetration.y);
        renderer2D.pushDebugShape(segment, new Color(1,0,1,1));
    }

    private void renderBounds(Shape2D shape2D) {
        float r = shape2D.getBoundingRadius();
        Shape2DCircle bounds = new Shape2DCircle(r, shape2D.x(), shape2D.y());
        renderer2D.pushDebugShape(bounds,new Color(1,1,0,1));
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


    public static class Cell {

        public static float CELL_SIZE = 10;
        Array<Shape2D> shapes = new Array<>();

    }

}
