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
package com.github.introfog.pie.benchmark.collisions.broadphase.dynamical;

import com.github.introfog.pie.benchmark.collisions.broadphase.AbstractBroadPhaseBenchmarkTest;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BenchmarkTest.class)
public class DynamicSquareShapesBenchmarkTest extends AbstractBroadPhaseBenchmarkTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void mediumSquareSlowMovingTest() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision.pie", PATH_TO_SOURCE_FOLDER,
                10, 100, new double[]{1.0, 1.27, 0.18, 0.14}, new SlowToPointMover());
    }

    private static class SlowToPointMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 30) {
                callCounter = -29;
            }

            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            diameter /= 10;
            Vector2f center = methodShapes.stream().map((shape) -> shape.body.position).collect(Collectors.toList()).stream().
                    reduce((sum, current) -> {sum.add(current); return sum;}).orElse(new Vector2f());
            center.mul(1.0f / methodShapes.size());

            for (IShape shape : methodShapes) {
                float dist = (float) Math.sqrt(Vector2f.distanceWithoutSqrt(center, shape.body.position));
                float cos = 0;
                float sin = 0;
                if (dist != 0) {
                    cos = (shape.body.position.x - center.x) / dist;
                    sin = (shape.body.position.y - center.y) / dist;
                }
                Vector2f offset = new Vector2f(cos, sin);
                offset.mul(callCounter > 0 ? diameter : -diameter);
                shape.body.position.add(offset);
            }
        }
    }

    @Test
    public void mediumSquareQuickMovingTest() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision.pie", PATH_TO_SOURCE_FOLDER,
                10, 100, new double[]{1.0, 0.68, 0.11, 0.09}, new QuickToPointMover());
    }

    private static class QuickToPointMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 30) {
                callCounter = -29;
            }

            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            diameter /= 1;
            Vector2f center = methodShapes.stream().map((shape) -> shape.body.position).collect(Collectors.toList()).stream().
                    reduce((sum, current) -> {sum.add(current); return sum;}).orElse(new Vector2f());
            center.mul(1.0f / methodShapes.size());

            for (IShape shape : methodShapes) {
                float dist = (float) Math.sqrt(Vector2f.distanceWithoutSqrt(center, shape.body.position));
                float cos = 0;
                float sin = 0;
                if (dist != 0) {
                    cos = (shape.body.position.x - center.x) / dist;
                    sin = (shape.body.position.y - center.y) / dist;
                }
                Vector2f offset = new Vector2f(cos, sin);
                offset.mul(callCounter > 0 ? diameter : -diameter);
                shape.body.position.add(offset);
            }
        }
    }
}
