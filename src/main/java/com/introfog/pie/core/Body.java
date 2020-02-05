package com.introfog.pie.core;

import com.introfog.pie.core.math.Vector2f;
import com.introfog.pie.core.shape.IShape;

public class Body {
    public float invertMass;
    public float restitution;
    public float density;
    public float staticFriction;
    public float dynamicFriction;

    /** Orientation in radians. */
    public float orientation;

    public float angularVelocity;
    public float torque;
    public float invertInertia;
    public Vector2f position;
    public Vector2f force;
    public Vector2f velocity;
    public IShape shape;

    public Body(IShape shape, float positionX, float positionY, float density, float restitution) {
        this.shape = shape;
        this.density = density;
        this.restitution = restitution;

        staticFriction = 0.5f;
        dynamicFriction = 0.3f;
        torque = 0f;

        force = new Vector2f(0f, 0f);
        velocity = new Vector2f(0f, 0f);
        position = new Vector2f(positionX, positionY);
    }

    public void setOrientation(float radian) {
        orientation = radian;
        shape.setOrientation(radian);
    }

    public void applyImpulse(Vector2f impulse, Vector2f contactVector) {
        velocity.add(impulse, invertMass);
        angularVelocity += invertInertia * Vector2f.crossProduct(contactVector, impulse);
    }

    // TODO Is the method used? if so, then check whether the method should compare links or check for identity objects.
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}