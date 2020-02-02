package com.introfog.pie.core.shape;

import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.Mat22;

public abstract class Shape {
    public enum Type {
        circle, polygon
    }

    public Type type;
    public AABB aabb;
    public Mat22 rotateMatrix;
    public Body body;


    public Shape() {
        aabb = new AABB();
        rotateMatrix = new Mat22();
        rotateMatrix.setAngle(0f);
    }

    public void setOrientation(float radian) {
        body.orientation = radian;
        rotateMatrix.setAngle(radian);
    }

    public abstract void computeAABB();

    protected abstract void computeMass();
}