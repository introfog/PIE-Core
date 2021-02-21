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

import java.util.StringJoiner;

public final class RotationMatrix2x2 {
    public float m00;
    public float m01;
    public float m10;
    public float m11;

    public RotationMatrix2x2() {
        setAngle(0);
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

    public void transposeMul(Vector2f in, Vector2f out) {
        transposeMul(in.x, in.y, out);
    }

    public void transposeMul(float x, float y, Vector2f out) {
        out.x = m00 * x + m10 * y;
        out.y = m01 * x + m11 * y;
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add(new StringJoiner("; ", "{", "}")
                        .add("m00=" + m00)
                        .add("m01=" + m01)
                        .toString())
                .add(new StringJoiner("; ", "{", "}")
                        .add("m10=" + m10)
                        .add("m11=" + m11)
                        .toString())
                .toString();
    }
}
