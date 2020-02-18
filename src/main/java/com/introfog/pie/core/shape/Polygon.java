package com.introfog.pie.core.shape;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.math.Vector2f;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Polygon extends IShape {
    @JsonProperty
    public int vertexCount;
    @JsonProperty
    public Vector2f[] vertices = Vector2f.arrayOf(MathPIE.MAX_POLY_VERTEX_COUNT);
    @JsonProperty
    public Vector2f[] normals = Vector2f.arrayOf(MathPIE.MAX_POLY_VERTEX_COUNT);

    @JsonIgnore
    public Vector2f tmpV = new Vector2f();
    @JsonIgnore
    public Vector2f tmpV2 = new Vector2f();

    public Polygon() {
        type = Type.polygon;
    }

    // TODO поиск минимальной выпуклой оболочки (Джарвис) работает за O(n*h) где h-кол-во вершин в МВО
    public Polygon(float density, float restitution, float centreX, float centreY, Vector2f... vertices) {
        body = new Body(centreX, centreY, density, restitution);

        // Алгоритм Джарвиса построения минимальной выпуклой оболочки
        // находим самую нижнюю и правую координату, она станет стартовой точкой,
        // и точно принадлежит МВО (мин. выпукл. оболочке)
        tmpV.set(vertices[0]);
        int rightMost = -1;
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].x > tmpV.x) {
                tmpV.set(vertices[i]);
                rightMost = i;
            } else if (tmpV.x == vertices[i].x && tmpV.y > vertices[i].y) {
                tmpV.set(vertices[i]);
                rightMost = i;
            }
        }

        int[] hull = new int[MathPIE.MAX_POLY_VERTEX_COUNT];
        int outCount = 0;
        int indexHull = rightMost;

        for (; ; ) {
            hull[outCount] = indexHull;

            // Ищем вершину, с самым большим углом против часовой стрелки, от текущей
            // (считаем угол через векторное произведение)
            int nextHullIndex = 0;
            for (int i = 1; i < vertices.length; ++i) {
                // Пропускаем одинаковые вершины, т.к. нам нужны уникальные вершины в треугольнике
                if (nextHullIndex == indexHull) {
                    nextHullIndex = i;
                    continue;
                }
                // Перебираем все треугольника, ища самую крайнюю вершину
                tmpV.set(vertices[nextHullIndex]);
                tmpV.sub(vertices[hull[outCount]]);

                tmpV2.set(vertices[i]);
                tmpV2.sub(vertices[hull[outCount]]);
                float c = Vector2f.crossProduct(tmpV, tmpV2);
                if (c < 0.0f) {
                    nextHullIndex = i;
                }
                // Если векторное произведение равно 0, то они лежат на одной прямой, и нам нужна вершина
                // самая удаленная от заданой
                if (c == 0.0f && tmpV2.lengthWithoutSqrt() > tmpV.lengthWithoutSqrt()) {
                    nextHullIndex = i;
                }
            }

            outCount++;
            indexHull = nextHullIndex;

            // Когда дошли до стартойо вершины, алгоритм Джарвиса закончен
            if (nextHullIndex == rightMost) {
                vertexCount = outCount;
                break;
            }
        }

        for (int i = 0; i < vertexCount; i++) {
            this.vertices[i].set(vertices[hull[i]]);
        }

        for (int i = 0; i < vertexCount; i++) {
            tmpV.set(this.vertices[(i + 1) % vertexCount]);
            tmpV.sub(this.vertices[i]);

            // Берем правую нормаль
            normals[i].set(tmpV.y, -tmpV.x);
            normals[i].normalize();
        }

        computeMass();
        computeAABB();

        type = Type.polygon;
    }

    public static Polygon generateRectangle(float centerX, float centerY, float width, float height, float density,
            float restitution) {
        Vector2f[] vertices = new Vector2f[4];
        vertices[0] = new Vector2f(-width / 2f, -height / 2f);
        vertices[1] = new Vector2f(width / 2f, -height / 2f);
        vertices[2] = new Vector2f(width / 2f, height / 2f);
        vertices[3] = new Vector2f(-width / 2f, height / 2f);
        return new Polygon(density, restitution, centerX, centerY, vertices);
    }

    public Vector2f getSupport(Vector2f dir) {
        // Ищем самую удаленную точку в заданном направлении
        float bestProjection = -Float.MAX_VALUE;
        Vector2f bestVertex = new Vector2f();

        for (int i = 0; i < vertexCount; ++i) {
            Vector2f v = vertices[i];
            float projection = Vector2f.dotProduct(v, dir);

            if (projection > bestProjection) {
                bestVertex.set(v);
                bestProjection = projection;
            }
        }

        return bestVertex;
    }

    @Override
    public void computeAABB() {
        aabb.min.x = Float.MAX_VALUE;
        aabb.min.y = Float.MAX_VALUE;

        aabb.max.x = -Float.MAX_VALUE;
        aabb.max.y = -Float.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            tmpV.set(vertices[i]);
            rotateMatrix.mul(tmpV, tmpV);
            if (tmpV.x < aabb.min.x) {
                aabb.min.x = tmpV.x;
            }
            if (tmpV.y < aabb.min.y) {
                aabb.min.y = tmpV.y;
            }
            if (tmpV.x > aabb.max.x) {
                aabb.max.x = tmpV.x;
            }
            if (tmpV.y > aabb.max.y) {
                aabb.max.y = tmpV.y;
            }
        }

        aabb.min.add(body.position);
        aabb.max.add(body.position);
    }

    @Override
    protected void computeMass() {
        float area = 0f;
        float I = 0f;
        final float k_inv3 = 1f / 3f;

        for (int i = 0; i < vertexCount; ++i) {
            // Разбиваем выпуклый многоугольник на треугольники, у которых одна из точек (0, 0)
            Vector2f p1 = vertices[i];
            Vector2f p2 = vertices[(i + 1) % vertexCount];

            float D = Vector2f.crossProduct(p1, p2);
            float triangleArea = 0.5f * D;

            area += triangleArea;

            float intX2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x;
            float intY2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y;
            I += (0.25f * k_inv3 * D) * (intX2 + intY2);
        }

        float mass = body.density * area;
        body.invertMass = (mass != 0f) ? 1f / mass : 0f;
        float inertia = I * body.density;
        body.invertInertia = (inertia != 0f) ? 1f / inertia : 0f;
    }
}