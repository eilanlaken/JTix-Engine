package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.shape.Shape2D;

// https://github.com/RandyGaul/ImpulseEngine/blob/master/Manifold.h
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-the-core-engine--gamedev-7493t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756t
// https://code.tutsplus.com/how-to-create-a-custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032t
public class Physics2DWorld {

    // todo: change everything to private or protected
    private static final short PHASE_PREPARATION = 0;
    private static final short PHASE_INTEGRATION = 1;
    private static final short PHASE_BROAD       = 2;
    private static final short PHASE_NARROW      = 3;
    private static final short PHASE_RESOLUTION  = 4;

    public CollectionsArray<Physics2DBody> allBodies      = new CollectionsArray<>(false, 500);
    public CollectionsArray<Physics2DBody> bodiesToAdd    = new CollectionsArray<>(false, 100);
    public CollectionsArray<Physics2DBody> bodiesToRemove = new CollectionsArray<>(false, 500);
    public short phase;

    // [0, 1], [2, 3], [4, 5], ... are collision candidates.
    public final CollectionsArray<Physics2DBody>                    collisionCandidates = new CollectionsArray<>(false, 400);
    public final CollectionsArray<Physics2DWorldCollision.Manifold> collisionManifolds  = new CollectionsArray<>(false, 200);

    public Physics2DWorld() {

    }

    public void update(final float delta) {
        this.phase = PHASE_PREPARATION;
        {
            allBodies.removeAll(bodiesToRemove, true);
            allBodies.addAll(bodiesToAdd);
            bodiesToAdd.clear();
            bodiesToRemove.clear();
            collisionCandidates.clear();
            collisionManifolds.clear();
        }

        this.phase = PHASE_INTEGRATION;
        {
            for (Physics2DBody body : allBodies) {
                if (!body.active) continue;
                if (body.type == Physics2DBody.Type.STATIC) continue;
                CollectionsArray<MathVector2> forces = body.forces;
                for (MathVector2 force : forces) {
                    body.velocity.add(body.massInv * delta * force.x, body.massInv * delta * force.y);
                }
                body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocity);
                body.shape.update();

                // TODO: update broad phase cells.
            }
        }

        this.phase = PHASE_BROAD;
        {
            // for now.
            collisionCandidates.addAll(allBodies);
        }

        this.phase = PHASE_NARROW;
        {
            // TODO: use enhanced loop below. (delete this).
            for (int i = 0; i < allBodies.size - 1; i++) {
                for (int j = i + 1; j < allBodies.size; j++) {
                    Physics2DBody a = allBodies.get(i);
                    Physics2DBody b = allBodies.get(j);
                }
            }
        }

        // resolution
        this.phase = PHASE_RESOLUTION;
        {

        }

    }

    // TODO: change to create using shapes.
    public Physics2DBody createBody(Shape2D shape, MathVector2 position, float angle, MathVector2 velocity) {
        Physics2DBody body = new Physics2DBody(shape, position, angle, velocity);
        this.bodiesToAdd.add(body);
        return body;
    }

    public void destroyBody(int handle) {

    }

    public void createJoint() {

    }

    public void destroyJoint() {

    }

    // TODO
    public void castRay() {

    }

}
