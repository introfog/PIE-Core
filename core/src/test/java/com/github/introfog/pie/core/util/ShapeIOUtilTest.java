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
package com.github.introfog.pie.core.util;

import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ShapeIOUtilTest extends PieTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/core/util/ShapeIOUtilTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/github/introfog/pie/core/util/ShapeIOUtilTest/";

    @BeforeClass
    public static void createDestinationFolder() {
        PieTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void writeShapesTest() throws IOException {
        String destPath = DESTINATION_FOLDER + "writeShapesTest.pie";
        String cmpPath = SOURCE_FOLDER + "cmp_writeShapesTest.pie";

        Set<IShape> shapes = new LinkedHashSet<>();
        shapes.add(new Circle(10, 1, 2, 0.23f, 0.124f));
        shapes.add(Polygon.generateRectangle(20, 23, 15, 17, 0.17f, 1.23f));
        shapes.add(new Circle(2.456467f, 5, 2, 0.23f, 0.124f));
        shapes.add(Polygon.generateRectangle(1, 0.456f, 15, 17, 0, 1.23f));

        ShapeIOUtil.writeShapesToFile(shapes, destPath);
        // Replacement is necessary to avoid different result on Windows and Linux
        Assert.assertEquals(Files.readString(Paths.get(destPath)).replaceAll("[\n\r]", ""),
                Files.readString(Paths.get(cmpPath)).replaceAll("[\n\r]", ""));
    }

    @Test
    public void readShapesTest() throws IOException {
        String sourcePath = SOURCE_FOLDER + "readShapesTest.pie";

        IShape[] shapes = ShapeIOUtil.readShapesFromFile(sourcePath).toArray(new IShape[]{});

        Circle circle = new Circle(10, 1, 2, 0.23f, 0.124f);
        Circle actCircle = (Circle) (shapes[0] instanceof Circle ? shapes[0] : shapes[1]);
        Assert.assertEquals(circle.getRadius(), actCircle.getRadius(), PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(circle.getBody(), actCircle.getBody());

        Polygon polygon = Polygon.generateRectangle(20, 23, 15, 17, 0.17f, 1.23f);
        Polygon actPolygon = (Polygon) (shapes[0] instanceof Polygon ? shapes[0] : shapes[1]);
        Assert.assertEquals(polygon.getVertices().length, actPolygon.getVertices().length);
        Assert.assertArrayEquals(polygon.getVertices(), actPolygon.getVertices());
        Assert.assertEquals(polygon.getBody(), actPolygon.getBody());
    }

    @Test
    public void convertStringToShapeTest() {
        String strCircle = "Circle;10.0;1.0;2.0;0.23123;0.1";
        Circle circle = new Circle(10, 1, 2, 0.23123f, 0.1f);
        Circle actCircle = (Circle) ShapeIOUtil.convertStringToShape(strCircle);
        Assert.assertEquals(circle.getRadius(), actCircle.getRadius(), PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(circle.getBody(), actCircle.getBody());

        String strPolygon = "Polygon;4;7.5;-8.5;7.5;8.5;-7.5;8.5;-7.5;-8.5;20.0;23.0;0.17;1.23";
        Polygon polygon = Polygon.generateRectangle(20, 23, 15, 17, 0.17f, 1.23f);
        Polygon actPolygon = (Polygon) ShapeIOUtil.convertStringToShape(strPolygon);
        Assert.assertEquals(polygon.getVertices().length, actPolygon.getVertices().length);
        Assert.assertArrayEquals(polygon.getVertices(), actPolygon.getVertices());
        Assert.assertEquals(polygon.getBody(), actPolygon.getBody());
    }

    @Test
    public void convertShapeToStringTest() {
        String expectCircle = "Circle;10.0;1.0;2.0;0.23123;0.1\n";
        Circle circle = new Circle(10, 1, 2, 0.23123f, 0.1f);
        Assert.assertEquals(expectCircle, ShapeIOUtil.convertShapeToString(circle));

        String expectPolygon = "Polygon;4;7.5;-8.5;7.5;8.5;-7.5;8.5;-7.5;-8.5;20.0;23.0;0.17;1.23\n";
        Polygon polygon = Polygon.generateRectangle(20, 23, 15, 17, 0.17f, 1.23f);
        Assert.assertEquals(expectPolygon, ShapeIOUtil.convertShapeToString(polygon));
    }
}
