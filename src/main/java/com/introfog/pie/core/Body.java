package com.introfog.pie.core;

import com.introfog.pie.core.math.Vector2f;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Body {
    @JsonProperty
    public float density;
    @JsonProperty
    public float restitution;
    @JsonProperty
    public Vector2f position;

    @JsonIgnore
    public float invertMass;
    @JsonIgnore
    public float staticFriction;
    @JsonIgnore
    public float dynamicFriction;

    /** Orientation in radians. */
    @JsonIgnore
    public float orientation;

    @JsonIgnore
    public float angularVelocity;
    @JsonIgnore
    public float torque;
    @JsonIgnore
    public float invertInertia;
    @JsonIgnore
    public Vector2f force;
    @JsonIgnore
    public Vector2f velocity;

    public Body() {}

    public Body(float positionX, float positionY, float density, float restitution) {
        this.density = density;
        this.restitution = restitution;

        staticFriction = 0.5f;
        dynamicFriction = 0.3f;
        torque = 0f;

        force = new Vector2f(0f, 0f);
        velocity = new Vector2f(0f, 0f);
        position = new Vector2f(positionX, positionY);
    }
}