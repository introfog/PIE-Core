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
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;

import java.util.List;

public class AddShapesAction extends CallCountAction {
    private final float circleDiameter;
    private final float offsetValue;
    private final Vector2f startPoint;
    private final boolean isHorizontalAdder;

    public AddShapesAction(float circleDiameter, float offsetValue, Vector2f startPoint, boolean isHorizontalAdder) {
        this.circleDiameter = circleDiameter;
        this.offsetValue = offsetValue;
        this.startPoint = startPoint;
        this.isHorizontalAdder = isHorizontalAdder;
    }

    @Override
    protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
        for (int i = 0; i < 10; i++) {
            Circle circle;
            if (isHorizontalAdder) {
                circle = new Circle(circleDiameter / 2, startPoint.x - callCounter * offsetValue,
                        startPoint.y + i * (circleDiameter - 1), MathPie.STATIC_BODY_DENSITY, 0f);
            } else {
                circle = new Circle(circleDiameter / 2, startPoint.x + i * (circleDiameter - 1),
                        startPoint.y + callCounter * offsetValue, MathPie.STATIC_BODY_DENSITY, 0f);
            }
            methods.forEach(method -> method.addShape(circle));
            methodShapes.add(circle);
        }
    }
}
