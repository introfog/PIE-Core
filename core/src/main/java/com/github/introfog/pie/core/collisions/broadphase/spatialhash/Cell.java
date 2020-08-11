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
package com.github.introfog.pie.core.collisions.broadphase.spatialhash;

import com.github.introfog.pie.core.math.MathPIE;

/**
 * This class represents a cell when dividing space into cells in the {@link RegularSpatialHashingMethod}
 * method. Cell coordinates are coordinates in the splitted space. The main task of the cell is to generate
 * a unique hash key that is used to fill in the hash table.
 */
public class Cell {
    /** The X cell coordinate. */
    public int x;
    /** The Y cell coordinate. */
    public int y;

    /**
     * Instantiates a new {@link Cell} instance.
     *
     * @param fX the X coordinate which will be converted to the coordinates of the cells
     * @param fY the Y coordinate which will be converted to the coordinates of the cells
     * @param cellSize the cell size
     */
    public Cell(float fX, float fY, int cellSize) {
        this.x = MathPIE.fastFloor(fX / cellSize);
        this.y = MathPIE.fastFloor(fY / cellSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return ((x * 73_856_093) ^ (y * 19_349_663));
    }
}
