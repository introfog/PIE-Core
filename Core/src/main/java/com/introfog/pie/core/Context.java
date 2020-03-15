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
package com.introfog.pie.core;

import com.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.introfog.pie.core.math.Vector2f;

public class Context {
    private float fixedDeltaTime;
    private float deadLoopBorder;
    private float epsilon;
    private float correctPositionPercent;
    private float minBorderSlop;
    private Vector2f gravity;
    private AbstractBroadPhase broadPhase;

    public Context() {
        fixedDeltaTime = 1f / 60f;
        deadLoopBorder = fixedDeltaTime * 20f;
        epsilon = 0.0001f;
        correctPositionPercent = 0.5f;
        minBorderSlop = 0.1f;
        // Earth value is (0f, 9.807f)
        gravity = new Vector2f(0f, 50f);
        broadPhase = new BruteForceMethod();
    }

    public Context(Context other) {
        this.fixedDeltaTime = other.fixedDeltaTime;
        this.deadLoopBorder = other.deadLoopBorder;
        this.epsilon = other.epsilon;
        this.correctPositionPercent = other.correctPositionPercent;
        this.minBorderSlop = other.minBorderSlop;
        this.gravity = other.gravity;
        this.broadPhase = other.broadPhase;
    }

    public float getDeadLoopBorder() {
        return deadLoopBorder;
    }

    public Context setDeadLoopBorder(float deadLoopBorder) {
        this.deadLoopBorder = deadLoopBorder;
        return this;
    }

    public float getFixedDeltaTime() {
        return fixedDeltaTime;
    }

    public Context setFixedDeltaTime(float fixedDeltaTime) {
        this.fixedDeltaTime = fixedDeltaTime;
        return this;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public Context setEpsilon(float epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    public float getCorrectPositionPercent() {
        return correctPositionPercent;
    }

    public Context setCorrectPositionPercent(float correctPositionPercent) {
        this.correctPositionPercent = correctPositionPercent;
        return this;
    }

    public float getMinBorderSlop() {
        return minBorderSlop;
    }

    public Context setMinBorderSlop(float minBorderSlop) {
        this.minBorderSlop = minBorderSlop;
        return this;
    }

    public Vector2f getGravity() {
        return gravity;
    }

    public Context setGravity(Vector2f gravity) {
        this.gravity = new Vector2f(gravity);
        return this;
    }

    public AbstractBroadPhase getBroadPhase() {
        return broadPhase;
    }

    public Context setBroadPhase(AbstractBroadPhase broadPhase) {
        this.broadPhase = broadPhase;
        return this;
    }

    public float getResting() {
        return Vector2f.mul(gravity, fixedDeltaTime).lengthWithoutSqrt() + epsilon;
    }
}
