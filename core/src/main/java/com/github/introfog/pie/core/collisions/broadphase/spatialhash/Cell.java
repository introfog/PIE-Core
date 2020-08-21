package com.github.introfog.pie.core.collisions.broadphase.spatialhash;

import com.github.introfog.pie.core.math.MathPIE;

public class Cell {
    public int x;
    public int y;

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
