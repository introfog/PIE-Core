package com.introfog.pie.core.util;

import com.introfog.pie.core.math.Vector2f;
import com.introfog.pie.core.shape.Circle;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.shape.IShape.Type;
import com.introfog.pie.core.shape.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

public class ShapeIOUtil {
    public static List<IShape> readShapesFromFile(String path) throws IOException {
        String string = new String(Files.readAllBytes(Paths.get(path)));
        BufferedReader reader = new BufferedReader(new StringReader(string));
        List<IShape> shapes = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            shapes.add(convertStringToShape(line));
        }
        return shapes;
    }

    public static void writeShapesToFile(List<IShape> shapes, String path) throws IOException {
        StringWriter writer = new StringWriter();
        shapes.forEach(shape -> writer.write(convertShapeToString(shape)));
        writer.flush();
        writer.close();

        Files.write(Paths.get(path), writer.toString().getBytes());
    }

    public static void writeShapePairsToFile(List<ShapePair> pairs, String path) throws IOException {
        StringWriter writer = new StringWriter();
        pairs.forEach(pair -> {
            writer.write(convertShapeToString(pair.first));
            writer.write(convertShapeToString(pair.second));
        });
        writer.flush();
        writer.close();

        Files.write(Paths.get(path), writer.toString().getBytes());
    }

    private static IShape convertStringToShape(String string) {
        IShape shape = null;

        String[] strings = string.split(";");
        int size = strings.length;

        float restitution = Float.parseFloat(strings[size - 1]);
        float density = Float.parseFloat(strings[size - 2]);
        float centerY = Float.parseFloat(strings[size - 3]);
        float centerX = Float.parseFloat(strings[size - 4]);

        if (strings[0].equals(Type.circle.toString())) {
            float radius = Float.parseFloat(strings[1]);
            shape = new Circle(radius, centerX, centerY, density, restitution);
        } else if (strings[0].equals(Type.polygon.toString())) {
            int vertexCount = Integer.parseInt(strings[1]);
            Vector2f[] vertices = new Vector2f[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                vertices[i] = new Vector2f(Float.parseFloat(strings[2 + i * 2]), Float.parseFloat(strings[3 + i * 2]));
            }
            shape = new Polygon(density, restitution, centerX, centerY, vertices);
        }

        return shape;
    }

    private static String convertShapeToString(IShape shape) {
        StringBuilder str = new StringBuilder();
        str.append(shape.type).append(";");
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            str.append(circle.radius).append(";");
        } else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon) shape;
            str.append(polygon.vertexCount).append(";");
            for (Vector2f vec : polygon.vertices) {
                str.append(vec.x).append(";").append(vec.y).append(";");
            }
        }
        str.append(shape.body.position.x).append(";")
                .append(shape.body.position.y).append(";")
                .append(shape.body.density).append(";")
                .append(shape.body.restitution).append("\n");

        return str.toString();
    }
}
