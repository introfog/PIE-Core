package com.introfog.pie.core.math;

import java.util.Objects;

public class Vector2f {
    public float x;
    public float y;

    public static Vector2f mul(Vector2f a, float s) {
        return new Vector2f(a.x * s, a.y * s);
    }

    public static Vector2f mul(Vector2f a, Vector2f b) {
        return new Vector2f(a.x * b.x, a.y * b.y);
    }

    public static Vector2f sub(Vector2f a, Vector2f b) {
        return new Vector2f(a.x - b.x, a.y - b.y);
    }

    public static Vector2f sub(Vector2f a, float bX, float bY) {
        return new Vector2f(a.x - bX, a.y - bY);
    }

    public static float distanceWithoutSqrt(Vector2f a, Vector2f b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    public static float dotProduct(Vector2f a, Vector2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static float crossProduct(Vector2f a, Vector2f b) {
        return a.x * b.y - a.y * b.x;
    }

    public static Vector2f crossProduct(float a, Vector2f v) {
        Vector2f result = new Vector2f();
        result.x = v.y * -a;
        result.y = v.x * a;
        return result;
    }

    public static Vector2f[] arrayOf(int length) {
        Vector2f[] array = new Vector2f[length];

        for (int i = 0; i < length; i++) {
            array[i] = new Vector2f();
        }

        return array;
    }

    public Vector2f() {
        this(0f, 0f);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f vector2f) {
        this(vector2f.x, vector2f.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vector2f vector2f = (Vector2f) o;
        return Float.compare(vector2f.x, x) == 0 &&
                Float.compare(vector2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vector2f [" + x + "][" + y + "]";
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f vector2f) {
        set(vector2f, 1f);
    }

    public void set(Vector2f vector2f, float multiplier) {
        x = vector2f.x * multiplier;
        y = vector2f.y * multiplier;
    }

    public float lengthWithoutSqrt() {
        return x * x + y * y;
    }

    public void div(float value) {
        x /= value;
        y /= value;
    }

    public void mul(float value) {
        x *= value;
        y *= value;
    }

    public void sub(Vector2f vector2f) {
        x -= vector2f.x;
        y -= vector2f.y;
    }

    public void add(Vector2f vector2f, float s) {
        x += vector2f.x * s;
        y += vector2f.y * s;
    }

    public void add(Vector2f vector2f) {
        add(vector2f, 1f);
    }

    public void normalize() {
        if (MathPIE.equal(x * x + y * y, 1f)) {
            return;
        }

        float length = lengthWithoutSqrt();
        if (length == 0f) {
            return;
        }
        length = (float) Math.sqrt(length);
        x /= length;
        y /= length;
    }

    public void negative() {
        x = -x;
        y = -y;
    }
}