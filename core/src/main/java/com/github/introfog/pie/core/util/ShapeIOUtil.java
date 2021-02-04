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

import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * utility class for input and output {@link IShape} from\to string or file.
 */
public final class ShapeIOUtil {
    private ShapeIOUtil() {
        // Empty private constructor
    }

    /**
     * Reads {@link IShape} from file to set of shapes.
     *
     * @param path the path to file with shapes
     * @return the set of read shapes
     * @throws IOException if an I/O error occurs
     * @see #writeShapesToFile(Set, String) 
     */
    public static Set<IShape> readShapesFromFile(String path) throws IOException {
        String string = new String(Files.readAllBytes(Paths.get(path)));
        BufferedReader reader = new BufferedReader(new StringReader(string));
        Set<IShape> shapes = new HashSet<>();
        String line;
        while ((line = reader.readLine()) != null) {
            shapes.add(convertStringToShape(line));
        }
        return shapes;
    }

    /**
     * Writes set of {@link IShape} to file.
     *
     * @param shapes the set of shapes to be written
     * @param path the path to file in which set of shapes will written
     * @throws IOException if an I/O error occurs
     * @see #readShapesFromFile(String) 
     */
    public static void writeShapesToFile(Set<IShape> shapes, String path) throws IOException {
        StringWriter writer = new StringWriter();
        shapes.forEach(shape -> writer.write(convertShapeToString(shape)));
        writer.flush();
        writer.close();

        Files.write(Paths.get(path), writer.toString().getBytes());
    }

    /**
     * Converts the string representation of shape to {@link IShape} instance.
     * 
     * @param string the string representation of shape
     * @return the {@link IShape} instance from string
     * @see #convertShapeToString(IShape) 
     */
    public static IShape convertStringToShape(String string) {
        IShape shape = null;

        String[] strings = string.split(";");
        int size = strings.length;

        float restitution = Float.parseFloat(strings[size - 1]);
        float density = Float.parseFloat(strings[size - 2]);
        float centerY = Float.parseFloat(strings[size - 3]);
        float centerX = Float.parseFloat(strings[size - 4]);


        if (strings[0].equals(Circle.class.getSimpleName())) {
            float radius = Float.parseFloat(strings[1]);
            shape = new Circle(radius, centerX, centerY, density, restitution);
        } else if (strings[0].equals(Polygon.class.getSimpleName())) {
            int vertexCount = Integer.parseInt(strings[1]);
            List<Vector2f> vertices = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                vertices.add(new Vector2f(Float.parseFloat(strings[2 + i * 2]), Float.parseFloat(strings[3 + i * 2])));
            }
            shape = new Polygon(density, restitution, centerX, centerY, vertices);
        }
        // TODO add log message if shape doesn't created

        return shape;
    }

    /**
     * Converts {@link IShape} to string.
     * 
     * @param shape the shape for representation
     * @return the string representation of passed shape
     * @see #convertStringToShape(String) 
     */
    public static String convertShapeToString(IShape shape) {
        StringBuilder str = new StringBuilder();
        str.append(shape.getClass().getSimpleName()).append(";");
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            str.append(circle.getRadius()).append(";");
        } else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            str.append(polygon.getVertexCount()).append(";");
            for (Vector2f vec : polygon.getVertices()) {
                str.append(vec.x).append(";").append(vec.y).append(";");
            }
        }
        str.append(shape.getBody().position.x).append(";")
                .append(shape.getBody().position.y).append(";")
                .append(shape.getBody().density).append(";")
                .append(shape.getBody().restitution).append("\n");

        return str.toString();
    }
}
