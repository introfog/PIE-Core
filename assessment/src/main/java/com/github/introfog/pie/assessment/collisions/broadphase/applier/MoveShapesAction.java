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

public class MoveShapesAction extends CallCountAction {
    private final int iterationOneWay;
    private final float offsetValue;
    private final boolean isHorizontalMover;
    private final int oneMovingBodyOfBodies;

    public MoveShapesAction(int iterationOneWay, float offsetValue, boolean isHorizontalMover) {
        this(iterationOneWay, offsetValue, isHorizontalMover, 1);
    }

    public MoveShapesAction(int iterationOneWay, float offsetValue, boolean isHorizontalMover,
            int oneMovingBodyOfBodies) {
        this.iterationOneWay = iterationOneWay;
        this.offsetValue = offsetValue;
        this.isHorizontalMover = isHorizontalMover;
        this.oneMovingBodyOfBodies = oneMovingBodyOfBodies;
    }

    @Override
    protected void domesticApplyAction(List<AbstractBroadPhase> methods, Set<IShape> methodShapes) {
        if (callCounter >= iterationOneWay) {
            callCounter = -iterationOneWay;
        }

        Vector2f offset;
        if (isHorizontalMover) {
            offset = new Vector2f(callCounter > 0 ? offsetValue : -offsetValue, 0);
        } else {
            offset = new Vector2f(0, callCounter >= 0 ? offsetValue : -offsetValue);
        }
        IShape[] arrayMethodShapes = methodShapes.toArray(new IShape[] {});
        for (int i = 0; i < arrayMethodShapes.length; i += oneMovingBodyOfBodies) {
            arrayMethodShapes[i].body.position.add(offset, i % 2 == 0 ? -1 : 1);
        }
    }
}
