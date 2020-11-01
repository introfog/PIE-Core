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
import com.github.introfog.pie.core.shape.IShape;

import java.util.List;
import java.util.Set;

public class ChangeShapesAction extends CallCountAction {
    private final Set<IShape> firstShapes;
    private final Set<IShape> secondShapes;
    private final int iterationCalls;

    public ChangeShapesAction(Set<IShape> firstShapes, Set<IShape> secondShapes, int iterationCalls) {
        this.firstShapes = firstShapes;
        this.secondShapes = secondShapes;
        this.iterationCalls = iterationCalls;
    }

    @Override
    protected void domesticApplyAction(List<AbstractBroadPhase> methods, Set<IShape> methodShapes) {
        long temp = callCounter % iterationCalls;
        if (temp < (iterationCalls / 2)) {
            methods.forEach(method -> method.setShapes(firstShapes));
            methodShapes.clear();
            methodShapes.addAll(firstShapes);
        } else {
            methods.forEach(method -> method.setShapes(secondShapes));
            methodShapes.clear();
            methodShapes.addAll(secondShapes);
        }
    }
}
