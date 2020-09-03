/*
    Copyright 2020 Dmitry Chubrick

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.github.introfog.pie.core.math;

import java.util.Objects;
import java.util.StringJoiner;

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

    public static float distanceWithoutSqrt(Vector2f a, Vector2f b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    public static float dotProduct(Vector2f a, Vector2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static float crossProduct(Vector2f a, Vector2f b) {
        return a.x * b.y - a.y * b.x;
    }

    public static Vector2f crossProduct(float a, Vector2f vec) {
        return new Vector2f(vec.y * -a, vec.x * a);
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

    public Vector2f(Vector2f vec) {
        this(vec.x, vec.y);
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
        return new StringJoiner("; ", "{", "}")
                .add("" + x)
                .add("" + y)
                .toString();
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f vec) {
        this.set(vec.x, vec.y);
    }

    public float lengthWithoutSqrt() {
        return x * x + y * y;
    }

    public void mul(float value) {
        x *= value;
        y *= value;
    }

    public void mul(Vector2f vec) {
        x *= vec.x;
        y *= vec.y;
    }

    public void sub(Vector2f vec) {
        x -= vec.x;
        y -= vec.y;
    }

    public void add(Vector2f vec, float s) {
        x += vec.x * s;
        y += vec.y * s;
    }

    public void add(Vector2f vec) {
        add(vec, 1f);
    }

    public void normalize() {
        if (MathPIE.equal(x * x + y * y, 1.0f)) {
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
