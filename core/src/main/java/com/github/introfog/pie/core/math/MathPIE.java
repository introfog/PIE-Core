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

// TODO Change class to context or property class
public final class MathPIE {
    private MathPIE() {
        // Empty constructor
    }

    public static final float STATIC_BODY_DENSITY = 0f;

    public static final int MAX_POLY_VERTEX_COUNT = 64;

    public static final float EPSILON = 0.0001f;

    public static final int BIG_ENOUGH_INT = 16 * 1024;
    public static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT + 0.0000;
    public static int fastFloor(float f) {
        return (int) (f + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static boolean areEqual(float a, float b) {
        return Math.abs(a - b) <= EPSILON;
    }

    // TODO what is the function gt?
    public static final float BIAS_RELATIVE = 0.95f;
    public static final float BIAS_ABSOLUTE = 0.01f;
    public static boolean gt(float a, float b) {
        return a >= b * BIAS_RELATIVE + a * BIAS_ABSOLUTE;
    }
}
