package com.introfog.pie.core.shape;

import com.introfog.pie.core.math.Vector2f;

// Axis Aligned Bounding Box
public class AABB {
    public Vector2f min;
    public Vector2f max;


    public AABB() {
        min = new Vector2f();
        max = new Vector2f();
    }

    public static boolean isIntersected(AABB a, AABB b) {
        // Выходим без пересечения, потому что найдена разделяющая ось
        if (a.max.x < b.min.x || a.min.x > b.max.x) {
            return false;
        }
        return !(a.max.y < b.min.y) && !(a.min.y > b.max.y);

        // Разделяющая ось не найдена, поэтому существует по крайней мере одна пересекающая ось
    }
}
