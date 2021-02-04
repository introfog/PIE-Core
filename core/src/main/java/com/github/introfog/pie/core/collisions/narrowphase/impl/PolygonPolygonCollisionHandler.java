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
package com.github.introfog.pie.core.collisions.narrowphase.impl;

import com.github.introfog.pie.core.Context;
import com.github.introfog.pie.core.PieExceptionMessage;
import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.collisions.narrowphase.IShapeCollisionHandler;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.ShapeType;

/**
 * Class is used to handle possible collision between two {@link Polygon}.
 */
public class PolygonPolygonCollisionHandler implements IShapeCollisionHandler {
    @Override
    public Manifold handleCollision(IShape aShape, IShape bShape, Context context) {
        if (aShape.type != ShapeType.POLYGON || bShape.type != ShapeType.POLYGON) {
            throw new IllegalArgumentException(PieExceptionMessage.INVALID_SHAPES_TYPE_FOR_NARROW_PHASE_HANDLER);
        }
        Polygon polygonA = (Polygon) aShape;
        Polygon polygonB = (Polygon) bShape;

        Manifold manifold = new Manifold(polygonA, polygonB, context);
        // Looking for a dividing axis with external faces A
        int[] faceA = {0};
        float penetrationA = PolygonPolygonCollisionHandler.findAxisLeastPenetration(faceA, polygonA, polygonB);
        if (penetrationA >= 0.0f) {
            return null;
        }

        // Looking for a dividing axis with external faces В
        int[] faceB = {0};
        float penetrationB = PolygonPolygonCollisionHandler.findAxisLeastPenetration(faceB, polygonB, polygonA);
        if (penetrationB >= 0.0f) {
            return null;
        }

        int referenceIndex;
        // Always indicate from a to b
        boolean flip;

        // Reference
        Polygon refPoly;
        // Incident
        Polygon incPoly;

        // Determine which polygon contains the incident face
        if (MathPie.gt(penetrationA, penetrationB)) {
            refPoly = polygonA;
            incPoly = polygonB;
            referenceIndex = faceA[0];
            flip = false;
        } else {
            refPoly = polygonB;
            incPoly = polygonA;
            referenceIndex = faceB[0];
            flip = true;
        }

        // World space incident face
        Vector2f[] incidentFace = Vector2f.arrayOf(2);

        findIncidentFace(incidentFace, refPoly, incPoly, referenceIndex);

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
        Vector2f v1 = new Vector2f(refPoly.vertices[referenceIndex]);
        referenceIndex = referenceIndex + 1 == refPoly.vertexCount ? 0 : referenceIndex + 1;
        Vector2f v2 = new Vector2f(refPoly.vertices[referenceIndex]);

        // Transform vectors to world coordinates
        // v1 = RefPoly->u * v1 + RefPoly->body->position;
        // v2 = RefPoly->u * v2 + RefPoly->body->position;
        refPoly.rotateMatrix.mul(v1, v1);
        v1.add(refPoly.body.position);
        refPoly.rotateMatrix.mul(v2, v2);
        v2.add(refPoly.body.position);

        // Calculate reference face side normal in world space
        // Vec2 sidePlaneNormal = (v2 - v1);
        // sidePlaneNormal.Normalize( );
        Vector2f sidePlaneNormal = Vector2f.sub(v2, v1);
        sidePlaneNormal.normalize();

        // Orthogonalize
        // Vec2 refFaceNormal( sidePlaneNormal.y, -sidePlaneNormal.x );
        Vector2f refFaceNormal = new Vector2f(sidePlaneNormal.y, -sidePlaneNormal.x);

        // ax + by = c
        // c - distance from source
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
            // Due to floating point error, possible to not have required points
            return null;
        }
        sidePlaneNormal.negative();

        // if (Clip( sidePlaneNormal, posSide, incidentFace ) < 2)
        if (clip(sidePlaneNormal, posSide, incidentFace) < 2) {
            // Due to floating point error, possible to not have required points
            return null;
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

        return manifold;
    }

    private void findIncidentFace(Vector2f[] v, Polygon refPoly, Polygon incPoly, int referenceIndex) {
        Vector2f referenceNormal = new Vector2f(refPoly.normals[referenceIndex]);

        // Calculate normal in incident's frame of reference
        // referenceNormal = RefPoly->u * referenceNormal; // To world space
        // referenceNormal = IncPoly->u.Transpose( ) * referenceNormal; // To
        // incident's model space
        refPoly.rotateMatrix.mul(referenceNormal, referenceNormal); // To world space
        incPoly.rotateMatrix.transposeMul(referenceNormal, referenceNormal);// To
        // incident's
        // model
        // space

        // Find most anti-normal face on incident polygon
        int incidentFace = 0;
        float minDot = Float.MAX_VALUE;
        for (int i = 0; i < incPoly.vertexCount; ++i) {
            // real dot = Dot( referenceNormal, IncPoly->m_normals[i] );
            float dotProduct = Vector2f.dotProduct(referenceNormal, incPoly.normals[i]);

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
        incPoly.rotateMatrix.mul(incPoly.vertices[incidentFace], v[0]);
        v[0].add(incPoly.body.position);
        incidentFace = incidentFace + 1 >= incPoly.vertexCount ? 0 : incidentFace + 1;
        incPoly.rotateMatrix.mul(incPoly.vertices[incidentFace], v[1]);
        v[1].add(incPoly.body.position);
    }

    private int clip(Vector2f n, float c, Vector2f[] face) {
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

    private static float findAxisLeastPenetration(int[] faceIndex, Polygon polygonA, Polygon polygonB) {
        // Looking for the axis of least penetration
        float bestDistance = -Float.MAX_VALUE;
        int bestIndex = 0;

        for (int i = 0; i < polygonA.vertexCount; ++i) {
            // Retrieve a face normal from A
            // Vec2 n = A->m_normals[i];
            // Vec2 nw = A->u * n;
            Vector2f nw = new Vector2f();
            polygonA.rotateMatrix.mul(polygonA.normals[i], nw);

            // Transform face normal into B's model space
            // Mat2 buT = B->u.Transpose( );
            // n = buT * nw;
            Vector2f n = new Vector2f();
            polygonB.rotateMatrix.transposeMul(nw, n);

            // Retrieve support point from B along -n
            // Vector2f s = B->GetSupport( -n );
            n.negative();
            Vector2f s = polygonB.getSupport(n);
            n.negative();

            // Translate the face A to the local coordinates of B
            // Vec2 v = A->m_vertices[i];
            // v = A->u * v + A->body->position;
            // v -= B->body->position;
            // v = buT * v;
            Vector2f v = new Vector2f(polygonA.vertices[i]);
            polygonA.rotateMatrix.mul(v, v);
            v.add(polygonA.body.position);
            v.sub(polygonB.body.position);
            polygonB.rotateMatrix.transposeMul(v, v);

            // Calculate penetration (in local coordinates B)
            // real d = Dot( n, s - v );
            float d = Vector2f.dotProduct(n, Vector2f.sub(s, v));

            // Remember the best penetration
            if (d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        faceIndex[0] = bestIndex;
        return bestDistance;
    }
}
