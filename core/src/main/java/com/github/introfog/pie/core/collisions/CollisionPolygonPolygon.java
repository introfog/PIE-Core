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

import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;

public class CollisionPolygonPolygon implements CollisionCallback {
    public static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon();

    @Override
    public void handleCollision(Manifold manifold) {
        Polygon A = manifold.polygonA;
        Polygon B = manifold.polygonB;

        // Ищем разделительную ось с внешними гранями А
        int[] faceA = {0};
        float penetrationA = findAxisLeastPenetration(faceA, A, B);
        if (penetrationA >= 0.0f) {
            manifold.areBodiesCollision = false;
            return;
        }

        // Ищем разделительную ось с внешними гранями В
        int[] faceB = {0};
        float penetrationB = findAxisLeastPenetration(faceB, B, A);
        if (penetrationB >= 0.0f) {
            manifold.areBodiesCollision = false;
            return;
        }

        int referenceIndex;
        // Всегда указываем от а к b
        boolean flip;

        // Reference (ссылка)
        Polygon RefPoly;
        // Incident (падающий)
        Polygon IncPoly;

        // Определяем, какой полигон содержит опорную грань
        if (MathPIE.gt(penetrationA, penetrationB)) {
            RefPoly = A;
            // Падающий объект B
            IncPoly = B;
            referenceIndex = faceA[0];
            flip = false;
        } else {
            RefPoly = B;
            IncPoly = A;
            referenceIndex = faceB[0];
            flip = true;
        }

        // World space incident face
        Vector2f[] incidentFace = Vector2f.arrayOf(2);

        findIncidentFace(incidentFace, RefPoly, IncPoly, referenceIndex);

        // y
        // ^ .n ^
        // +---c ------posPlane--
        // x < | i |\
        // +---+ c-----negPlane--
        // \ v
        // r
        //
        // r : reference face
        // i : incident poly
        // c : clipped point
        // n : incident normal

        // Setup reference face vertices
        Vector2f v1 = new Vector2f(RefPoly.vertices[referenceIndex]);
        referenceIndex = referenceIndex + 1 == RefPoly.vertexCount ? 0 : referenceIndex + 1;
        Vector2f v2 = new Vector2f(RefPoly.vertices[referenceIndex]);

        // Трансформируем вектора к мировым координатам
        // v1 = RefPoly->u * v1 + RefPoly->body->position;
        // v2 = RefPoly->u * v2 + RefPoly->body->position;
        RefPoly.rotateMatrix.mul(v1, v1);
        v1.add(RefPoly.body.position);
        RefPoly.rotateMatrix.mul(v2, v2);
        v2.add(RefPoly.body.position);

        // Calculate reference face side normal in world space
        // Vec2 sidePlaneNormal = (v2 - v1);
        // sidePlaneNormal.Normalize( );
        Vector2f sidePlaneNormal = Vector2f.sub(v2, v1);
        sidePlaneNormal.normalize();

        // Orthogonalize
        // Vec2 refFaceNormal( sidePlaneNormal.y, -sidePlaneNormal.x );
        Vector2f refFaceNormal = new Vector2f(sidePlaneNormal.y, -sidePlaneNormal.x);

        // ax + by = c
        // c - расстояние от источника
        // real refC = Dot( refFaceNormal, v1 );
        // real negSide = -Dot( sidePlaneNormal, v1 );
        // real posSide = Dot( sidePlaneNormal, v2 );
        float refC = Vector2f.dotProduct(refFaceNormal, v1);
        float negSide = -Vector2f.dotProduct(sidePlaneNormal, v1);
        float posSide = Vector2f.dotProduct(sidePlaneNormal, v2);

        // Clip incident face to reference face side planes
        // if(Clip( -sidePlaneNormal, negSide, incidentFace ) < 2)
        sidePlaneNormal.negative();
        if (clip(sidePlaneNormal, negSide, incidentFace) < 2) {
            manifold.areBodiesCollision = false;
            // Due to floating point error, possible to not have required points
            return;
        }
        sidePlaneNormal.negative();

        // if (Clip( sidePlaneNormal, posSide, incidentFace ) < 2)
        if (clip(sidePlaneNormal, posSide, incidentFace) < 2) {
            manifold.areBodiesCollision = false;
            // Due to floating point error, possible to not have required points
            return;
        }

        // Flip
        manifold.normal.set(refFaceNormal);
        if (flip) {
            manifold.normal.negative();
        }

        // Keep points behind reference face
        // Clipped points behind reference face
        int cp = 0;
        float separation = Vector2f.dotProduct(refFaceNormal, incidentFace[0]) - refC;
        if (separation <= 0.0f) {
            manifold.contacts[cp].set(incidentFace[0]);
            manifold.penetration = -separation;
            ++cp;
        } else {
            manifold.penetration = 0;
        }

        separation = Vector2f.dotProduct(refFaceNormal, incidentFace[1]) - refC;

        if (separation <= 0.0f) {
            manifold.contacts[cp].set(incidentFace[1]);

            manifold.penetration += -separation;
            ++cp;

            // Average penetration
            manifold.penetration /= cp;
        }

        manifold.contactCount = cp;
    }

    public float findAxisLeastPenetration(int[] faceIndex, Polygon A, Polygon B) {
        // Ищем ось наименьшего проникновения
        float bestDistance = -Float.MAX_VALUE;
        int bestIndex = 0;

        for (int i = 0; i < A.vertexCount; ++i) {
            // Retrieve a face normal from A
            // Vec2 n = A->m_normals[i];
            // Vec2 nw = A->u * n;
            Vector2f nw = new Vector2f();
            A.rotateMatrix.mul(A.normals[i], nw);

            // Transform face normal into B's model space
            // Mat2 buT = B->u.Transpose( );
            // n = buT * nw;
            Vector2f n = new Vector2f();
            B.rotateMatrix.transposeMul(nw, n);

            // Retrieve support point from B along -n
            // Vector2f s = B->GetSupport( -n );
            n.negative();
            Vector2f s = B.getSupport(n);
            n.negative();

            // Переводим грань А, в локальные координаты B
            // Vec2 v = A->m_vertices[i];
            // v = A->u * v + A->body->position;
            // v -= B->body->position;
            // v = buT * v;
            Vector2f v = new Vector2f(A.vertices[i]);
            A.rotateMatrix.mul(v, v);
            v.add(A.body.position);
            v.sub(B.body.position);
            B.rotateMatrix.transposeMul(v, v);

            // Высчитываем проникновение (в локальных координатах B)
            // real d = Dot( n, s - v );
            float d = Vector2f.dotProduct(n, Vector2f.sub(s, v));

            // Запоминаем лучшее проникновение
            if (d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        faceIndex[0] = bestIndex;
        return bestDistance;
    }

    public void findIncidentFace(Vector2f[] v, Polygon RefPoly, Polygon IncPoly, int referenceIndex) {
        Vector2f referenceNormal = new Vector2f(RefPoly.normals[referenceIndex]);

        // Calculate normal in incident's frame of reference
        // referenceNormal = RefPoly->u * referenceNormal; // To world space
        // referenceNormal = IncPoly->u.Transpose( ) * referenceNormal; // To
        // incident's model space
        RefPoly.rotateMatrix.mul(referenceNormal, referenceNormal); // To world space
        IncPoly.rotateMatrix.transposeMul(referenceNormal, referenceNormal);// To
        // incident's
        // model
        // space

        // Find most anti-normal face on incident polygon
        int incidentFace = 0;
        float minDot = Float.MAX_VALUE;
        for (int i = 0; i < IncPoly.vertexCount; ++i) {
            // real dot = Dot( referenceNormal, IncPoly->m_normals[i] );
            float dotProduct = Vector2f.dotProduct(referenceNormal, IncPoly.normals[i]);

            if (dotProduct < minDot) {
                minDot = dotProduct;
                incidentFace = i;
            }
        }

        // Assign face vertices for incidentFace
        // v[0] = IncPoly->u * IncPoly->m_vertices[incidentFace] +
        // IncPoly->body->position;
        // incidentFace = incidentFace + 1 >= (int32)IncPoly->m_vertexCount ? 0 :
        // incidentFace + 1;
        // v[1] = IncPoly->u * IncPoly->m_vertices[incidentFace] +
        // IncPoly->body->position;
        IncPoly.rotateMatrix.mul(IncPoly.vertices[incidentFace], v[0]);
        v[0].add(IncPoly.body.position);
        incidentFace = incidentFace + 1 >= IncPoly.vertexCount ? 0 : incidentFace + 1;
        IncPoly.rotateMatrix.mul(IncPoly.vertices[incidentFace], v[1]);
        v[1].add(IncPoly.body.position);
    }

    public int clip(Vector2f n, float c, Vector2f[] face) {
        int sp = 0;
        Vector2f[] out = {new Vector2f(face[0]), new Vector2f(face[1])};

        // Retrieve distances from each endpoint to the line
        // d = ax + by - c
        // real d1 = Dot( n, face[0] ) - c;
        // real d2 = Dot( n, face[1] ) - c;
        float d1 = Vector2f.dotProduct(n, face[0]) - c;
        float d2 = Vector2f.dotProduct(n, face[1]) - c;

        // If negative (behind plane) clip
        // if(d1 <= 0.0f) out[sp++] = face[0];
        // if(d2 <= 0.0f) out[sp++] = face[1];
        if (d1 <= 0.0f) {
            out[sp++].set(face[0]);
        }
        if (d2 <= 0.0f) {
            out[sp++].set(face[1]);
        }

        // If the points are on different sides of the plane
        if (d1 * d2 < 0.0f) // less than to ignore -0.0f
        {
            // Push intersection point
            // real alpha = d1 / (d1 - d2);
            // out[sp] = face[0] + alpha * (face[1] - face[0]);
            // ++sp;

            float alpha = d1 / (d1 - d2);
            out[sp] = Vector2f.sub(face[1], face[0]);
            out[sp].mul(alpha);
            out[sp].add(face[0]);
            sp++;
        }

        // Assign our new converted values
        face[0] = out[0];
        face[1] = out[1];

        return sp;
    }
}
