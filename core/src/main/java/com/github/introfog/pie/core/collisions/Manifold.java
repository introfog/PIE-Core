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
package com.github.introfog.pie.core.collisions;

import com.github.introfog.pie.core.Body;
import com.github.introfog.pie.core.Context;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.ShapeType;

public class Manifold {
    public boolean areBodiesCollision;
    public float penetration;
    public float e;
    public float staticFriction;
    public float dynamicFriction;
    public Vector2f normal;
    public int contactCount = 0;
    public Vector2f[] contacts;
    public Context context;
    public Polygon polygonA;
    public Polygon polygonB;
    public Circle circleA;
    public Circle circleB;
    public Body a;
    public Body b;
    public IShape aShape;
    public IShape bShape;

    public Manifold(IShape aShape, IShape bShape, Context context) {
        this.aShape = aShape;
        this.bShape = bShape;
        this.a = aShape.body;
        this.b = bShape.body;
        this.context = new Context(context);

        areBodiesCollision = true;
        normal = new Vector2f();
        contacts = Vector2f.arrayOf(2);
    }

    public void initializeCollision() {
        if (MathPie.areEqual(a.invertedMass + b.invertedMass, 0f)) {
            a.velocity.set(0f, 0f);
            b.velocity.set(0f, 0f);
            areBodiesCollision = false;
            return;
        }

        if (aShape.type == ShapeType.CIRCLE && bShape.type == ShapeType.CIRCLE) {
            circleA = (Circle) aShape;
            circleB = (Circle) bShape;
        } else if (aShape.type == ShapeType.POLYGON && bShape.type == ShapeType.POLYGON) {
            polygonA = (Polygon) aShape;
            polygonB = (Polygon) bShape;
        } else if (aShape.type == ShapeType.POLYGON && bShape.type == ShapeType.CIRCLE) {
            polygonA = (Polygon) aShape;
            circleB = (Circle) bShape;
        } else if (aShape.type == ShapeType.CIRCLE && bShape.type == ShapeType.POLYGON) {
            circleA = (Circle) aShape;
            polygonB = (Polygon) bShape;
        }

        Collisions.table[aShape.type.ordinal()][bShape.type.ordinal()].handleCollision(this);

        // Static friction - is a value that shows how much energy need to apply to moving the body,
        // i.e. this is the threshold, if the energy is lower, then the body is at rest, if higher, then the body moves
        // Dynamic friction - friction in the usual sense, when bodies rub against each other,
        // they lose part of their energy against each other
        staticFriction = (float) StrictMath.sqrt(
                a.staticFriction * a.staticFriction + b.staticFriction * b.staticFriction);
        dynamicFriction = (float) StrictMath.sqrt(
                a.dynamicFriction * a.dynamicFriction + b.dynamicFriction * b.dynamicFriction);

        // Calculate the elasticity
        e = Math.min(a.restitution, b.restitution);

        for (int i = 0; i < contactCount; ++i) {
            // Calculate radii from COM to contact
            // Vec2 ra = contacts[i] - A->position;
            // Vec2 rb = contacts[i] - B->position;
            Vector2f radA = Vector2f.sub(contacts[i], a.position);
            Vector2f radB = Vector2f.sub(contacts[i], b.position);

            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            // Calculate the relative speed
            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            //relativeVelocity
            Vector2f rv = Vector2f.sub(b.velocity, a.velocity);
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Determine whether should perform a collision with a stop or not.
            // The idea is that the only thing that moves this object is gravity,
            // then the collision should be carried out without any restitution
            // if(rv.LenSqr( ) < (dt * gravity).LenSqr( ) + EPSILON)
            if (rv.lengthWithoutSqrt() < context.getResting()) {
                e = 0.0f;
            }
        }
    }

    public void solve() {
        normal.normalize();

        for (int i = 0; i < contactCount; i++) {
            // Calculate the contact points regarding centers
            Vector2f radA = Vector2f.sub(contacts[i], a.position);
            Vector2f radB = Vector2f.sub(contacts[i], b.position);

            // Calculate the relative velocity
            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            // relativeVelocity
            Vector2f rv = Vector2f.sub(b.velocity, a.velocity);
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Calculate the relative velocity relative to the normal direction
            float velAlongNormal = Vector2f.dotProduct(rv, normal);

            // Do not perform calculations if the velocities are divided
            if (velAlongNormal > 0) {
                return;
            }

            float raCrossN = Vector2f.crossProduct(radA, normal);
            float rbCrossN = Vector2f.crossProduct(radB, normal);
            float invertMassSum = a.invertedMass + b.invertedMass + (raCrossN * raCrossN) * a.invertedInertia
                    + (rbCrossN * rbCrossN) * b.invertedInertia;

            // Calculate the scalar of the force impulse
            float j = -(1.0f + e) * velAlongNormal;
            j /= invertMassSum;
            j /= contactCount;

            // Applying a force impulse
            Vector2f impulse = Vector2f.mul(normal, j);
            bShape.applyImpulse(impulse, radB);
            impulse.negative();
            aShape.applyImpulse(impulse, radA);

            // Friction work

            // Recalculation relative speed after application of a normal impulse
            rv = Vector2f.sub(b.velocity, a.velocity); //relativeVelocity
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Calculate the tangent vector: tangent = rb - dotProduct (rv, normal) * normal
            // Vec2 t = rv - (normal * Dot( rv, normal ));
            // t.Normalize( );
            Vector2f t = Vector2f.sub(rv, Vector2f.mul(normal, Vector2f.dotProduct(rv, normal)));
            t.normalize();

            // Calculate the value applied along the friction vector
            float jt = -Vector2f.dotProduct(rv, t);
            jt /= invertMassSum;
            jt /= contactCount;

            // If jt is very small then do not apply and do return
            if (MathPie.areEqual(jt, 0f)) {
                return;
            }

            Vector2f frictionImpulse;
            if (Math.abs(jt) < j * staticFriction) {
                // Amonton-Coulomb Law (if j value is too small, then bodies should rest)
                frictionImpulse = Vector2f.mul(t, jt);
            } else {
                frictionImpulse = Vector2f.mul(t, -j * dynamicFriction);
            }

            // Apply impulse
            bShape.applyImpulse(frictionImpulse, radB);
            frictionImpulse.negative();
            aShape.applyImpulse(frictionImpulse, radA);
        }
    }

    public void correctPosition() {
        if (penetration < context.getMinBorderSlop()) {
            return;
        }
        Vector2f correction = Vector2f.mul(normal, penetration *
                context.getCorrectPositionPercent() / (a.invertedMass + b.invertedMass));
        a.position.sub(Vector2f.mul(correction, a.invertedMass));
        b.position.add(Vector2f.mul(correction, b.invertedMass));
    }
}
