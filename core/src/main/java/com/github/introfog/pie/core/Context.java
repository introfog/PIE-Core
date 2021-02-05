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
package com.github.introfog.pie.core;

import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.collisions.broadphase.IBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.narrowphase.IShapeCollisionHandler;
import com.github.introfog.pie.core.collisions.narrowphase.ShapeCollisionHandlersMapper;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Aabb;
import com.github.introfog.pie.core.shape.IShape;

/**
 * The class contains fields that specify the basic properties of the
 * physical engine, in particular the {@link World} class properties.
 */
public class Context {
    private float fixedDeltaTime;
    private float deadLoopBorder;
    private float correctPositionPercent;
    private float minBorderSlop;
    private int collisionSolveIterations;
    private Vector2f gravity;
    private IBroadPhase broadPhaseMethod;
    private ShapeCollisionHandlersMapper shapeCollisionHandlersMapper;

    /**
     * Instantiates a new {@link Context} instance with default values of fields (default constructor).
     *
     * <p>
     * Default context values:
     * <ul>
     * <li>fixedDeltaTime = 1 / 60
     * <li>deadLoopBorder = 20 / 60
     * <li>correctPositionPercent = 0.5
     * <li>minBorderSlop = 0.1
     * <li>collisionSolveIterations = 1
     * <li>gravity = (0, 50)
     * <li>broadPhaseMethod = {@link BruteForceMethod}
     * <li>narrowPhaseAdapter = {@link ShapeCollisionHandlersMapper#createAndGetDefaultMapping()}
     * </ul>
     */
    public Context() {
        fixedDeltaTime = 1f / 60f;
        deadLoopBorder = fixedDeltaTime * 20f;
        correctPositionPercent = 0.5f;
        minBorderSlop = 0.1f;
        collisionSolveIterations = 1;
        // Earth value is (0f, 9.807f)
        gravity = new Vector2f(0f, 50f);
        broadPhaseMethod = new BruteForceMethod();
        shapeCollisionHandlersMapper = ShapeCollisionHandlersMapper.createAndGetDefaultMapping();
    }

    /**
     * Instantiates a new {@link Context} instance based on another {@link Context} instance (copy constructor).
     *
     * <p>
     * Note that this copy constructor create a deep copy of context. For example for
     * broad phase field method {@link IBroadPhase#newInstance()} is used.
     *
     * @param other the other {@link Context} instance
     */
    public Context(Context other) {
        this.setFixedDeltaTime(other.getFixedDeltaTime());
        this.setDeadLoopBorder(other.getDeadLoopBorder());
        this.setCorrectPositionPercent(other.getCorrectPositionPercent());
        this.setMinBorderSlop(other.getMinBorderSlop());
        this.setCollisionSolveIterations(other.getCollisionSolveIterations());
        this.setGravity(new Vector2f(other.getGravity()));
        this.setBroadPhaseMethod(other.getBroadPhaseMethod().newInstance());
        this.setShapeCollisionMapping(new ShapeCollisionHandlersMapper(other.getShapeCollisionMapping()));
    }

    /**
     * Sets the number of collision solve iterations.
     *
     * <p>
     * Because collisions are resolved not absolutely, but in part, to speed up the performance,
     * this number determines how many times the {@link Manifold#solve()} method will be called.
     * The larger the number, the more accurate collision resolution will be, but the longer it
     * will take to resolve collisions.
     *
     * @param collisionSolveIterations the number of collision solve iterations
     * @return the {@link Context} instance
     *
     * @throws IllegalArgumentException if negative number passed
     */
    public Context setCollisionSolveIterations(int collisionSolveIterations) {
        if (collisionSolveIterations < 0) {
            throw new IllegalArgumentException(PieExceptionMessage.COLLISION_SOLVE_ITERATION_MUST_NOT_BE_NEGATIVE);
        }
        this.collisionSolveIterations = collisionSolveIterations;
        return this;
    }

    /**
     * Gets the number of collision solve iterations.
     *
     * <p>
     * Because collisions are resolved not absolutely, but in part, to speed up the performance,
     * this number determines how many times the {@link Manifold#solve()} method will be called.
     * The larger the number, the more accurate collision resolution will be, but the longer it
     * will take to resolve collisions.
     *
     * @return the number of collision solve iterations
     */
    public int getCollisionSolveIterations() {
        return collisionSolveIterations;
    }

    /**
     * Gets the dead loop border.
     *
     * The deadLoopBorder is the boundary that serves to prevent the dead loop, i.e. when the iteration of the engine
     * takes longer than {@link Context#fixedDeltaTime}, and the World accumulator becomes very large, and the engine
     * freezes until it performs the required number of steps.
     * The recommended value is 20 times greater than the value of {@link Context#fixedDeltaTime}.
     *
     * @return the dead loop border
     */
    public float getDeadLoopBorder() {
        return deadLoopBorder;
    }

    /**
     * Sets the dead loop border.
     *
     * The deadLoopBorder is the boundary that serves to prevent the dead loop, i.e. when the iteration of the engine
     * takes longer than {@link Context#fixedDeltaTime}, and the World accumulator becomes very large, and the engine
     * freezes until it performs the required number of steps.
     * The recommended value is 20 times greater than the value of {@link Context#fixedDeltaTime}.
     *
     * @param deadLoopBorder the dead loop border
     * @return the {@link Context} instance
     */
    public Context setDeadLoopBorder(float deadLoopBorder) {
        this.deadLoopBorder = deadLoopBorder;
        return this;
    }

    /**
     * Gets the fixed delta time.
     *
     * The fixedDeltaTime is a number that is defined as the difference between the unit and the desired engine
     * refresh rate. Desired refresh rate (DSR) - how many times per second the world and bodies in it will be updated,
     * the higher the DSR, the smoother the physics in the world will be, but if you set the DSR too high,
     * the engine may not have time to iterate and a dead loop may occur (see {@link Context#deadLoopBorder}).
     *
     * @return the fixed delta time
     */
    public float getFixedDeltaTime() {
        return fixedDeltaTime;
    }

    /**
     * Sets the fixed delta time.
     *
     * The fixedDeltaTime is a number that is defined as the difference between the unit and the desired engine
     * refresh rate. Desired refresh rate (DSR) - how many times per second the world and bodies in it will be updated,
     * the higher the DSR, the smoother the physics in the world will be, but if you set the DSR too high,
     * the engine may not have time to iterate and a dead loop may occur (see {@link Context#deadLoopBorder}).
     *
     * @param fixedDeltaTime the fixed delta time
     * @return the {@link Context} instance
     */
    public Context setFixedDeltaTime(float fixedDeltaTime) {
        this.fixedDeltaTime = fixedDeltaTime;
        return this;
    }

    /**
     * Gets the correct position percent.
     *
     * The correctPositionPercent is used to prevent the situation of sinking objects, i.e. for example,
     * when a body hits a wall with infinite mass (the reciprocal mass is 0, in this way static bodies are
     * determined in Pie), due to a floating-point calculation, the error is accumulating and the body begins
     * to sink in the wall. Therefore, at each iteration, the object is moving along the collision normal by
     * a correctPositionPercent of penetration depth (usually 20-80 percent).
     *
     * @return the correct position percent
     */
    public float getCorrectPositionPercent() {
        return correctPositionPercent;
    }

    /**
     * Sets the correct position percent.
     *
     * The correctPositionPercent is used to prevent the situation of sinking objects, i.e. for example,
     * when a body hits a wall with infinite mass (the reciprocal mass is 0, in this way static bodies are
     * determined in Pie), due to a floating-point calculation, the error is accumulating and the body begins
     * to sink in the wall. Therefore, at each iteration, the object is moving along the collision normal by
     * a correctPositionPercent of penetration depth (usually 20-80 percent).
     *
     * @param correctPositionPercent the correct position percent
     * @return the {@link Context} instance
     */
    public Context setCorrectPositionPercent(float correctPositionPercent) {
        this.correctPositionPercent = correctPositionPercent;
        return this;
    }

    /**
     * Gets the minimal border slop.
     *
     * The minBorderSlop serves to prevent the jittering of objects back and forth when they rest upon
     * one another. Those, when the penetration is less than minBorderSlop, position correction is
     * not performed (see {@link Context#correctPositionPercent}).
     *
     * @return the minimal border slop
     */
    public float getMinBorderSlop() {
        return minBorderSlop;
    }

    /**
     * Sets the minimal border slop.
     *
     * The minBorderSlop serves to prevent the jittering of objects back and forth when they rest upon
     * one another. Those, when the penetration is less than minBorderSlop, position correction is
     * not performed (see {@link Context#correctPositionPercent}).
     *
     * @param minBorderSlop the minimal border slop
     * @return the {@link Context} instance
     */
    public Context setMinBorderSlop(float minBorderSlop) {
        this.minBorderSlop = minBorderSlop;
        return this;
    }

    /**
     * Gets the world gravity.
     *
     * Note that in order to achieve object physics similar to real physics, it is necessary to set more
     * gravity than on planet Earth, this is due to the fact that when you create an object 100x100 in
     * Pie, in the real world it would be 100 per 100 meters.
     *
     * @return the gravity
     */
    public Vector2f getGravity() {
        return gravity;
    }

    /**
     * Sets the world gravity.
     *
     * Note that in order to achieve object physics similar to real physics, it is necessary to set more
     * gravity than on planet Earth, this is due to the fact that when you create an object 100x100 in
     * Pie, in the real world it would be 100 per 100 meters.
     *
     * @param gravity the gravity
     * @return the {@link Context} instance
     */
    public Context setGravity(Vector2f gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * Gets the broad phase method.
     *
     * The broad phase method is used to determine possible collisions
     * of bodies, through the definition of collisions {@link Aabb}.
     *
     * @return the broad phase method
     */
    public IBroadPhase getBroadPhaseMethod() {
        return broadPhaseMethod;
    }

    /**
     * Sets the broad phase method.
     *
     * The broad phase method is used to determine possible collisions
     * of bodies, through the definition of collisions {@link Aabb}.
     *
     * @param broadPhaseMethod the broad phase method
     * @return the {@link Context} instance
     */
    public Context setBroadPhaseMethod(IBroadPhase broadPhaseMethod) {
        this.broadPhaseMethod = broadPhaseMethod;
        return this;
    }

    /**
     * Gets the narrow phase adapter.
     *
     * The narrow phase adapter is used to handle possible
     * {@link IShape} collisions based on real shape form.
     *
     * @return the broad phase method
     * @see IShapeCollisionHandler
     */
    public ShapeCollisionHandlersMapper getShapeCollisionMapping() {
        return shapeCollisionHandlersMapper;
    }

    /**
     * Gets the narrow phase adapter.
     *
     * The narrow phase adapter is used to handle possible
     * {@link IShape} collisions based on real shape form.
     *
     * @param shapeCollisionHandlersMapper the broad phase method
     * @return the {@link Context} instance
     * @see IShapeCollisionHandler
     */
    public Context setShapeCollisionMapping(ShapeCollisionHandlersMapper shapeCollisionHandlersMapper) {
        this.shapeCollisionHandlersMapper = shapeCollisionHandlersMapper;
        return this;
    }

    /**
     * Get the resting.
     *
     * The resting is used to determine when need to make a collision with a stop.
     *
     * @return the resting
     */
    public float getResting() {
        return Vector2f.mul(gravity, fixedDeltaTime).lengthWithoutSqrt() + MathPie.EPSILON;
    }
}
