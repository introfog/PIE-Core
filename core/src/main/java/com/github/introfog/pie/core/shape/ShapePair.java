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
package com.github.introfog.pie.core.shape;

import com.github.introfog.pie.core.PieExceptionMessage;
import java.util.Objects;

/**
 * Utility class for storing a pair of shapes. Used to store pairs of shapes that possibly collide or collide.
 */
public class ShapePair {
    /**
     * The shape of the pair with the smaller hash code.
     */
    private final IShape first;

    /**
     * The shape of the pair with the bigger hash code.
     */
    private final IShape second;

    /**
     * Instantiates a new {@link ShapePair} instance based on two different instance of {@link IShape} class.
     *
     * The {@link #first} variable always stores a shape with a smaller hash code, this is necessary for
     * the comparison of a pair of shapes and calculation of the hash code of the pair independent
     * of collision search method (that is, which shape is the first and which is the second).
     *
     * @param first the shape
     * @param second the shape
     *
     * @throws IllegalArgumentException if the same shapes passed to constructor
     */
    public ShapePair(IShape first, IShape second) {
        if (first == second) {
            throw new IllegalArgumentException(PieExceptionMessage.SAME_SHAPES_PASSED_TO_SHAPE_PAIR_CONSTRUCTOR);
        }
        if (first.hashCode() < second.hashCode()) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShapePair shapePair = (ShapePair) o;
        return first.equals(shapePair.first) &&
                second.equals(shapePair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * Gets the first shape in the pair (which has a smaller hash code).
     *
     * @return the first shape in pair
     */
    public IShape getFirst() {
        return first;
    }

    /**
     * Gets the second shape in the pair (which has a biggest hash code).
     *
     * @return the second shape in pair
     */
    public IShape getSecond() {
        return second;
    }
}
