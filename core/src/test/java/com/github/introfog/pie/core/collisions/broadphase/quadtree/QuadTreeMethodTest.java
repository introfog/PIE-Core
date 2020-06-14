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
package com.github.introfog.pie.core.collisions.broadphase.quadtree;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhaseTest;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;

public class QuadTreeMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new QuadTreeMethod(new AABB(new Vector2f(-2_000_000, -2_000_000), new Vector2f(14_000_000, 14_000_000)));
    }
}
