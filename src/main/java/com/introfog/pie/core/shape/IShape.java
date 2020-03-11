package com.introfog.pie.core.shape;

import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.Mat22;
import com.introfog.pie.core.math.Vector2f;

import java.util.Objects;

public abstract class IShape {
    public ShapeType type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IShape shape = (IShape) o;
        return type == shape.type &&
                body.equals(shape.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, body);
    }

    protected abstract void computeMass();
}