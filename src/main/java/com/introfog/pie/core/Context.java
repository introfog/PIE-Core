package com.introfog.pie.core;

import com.introfog.pie.core.math.Vector2f;

public class Context {
    private float fixedDeltaTime;
    private float deadLoopBorder;
    private float epsilon;
    private float correctPositionPercent;
    private float minBorderSlop;
    private Vector2f gravity;

    public Context() {
        fixedDeltaTime = 1f / 60f;
        deadLoopBorder = fixedDeltaTime * 20f;
        epsilon = 0.0001f;
        correctPositionPercent = 0.5f;
        minBorderSlop = 0.1f;
        // Earth value is (0f, 9.807f)
        gravity = new Vector2f(0f, 50f);
    }

    public Context(Context other) {
        this.fixedDeltaTime = other.fixedDeltaTime;
        this.deadLoopBorder = other.deadLoopBorder;
        this.epsilon = other.epsilon;
        this.correctPositionPercent = other.correctPositionPercent;
        this.minBorderSlop = other.minBorderSlop;
        this.gravity = new Vector2f(other.gravity);
    }

    public float getDeadLoopBorder() {
        return deadLoopBorder;
    }

    public void setDeadLoopBorder(float deadLoopBorder) {
        this.deadLoopBorder = deadLoopBorder;
    }

    public float getFixedDeltaTime() {
        return fixedDeltaTime;
    }

    public void setFixedDeltaTime(float fixedDeltaTime) {
        this.fixedDeltaTime = fixedDeltaTime;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }

    public float getCorrectPositionPercent() {
        return correctPositionPercent;
    }

    public void setCorrectPositionPercent(float correctPositionPercent) {
        this.correctPositionPercent = correctPositionPercent;
    }

    public float getMinBorderSlop() {
        return minBorderSlop;
    }

    public void setMinBorderSlop(float minBorderSlop) {
        this.minBorderSlop = minBorderSlop;
    }

    public Vector2f getGravity() {
        return gravity;
    }

    public void setGravity(Vector2f gravity) {
        this.gravity = new Vector2f(gravity);
    }

    public float getResting() {
        return Vector2f.mul(gravity, fixedDeltaTime).lengthWithoutSqrt() + epsilon;
    }
}
