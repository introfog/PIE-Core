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

import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBroadPhase {
    protected List<IShape> shapes;

    public AbstractBroadPhase() {
        shapes = new ArrayList<>();
    }

    public void setShapes(List<IShape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    public void addShape(IShape shape){
        shapes.add(shape);
    }

    public final List<ShapePair> calculateAabbCollision() {
        shapes.forEach(IShape::computeAABB);
        return domesticAabbCollisionCalculating();
    }

    protected abstract List<ShapePair> domesticAabbCollisionCalculating();
}
