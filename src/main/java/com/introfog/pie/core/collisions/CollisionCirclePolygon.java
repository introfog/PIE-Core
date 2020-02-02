package com.introfog.pie.core.collisions;

import com.introfog.pie.core.shape.Circle;
import com.introfog.pie.core.shape.Polygon;
import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.math.Vector2f;

public class CollisionCirclePolygon implements CollisionCallback {
    public static final CollisionCirclePolygon instance = new CollisionCirclePolygon();

    @Override
    public void handleCollision(Manifold manifold) {
        Circle A = manifold.circleA;
        Polygon B = manifold.polygonB;

        // Перевели координаты центра в координаты полигона
        // center = B->u.Transpose( ) * (center - b->position);
        Vector2f centerA = new Vector2f(A.body.position);
        centerA.sub(B.body.position);
        B.rotateMatrix.transposeMul(centerA, centerA);

        // Ищем ближайшее ребро полигона к центру окружности, проецируя центр на каждую нормаль ребра полигона
        float separation = -Float.MAX_VALUE;
        float separationIfCircleInPolygon = Float.MAX_VALUE;
        int indexFaceNormalIfCircleInPolygon = 0;
        int indexFaceNormal = 0;
        float dotProduct;
        Vector2f projection = new Vector2f();
        Vector2f realProjection = new Vector2f();
        Vector2f tmpV = new Vector2f();
        for (int i = 0; i < B.vertexCount; i++) {
            tmpV.set(centerA);
            tmpV.sub(B.vertices[i]);
            dotProduct = Vector2f.dotProduct(B.normals[i], tmpV);
            projection.x = dotProduct * B.normals[i].x;
            projection.y = dotProduct * B.normals[i].y;

            // Знак скалярного произведение показывает, находиться ли центр с противоположной стороны прямой от нормали
            if (dotProduct > 0f && projection.lengthWithoutSqrt() > A.radius * A.radius) {
                manifold.areBodiesCollision = false;
                return;
            }

            if (dotProduct > separation) {
                realProjection.set(projection);
                separation = dotProduct;
                indexFaceNormal = i;
            }
            // Сохраняем ближайшее ребро к центру окружности, наслучай если центр внутри полигона,
            // обычное dotProduct > separation даёт не правильный ответ, т.к. все производные отрицательные,
            // а нам нужно меньшее по модулю
            if (Math.abs(dotProduct) < separationIfCircleInPolygon) {
                separationIfCircleInPolygon = Math.abs(dotProduct);
                indexFaceNormalIfCircleInPolygon = i;
            }
        }

        // Если max скалярное произведение меньше 0, то значит центр круга внутри полигона
        if (separation < MathPIE.EPSILON) {
            // m->normal = -(B->u * B->m_normals[faceNormal]);
            // m->contacts[0] = m->normal * A->radius + a->position;

            manifold.contactCount = 1;
            B.rotateMatrix.mul(B.normals[indexFaceNormalIfCircleInPolygon], manifold.normal);
            manifold.normal.negative();

            manifold.contacts[0].set(manifold.normal);
            manifold.contacts[0].mul(A.radius);
            manifold.contacts[0].add(A.body.position);
            manifold.penetration = A.radius;
            return;
        }

        // Мы нашли ближайшее ребро к центру окржуности, и центр круга лежит снаружи полигона
        // Теперь определяем область Вороного в которой находится центр круга относительно ближайшего ребра полигона
        Vector2f v1 = new Vector2f(B.vertices[indexFaceNormal]);
        Vector2f v2 = new Vector2f(B.vertices[(indexFaceNormal + 1) % B.vertexCount]);

        float dot1 = Vector2f.dotProduct(Vector2f.sub(centerA, v1), Vector2f.sub(v2, v1));
        float dot2 = Vector2f.dotProduct(Vector2f.sub(centerA, v2), Vector2f.sub(v1, v2));

        if (dot1 <= 0f) {
            // Ближе к первой вершине
            if (Vector2f.distanceWithoutSqrt(centerA, v1) > A.radius * A.radius) {
                manifold.areBodiesCollision = false;
                return;
            }

            manifold.penetration = A.radius - (float) Math.sqrt(Vector2f.distanceWithoutSqrt(centerA, v1));

            manifold.contactCount = 1;
            // Vec2 n = v1 - center;
            // n = B->u * n;
            // n.Normalize( );
            // m->normal = n;
            // v1 = B->u * v1 + b->position;
            // m->contacts[0] = v1;
            Vector2f n = Vector2f.sub(v1, centerA);
            B.rotateMatrix.mul(n, n);
            n.normalize();
            manifold.normal.set(n);
            B.rotateMatrix.mul(v1, v1);
            v1.add(B.body.position);
            manifold.contacts[0].set(v1);
        } else if (dot2 <= 0f) {
            // Ближе ко второй вершине
            if (Vector2f.distanceWithoutSqrt(centerA, v2) > A.radius * A.radius) {
                manifold.areBodiesCollision = false;
                return;
            }

            manifold.penetration = A.radius - (float) Math.sqrt(Vector2f.distanceWithoutSqrt(centerA, v2));

            manifold.contactCount = 1;
            // Vec2 n = v2 - center;
            // v2 = B->u * v2 + b->position;
            // m->contacts[0] = v2;
            // n = B->u * n;
            // n.Normalize( );
            // m->normal = n;
            Vector2f n = Vector2f.sub(v2, centerA);
            B.rotateMatrix.mul(n, n);
            n.normalize();
            manifold.normal.set(n);
            B.rotateMatrix.mul(v2, v2);
            v2.add(B.body.position);
            manifold.contacts[0].set(v2);
        } else {
            // Ближе к лицевой стороне
            Vector2f n = new Vector2f(B.normals[indexFaceNormal]);

            manifold.penetration = A.radius - (float) Math.sqrt(realProjection.lengthWithoutSqrt());

            manifold.contactCount = 1;
            // n = B->u * n;
            // m->normal = -n;
            // m->contacts[0] = m->normal * A->radius + a->position;
            B.rotateMatrix.mul(n, n);
            n.negative();
            manifold.normal.set(n);
            manifold.contacts[0].set(manifold.normal);
            manifold.contacts[0].mul(A.radius);
            manifold.contacts[0].add(A.body.position);
        }
    }
}
