package com.introfog.pie.core.math;

// TODO Change class to context or property class
public class MathPIE {
    public static final Vector2f GRAVITY = new Vector2f(0f, 50f); //9.807f
    public static final float FIXED_DELTA_TIME = 1f / 60f;
    public static final float DEAD_LOOP_BORDER = FIXED_DELTA_TIME * 20f;

    public static final float CORRECT_POSITION_PERCENT = 0.5f;
    public static final float MIN_BORDER_SLOP = 0.1f;

    public static final float STATIC_BODY_DENSITY = 0f;

    public static final int MAX_POLY_VERTEX_COUNT = 64;

    public static final float PI = 3.141_592f;
    public static final float EPSILON = 0.0001f;
    public static final float RESTING = Vector2f.mul(GRAVITY, FIXED_DELTA_TIME).lengthWithoutSqrt() + EPSILON;

    public static final int BIG_ENOUGH_INT = 16 * 1024;
    public static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT + 0.0000;

    public static int fastFloor(float f) {
        return (int) (f + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }


    public static boolean equal(float a, float b) {
        return Math.abs(a - b) <= EPSILON;
    }


    public static final float BIAS_RELATIVE = 0.95f;
    public static final float BIAS_ABSOLUTE = 0.01f;

    public static boolean gt(float a, float b) {
        return a >= b * BIAS_RELATIVE + a * BIAS_ABSOLUTE;
    }
}