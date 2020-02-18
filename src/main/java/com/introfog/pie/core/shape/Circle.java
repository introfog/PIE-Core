package com.introfog.pie.core.shape;

import com.introfog.pie.core.Body;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Circle extends IShape {
    @JsonProperty
    public float radius;

    public Circle() {
        type = Type.circle;
    }

    public Circle(float radius, float centreX, float centreY, float density, float restitution) {
        body = new Body(centreX, centreY, density, restitution);
        this.radius = radius;

        computeMass();
        computeAABB();

        type = Type.circle;
    }

    @Override
    public void computeAABB() {
        aabb.min.set(body.position.x - radius, body.position.y - radius);
        aabb.max.set(body.position.x + radius, body.position.y + radius);
    }

    @Override
    protected void computeMass() {
        float mass = (float) Math.PI * radius * radius * body.density;
        body.invertMass = (mass == 0f) ? 0f : 1f / mass;

        float inertia = radius * radius / body.invertMass;
        body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
    }
}