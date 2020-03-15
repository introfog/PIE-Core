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
