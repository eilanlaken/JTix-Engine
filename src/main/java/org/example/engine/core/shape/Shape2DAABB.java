package org.example.engine.core.shape;

import org.example.engine.core.math.MathVector2;

// AABB = axis aligned bonding box
public class Shape2DAABB extends Shape2D {

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    private final MathVector2 localMin;
    private final MathVector2 localMax;
    public MathVector2 worldMin;
    public MathVector2 worldMax;

    private MathVector2 tmp = new MathVector2();

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        this.localMin = new MathVector2(x1, y1);
        this.localMax = new MathVector2(x2, y2);
        this.worldMin = new MathVector2(localMin);
        this.worldMax = new MathVector2(localMax);
        this.unscaledArea = Math.abs(x2 - x1) * Math.abs(y2 - y1);
        float centerX = (localMin.x + localMax.x) * 0.5f;
        float centerY = (localMin.y + localMax.y) * 0.5f;
        float halfDiagonal = MathVector2.dst(localMin, localMax) * 0.5f;
        unscaledBoundingRadius = MathVector2.len(centerX, centerY) + halfDiagonal;
    }

    public Shape2DAABB(float width, float height) {
        this(-width * 0.5f, -height * 0.5f, width * 0.5f, height * 0.5f);
    }

    public MathVector2 getWorldCenter() {
        return tmp.set(worldMin).add(worldMax).scl(0.5f);
    }

    @Override
    public boolean contains(float x, float y) {
        return x > worldMin.x && x < worldMax.x && y > worldMin.y && y < worldMax.y;
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    @Override
    public void updateWorldCoordinates() {
        if (angle != 0.0f) throw new IllegalStateException("Cannot rotate an AABB: must remain aligned to axis. angle must remain 0. Current value: angle = " + angle);
        this.worldMin.set(localMin).scl(scaleX, scaleY).add(x, y);
        this.worldMax.set(localMax).scl(scaleX, scaleY).add(x, y);
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + worldMin + ", max: " + worldMax + ">";
    }

}