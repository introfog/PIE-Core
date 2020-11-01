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
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.shape.Aabb;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.HashSet;
import java.util.Set;

/**
 * The class is a brute force method that iterates over all possible pairs of shapes and checks to see if their Aabbs are intersected.
 *
 * <p>
 * This method is effective for a small number of shapes (up to 100).
 *
 * @see AbstractBroadPhase
 */
public class BruteForceMethod extends AbstractBroadPhase {
    @Override
    public BruteForceMethod newInstance() {
        BruteForceMethod bruteForceMethod = new BruteForceMethod();
        bruteForceMethod.setShapes(shapes);
        return bruteForceMethod;
    }

    @Override
    protected Set<ShapePair> domesticCalculateAabbCollisions() {
        return BruteForceMethod.calculateAabbCollisionsWithoutAabbUpdating(shapes);
    }

    /**
     * A helper method for calculating the shape Aabb collisions which is used by the {@link BruteForceMethod}
     * (in fact, this method is) and in testing to obtain a known correct result and compare it with other methods.
     *
     * @param shapes the shape list
     * @return the {@link ShapePair} list in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    public static Set<ShapePair> calculateAabbCollisionsWithoutAabbUpdating(Set<IShape> shapes) {
        IShape a;
        IShape b;
        Set<ShapePair> collisionsSet = new HashSet<>();

        IShape[] arrayShapes = shapes.toArray(new IShape[]{});
        for (int i = 0; i < arrayShapes.length; i++) {
            for (int j = i + 1; j < arrayShapes.length; j++) {
                a = arrayShapes[i];
                b = arrayShapes[j];

                if (Aabb.isIntersected(a.aabb, b.aabb)) {
                    collisionsSet.add(new ShapePair(a, b));
                }
            }
        }

        return collisionsSet;
    }
}
