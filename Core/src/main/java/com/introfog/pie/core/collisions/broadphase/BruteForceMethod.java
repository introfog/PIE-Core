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
package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

public class BruteForceMethod extends AbstractBroadPhase {
    @Override
    public List<ShapePair> insideCollisionCalculating() {
        IShape a;
        IShape b;
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                a = shapes.get(i);
                b = shapes.get(j);

                if (AABB.isIntersected(a.aabb, b.aabb)) {
                    possibleCollisionList.add(new ShapePair(a, b));
                }
            }
        }

        return possibleCollisionList;
    }
}
