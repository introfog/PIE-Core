package com.introfog.pie.core;

import com.introfog.pie.core.collisions.Collisions;
import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.math.Vector2f;

public class Manifold {
    public boolean areBodiesCollision;
    public float penetration;
    public float e;
    public float staticFriction;
    public float dynamicFriction;
    public Vector2f normal;
    public int contactCount = 0;
    public Vector2f[] contacts;
    public Polygon polygonA;
    public Polygon polygonB;
    public Circle circleA;
    public Circle circleB;
    public Body a;
    public Body b;

    public Manifold(Body a, Body b) {
        this.a = a;
        this.b = b;

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

        if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.circle) {
            circleA = (Circle) a.shape;
            circleB = (Circle) b.shape;
        } else if (a.shape.type == Shape.Type.polygon && b.shape.type == Shape.Type.polygon) {
            polygonA = (Polygon) a.shape;
            polygonB = (Polygon) b.shape;
        } else if (a.shape.type == Shape.Type.polygon && b.shape.type == Shape.Type.circle) {
            polygonA = (Polygon) a.shape;
            circleB = (Circle) b.shape;
        } else if (a.shape.type == Shape.Type.circle && b.shape.type == Shape.Type.polygon) {
            circleA = (Circle) a.shape;
            polygonB = (Polygon) b.shape;
        }

        Collisions.table[a.shape.type.ordinal()][b.shape.type.ordinal()].handleCollision(this);

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
            if (rv.lengthWithoutSqrt() < MathPIE.RESTING) {
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
            Vector2f rv = Vector2f.sub(b.velocity, a.velocity); //relativeVelocity
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
            b.applyImpulse(impulse, radB);
            impulse.negative();
            a.applyImpulse(impulse, radA);

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
            b.applyImpulse(frictionImpulse, radB);
            frictionImpulse.negative();
            a.applyImpulse(frictionImpulse, radA);
        }
    }

    public void correctPosition() {
        if (penetration < MathPIE.MIN_BORDER_SLOP) {
            return;
        }
        Vector2f correction = Vector2f.mul(normal,
                penetration * MathPIE.CORRECT_POSITION_PERCENT / (a.invertMass + b.invertMass));
        a.position.sub(Vector2f.mul(correction, a.invertMass));
        b.position.add(Vector2f.mul(correction, b.invertMass));
    }
}