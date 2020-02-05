package com.introfog.pie.core.math;

// TODO Change class to context or property class
public class MathPIE {
    public static final float STATIC_BODY_DENSITY = 0f;

    public static final int MAX_POLY_VERTEX_COUNT = 64;

    public static final float EPSILON = 0.0001f;

    public static final int BIG_ENOUGH_INT = 16 * 1024;
    public static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT + 0.0000;
    public static int fastFloor(float f) {
        return (int) (f + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static boolean equal(float a, float b) {
        return Math.abs(a - b) <= EPSILON;
    }

    // TODO what is the function gt?
    public static final float BIAS_RELATIVE = 0.95f;
    public static final float BIAS_ABSOLUTE = 0.01f;
    public static boolean gt(float a, float b) {
        return a >= b * BIAS_RELATIVE + a * BIAS_ABSOLUTE;
    }
}