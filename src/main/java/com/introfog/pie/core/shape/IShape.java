package com.introfog.pie.core.shape;

import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.Mat22;
import com.introfog.pie.core.math.Vector2f;

public abstract class IShape {
    public enum Type {
        circle, polygon
    }

    public Type type;
    public AABB aabb;
    public Body body;
    public Mat22 rotateMatrix;

    public IShape() {
        aabb = new AABB();
        rotateMatrix = new Mat22();
        rotateMatrix.setAngle(0f);
    }

    public final void setOrientation(float radian) {
        body.orientation = radian;
        rotateMatrix.setAngle(radian);
    }

    public final void applyImpulse(Vector2f impulse, Vector2f contactVector) {
        body.velocity.add(impulse, body.invertMass);
        body.angularVelocity += body.invertInertia * Vector2f.crossProduct(contactVector, impulse);
    }

    public abstract void computeAABB();

    protected abstract void computeMass();
}