package com.introfog.pie.core.math;

// Матрица поворота размера 2 х 2
public class Mat22 {
    private float m00, m01;
    private float m10, m11;

    public void setAngle(Vector2f x, Vector2f y) {
        m00 = x.x;
        m01 = x.y;
        m01 = y.x;
        m11 = y.y;
    }

    public void setAngle(float radian) {
        float cos = (float) Math.cos(radian);
        float sin = (float) Math.sin(radian);

        m00 = cos;
        m01 = -sin;
        m10 = sin;
        m11 = cos;
    }

    public void mul(Vector2f in, Vector2f out) {
        mul(in.x, in.y, out);
    }

    public void mul(float x, float y, Vector2f out) {
        out.x = m00 * x + m01 * y;
        out.y = m10 * x + m11 * y;
    }

    public Vector2f mul(Vector2f vec) {
        Vector2f result = new Vector2f();
        mul(vec, result);
        return result;
    }

    public void transposeMul(Vector2f in, Vector2f out) {
        transposeMul(in.x, in.y, out);
    }

    public void transposeMul(float x, float y, Vector2f out) {
        out.x = m00 * x + m10 * y;
        out.y = m01 * x + m11 * y;
    }
}
