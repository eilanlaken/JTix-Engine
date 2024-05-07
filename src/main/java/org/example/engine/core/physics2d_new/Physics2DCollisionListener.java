package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.MathVector2;

public interface Physics2DCollisionListener {

    default void beginContact(Physics2DBody a, Physics2DBody b) {

    }

    default void endContact(Physics2DBody a, Physics2DBody b) {

    }

    default void preSolve(Physics2DWorld.CollisionManifold manifold) {

    }

    default void solve(Physics2DWorld.CollisionManifold manifold) {
        Physics2DBody a = manifold.a;
        Physics2DBody b = manifold.b;

        MathVector2 vRel = new MathVector2(b.velocity).sub(a.velocity);
        float normalVelocity = vRel.dot(manifold.normal);

        // separate bodies
        float aMassInv = a.motionType == Physics2DBody.MotionType.STATIC ? 0 : a.massInv;
        float bMassInv = b.motionType == Physics2DBody.MotionType.STATIC ? 0 : b.massInv;
        final float percent = 0.2f;
        final float threshold = 0.05f;
        final float depth = manifold.depth;
        final MathVector2 normal = manifold.normal;
        final float pushback = Math.max(depth - threshold, 0.0f);
        MathVector2 correction = new MathVector2(normal).scl(pushback * percent).scl(1.0f / (aMassInv + bMassInv));
        a.shape.dx_dy(aMassInv * correction.x, aMassInv * correction.y);
        b.shape.dx_dy(-bMassInv * correction.x, -bMassInv * correction.y);

        if (normalVelocity > 0) return;

        // FIXME collision response
        float e = Math.min(a.restitution, b.restitution);

        // what if one of the masses are 0?
        float j = -(1 + e) * normalVelocity / (aMassInv + bMassInv);
        MathVector2 impulse = new MathVector2(normal).scl(j);
        MathVector2 deltaVelA = new MathVector2(impulse).scl(aMassInv);
        MathVector2 deltaVelB = new MathVector2(impulse).scl(bMassInv);
        a.velocity.sub(deltaVelA);
        b.velocity.add(deltaVelB);
    }

    default void postSolve() {

    }

}