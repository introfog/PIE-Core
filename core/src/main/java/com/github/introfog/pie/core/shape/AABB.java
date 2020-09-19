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
package com.github.introfog.pie.core.shape;

import com.github.introfog.pie.core.math.Vector2f;

import java.util.StringJoiner;

// Axis Aligned Bounding Box
public class AABB {
    public Vector2f min;
    public Vector2f max;

    public AABB() {
        min = new Vector2f();
        max = new Vector2f();
    }

    public AABB(Vector2f min, Vector2f max) {
        this.min = min;
        this.max = max;
    }

    public float surfaceArea() {
        return (max.x - min.x) * (max.y - min.y);
    }

    public float deltaSurfaceArea(AABB aabb) {
        return  (Math.max(this.max.x, aabb.max.x) - Math.min(this.min.x, aabb.min.x)) *
                (Math.max(this.max.y, aabb.max.y) - Math.min(this.min.y, aabb.min.y)) - surfaceArea();
    }

    public static AABB union(AABB a, AABB b) {
        AABB result = new AABB();
        result.min.x = Math.min(a.min.x, b.min.x);
        result.min.y = Math.min(a.min.y, b.min.y);

        result.max.x = Math.max(a.max.x, b.max.x);
        result.max.y = Math.max(a.max.y, b.max.y);

        return result;
    }

    public static boolean isIntersected(AABB a, AABB b) {
        // Exit without intersection because a dividing axis is found
        if (a.max.x < b.min.x || a.min.x > b.max.x) {
            return false;
        }
        // No separation axis found, therefore at least one intersecting axis exists
        return !(a.max.y < b.min.y) && !(a.min.y > b.max.y);
    }

    public static boolean isContained(AABB container, AABB content) {
        if (container == content) {
            return true;
        }

        if (content.min.x < container.min.x || content.max.x > container.max.x) {
            return false;
        }

        if (content.min.y < container.min.y || content.max.y > container.max.y) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add("min=" + min)
                .add("max=" + max)
                .toString();
    }
}
