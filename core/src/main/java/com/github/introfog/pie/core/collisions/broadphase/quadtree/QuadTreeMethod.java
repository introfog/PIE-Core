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
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

public class QuadTreeMethod extends AbstractBroadPhase {
    private QuadTreeNode root;

    public QuadTreeMethod(AABB rootBoundingBox) {
        root = new QuadTreeNode(rootBoundingBox, null);
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        root.clear();
        root.insertDown(shapes);
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        root.insertDown(shape);
    }

    @Override
    protected List<ShapePair> domesticCalculateAabbCollisions() {
        root.update();

        return new ArrayList<>(root.calculateAllAabbCollision());
    }
}
