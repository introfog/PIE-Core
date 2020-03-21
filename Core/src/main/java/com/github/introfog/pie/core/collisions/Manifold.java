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
import com.github.introfog.pie.core.math.MathPIE;
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
        if (MathPIE.equal(a.invertMass + b.invertMass, 0f)) {
            a.velocity.set(0f, 0f);
            b.velocity.set(0f, 0f);
            areBodiesCollision = false;
            return;
        }

        if (aShape.type == ShapeType.circle && bShape.type == ShapeType.circle) {
            circleA = (Circle) aShape;
            circleB = (Circle) bShape;
        } else if (aShape.type == ShapeType.polygon && bShape.type == ShapeType.polygon) {
            polygonA = (Polygon) aShape;
            polygonB = (Polygon) bShape;
        } else if (aShape.type == ShapeType.polygon && bShape.type == ShapeType.circle) {
            polygonA = (Polygon) aShape;
            circleB = (Circle) bShape;
        } else if (aShape.type == ShapeType.circle && bShape.type == ShapeType.polygon) {
            circleA = (Circle) aShape;
            polygonB = (Polygon) bShape;
        }

        Collisions.table[aShape.type.ordinal()][bShape.type.ordinal()].handleCollision(this);

        // Статическое трение - величина, показывающая сколько нужно приложить энергии что бы свдинуть тела, т.е. это
        // порог, если энергия ниже, то тела покоятся, если выше, то они сдвинулись
        // Динамическое трение - трение в обычном понимании, когда тела труться друг об друга, они теряют часть своей
        // энергии друг об друга
        staticFriction = (float) StrictMath.sqrt(
                a.staticFriction * a.staticFriction + b.staticFriction * b.staticFriction);
        dynamicFriction = (float) StrictMath.sqrt(
                a.dynamicFriction * a.dynamicFriction + b.dynamicFriction * b.dynamicFriction);

        // Вычисляем упругость
        e = Math.min(a.restitution, b.restitution);

        for (int i = 0; i < contactCount; ++i) {
            // Calculate radii from COM to contact
            // Vec2 ra = contacts[i] - A->position;
            // Vec2 rb = contacts[i] - B->position;
            Vector2f radA = Vector2f.sub(contacts[i], a.position);
            Vector2f radB = Vector2f.sub(contacts[i], b.position);

            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            // Вычисляем относительную скорость
            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            Vector2f rv = Vector2f.sub(b.velocity, a.velocity); //relativeVelocity
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Определяем, следует ли нам выполнять столкновение с остановкой или нет
            // Идея заключается в том, что единственное, что движет этим объектом, - это гравитация,
            // то столкновение должно выполняться без какой-либо реституции
            // if(rv.LenSqr( ) < (dt * gravity).LenSqr( ) + EPSILON)
            if (rv.lengthWithoutSqrt() < context.getResting()) {
                e = 0.0f;
            }
        }
    }

    public void solve() {
        normal.normalize();

        for (int i = 0; i < contactCount; i++) {
            // Вычисляем точки контанка относителньо центров
            Vector2f radA = Vector2f.sub(contacts[i], a.position);
            Vector2f radB = Vector2f.sub(contacts[i], b.position);

            // Вычисляем относительную скорость
            // Vec2 rv = B->velocity + Cross( B->angularVelocity, rb ) -
            // A->velocity - Cross( A->angularVelocity, ra );
            // relativeVelocity
            Vector2f rv = Vector2f.sub(b.velocity, a.velocity);
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Вычисляем относительную скорость относительно направления нормали
            float velAlongNormal = Vector2f.dotProduct(rv, normal);

            // Не выполняем вычислений, если скорости разделены
            if (velAlongNormal > 0) {
                return;
            }

            float raCrossN = Vector2f.crossProduct(radA, normal);
            float rbCrossN = Vector2f.crossProduct(radB, normal);
            float invertMassSum = a.invertMass + b.invertMass + (raCrossN * raCrossN) * a.invertInertia
                    + (rbCrossN * rbCrossN) * b.invertInertia;

            // Вычисляем скаляр импульса силы
            float j = -(1.0f + e) * velAlongNormal;
            j /= invertMassSum;
            j /= contactCount;

            // Прикладываем импульс силы
            Vector2f impulse = Vector2f.mul(normal, j);
            bShape.applyImpulse(impulse, radB);
            impulse.negative();
            aShape.applyImpulse(impulse, radA);

            //--------Работа с трением

            // Перерасчет относительной скорости, после приложения нормального импульса
            rv = Vector2f.sub(b.velocity, a.velocity); //relativeVelocity
            rv.add(Vector2f.crossProduct(b.angularVelocity, radB));
            rv.sub(Vector2f.crossProduct(a.angularVelocity, radA));

            // Вычисялем касательный вектор: tangent = rb - dotProduct (rv, normal) * normal
            // Vec2 t = rv - (normal * Dot( rv, normal ));
            // t.Normalize( );
            Vector2f t = Vector2f.sub(rv, Vector2f.mul(normal, Vector2f.dotProduct(rv, normal)));
            t.normalize();

            // Вычисляем величину, прилагаемую вдоль вектора трения
            float jt = -Vector2f.dotProduct(rv, t);
            jt /= invertMassSum;
            jt /= contactCount;

            // Если jt очень маленько то не применять и делать return
            if (MathPIE.equal(jt, 0f)) {
                return;
            }

            Vector2f frictionImpulse;
            if (Math.abs(jt) < j * staticFriction) {
                // Закон Амонтона — Кулона (если велечина j слишком маленькая, то тела должны покояться)
                frictionImpulse = Vector2f.mul(t, jt);
            } else {
                frictionImpulse = Vector2f.mul(t, -j * dynamicFriction);
            }

            // Пркладываем
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
                context.getCorrectPositionPercent() / (a.invertMass + b.invertMass));
        a.position.sub(Vector2f.mul(correction, a.invertMass));
        b.position.add(Vector2f.mul(correction, b.invertMass));
    }
}