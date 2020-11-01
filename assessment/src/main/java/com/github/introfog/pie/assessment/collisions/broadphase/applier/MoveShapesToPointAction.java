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
package com.github.introfog.pie.assessment.collisions.broadphase.applier;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.IShape;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveShapesToPointAction extends CallCountAction {
    private final int iterationOneWay;
    private final float offsetValue;

    public MoveShapesToPointAction(int iterationOneWay, float offsetValue) {
        this.iterationOneWay = iterationOneWay;
        this.offsetValue = offsetValue;
    }

    @Override
    protected void domesticApplyAction(List<AbstractBroadPhase> methods, Set<IShape> methodShapes) {
        if (callCounter >= iterationOneWay) {
            callCounter = -iterationOneWay;
        }

        Vector2f center = methodShapes.stream().map(shape -> shape.body.position).collect(Collectors.toList()).stream().
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
            offset.mul(callCounter > 0 ? offsetValue : -offsetValue);
            shape.body.position.add(offset);
        }
    }
}
