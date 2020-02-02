package com.introfog.pie.core.shape;

import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.MathPIE;

public class Circle extends Shape {
    public float radius;

    public Circle(float radius, float centreX, float centreY, float density, float restitution) {
        body = new Body(this, centreX, centreY, density, restitution);
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
        float mass = MathPIE.PI * radius * radius * body.density;
        body.invertMass = (mass == 0f) ? 0f : 1f / mass;

        float inertia = radius * radius / body.invertMass;
        body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
    }
}