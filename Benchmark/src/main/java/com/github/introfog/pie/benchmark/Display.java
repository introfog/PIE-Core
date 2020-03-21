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
package com.github.introfog.pie.benchmark;

import com.github.introfog.pie.core.Body;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.core.util.ShapePair;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import static com.github.introfog.pie.benchmark.Main.SHAPES_OFFSET_X;
import static com.github.introfog.pie.benchmark.Main.SHAPES_OFFSET_Y;
import static com.github.introfog.pie.benchmark.Main.world;

public class Display extends JPanel implements ActionListener {
    private static final String sourceFolder = ".\\PIE\\Benchmark\\src\\test\\resources\\com\\introfog\\pie\\benchmark\\collisions\\broadphase\\";

    private boolean viewExitingJson;
    private boolean saveJson;
    private int collisionCount;
    private long previousTime;
    private String fileName;

    public Display() throws IOException {
        Timer timer = new Timer(0, this);
        timer.start();

        viewExitingJson = true;
        saveJson = false;

        fileName = "Line\\5x500line_8487collision";
        if (viewExitingJson) {
            downloadExitingShapes();
        } else {
            initializeShapesForTest();
        }
        world.setCollisionSolveIterations(10);
        previousTime = System.nanoTime();
    }

    @Override
    public void paint(Graphics g) {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - previousTime) / 1_000_000_000f;
        previousTime = currentTime;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);

        g.drawString("FPS: " + (int) (1 / deltaTime), 2, 12);
        g.drawString("Bodies: " + world.getShapes().size(), 2, 24);
        g.drawString("Collisions: " + collisionCount, 2, 36);
        g.drawString("Version: 1.0-SNAPSHOT", 2, 48);

        draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private void downloadExitingShapes() throws IOException {
        List<IShape> shapes = ShapeIOUtil.readShapesFromFile(sourceFolder + fileName + ".pie");
        AbstractBroadPhase broadPhase = new BruteForceMethod();
        broadPhase.setShapes(shapes);
        collisionCount = broadPhase.calculateAabbCollision().size();
        world.setShapes(shapes);
    }

    private void initializeShapesForTest() throws IOException {
        float RADIUS = 10;
        final float COUNT_IN_WIDTH = 100;
        final float COUNT_IN_HEIGHT = 100;
        final float RADIUS_OFFSET = 1f;

        float currX = SHAPES_OFFSET_X;
        float currY = SHAPES_OFFSET_Y;
        List<IShape> shapes = new ArrayList<>();

        for (int i = 0; i < COUNT_IN_HEIGHT; i++) {
            for (int j = 0; j < COUNT_IN_WIDTH; j++) {
                RADIUS++;
                shapes.add(new Circle(RADIUS, currX, currY, MathPIE.STATIC_BODY_DENSITY, 0.2f));
                currX += (RADIUS_OFFSET * RADIUS - 1) + ((j % 2 == 0) ? 10 * RADIUS : 0);
            }
            currY += (RADIUS_OFFSET * RADIUS - 1) /*+ ((i % 2 == 0) ? 10 * RADIUS : 0)*/;
            currX = SHAPES_OFFSET_X;
        }

        AbstractBroadPhase broadPhase = new BruteForceMethod();
        broadPhase.setShapes(shapes);
        List<ShapePair> collisions = broadPhase.calculateAabbCollision();
        collisionCount = collisions.size();

        if (saveJson) {
            ShapeIOUtil.writeShapesToFile(shapes, sourceFolder + fileName + "_" + collisionCount + "collision.json");
            ShapeIOUtil.writeShapePairsToFile(collisions, sourceFolder + fileName + "_" + collisionCount + "collision_answer.json");
        }

        world.setShapes(shapes);
    }

    private void draw(Graphics graphics) {
        world.getShapes().forEach((shape) -> {
            Body body = shape.body;
            if (shape instanceof Polygon) {
                Polygon polygon = (Polygon) shape;

                if (Main.ENABLE_DEBUG_DRAW) {
                    drawAABB(graphics, polygon);
                }

                graphics.setColor(Color.BLUE);

                for (int i = 0; i < polygon.vertexCount; i++) {
                    polygon.tmpV.set(polygon.vertices[i]);
                    polygon.rotateMatrix.mul(polygon.tmpV, polygon.tmpV);
                    polygon.tmpV.add(body.position);

                    polygon.tmpV2.set(polygon.vertices[(i + 1) % polygon.vertexCount]);
                    polygon.rotateMatrix.mul(polygon.tmpV2, polygon.tmpV2);
                    polygon.tmpV2.add(body.position);
                    graphics.drawLine((int) polygon.tmpV.x, (int) polygon.tmpV.y, (int) polygon.tmpV2.x,
                            (int) polygon.tmpV2.y);
                }

                graphics.drawLine((int) body.position.x, (int) body.position.y, (int) body.position.x,
                        (int) body.position.y);
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;

                if (Main.ENABLE_DEBUG_DRAW) {
                    drawAABB(graphics, circle);
                }
                graphics.setColor(Color.RED);
                graphics.drawLine((int) body.position.x, (int) body.position.y,
                        (int) body.position.x, (int) body.position.y);
                graphics.drawLine((int) body.position.x, (int) body.position.y,
                        (int) (body.position.x + circle.radius * Math.cos(body.orientation)),
                        (int) (body.position.y + circle.radius * Math.sin(body.orientation)));
                graphics.drawOval((int) (body.position.x - circle.radius), (int) (body.position.y - circle.radius),
                        (int) circle.radius * 2, (int) circle.radius * 2);
            }
        });

        // Рисвание нормалей к точкам касания в коллизии
        if (Main.ENABLE_DEBUG_DRAW) {
            graphics.setColor(Color.GREEN);
            world.getCollisions().forEach((collision) -> {
                for (int i = 0; i < collision.contactCount; i++) {
                    graphics.drawLine((int) collision.contacts[i].x, (int) collision.contacts[i].y,
                            (int) (collision.contacts[i].x + collision.normal.x * 10),
                            (int) (collision.contacts[i].y + collision.normal.y * 10));
                    graphics.drawLine((int) collision.contacts[i].x + 1, (int) collision.contacts[i].y + 1,
                            (int) (collision.contacts[i].x + collision.normal.x * 10 + 1),
                            (int) (collision.contacts[i].y + collision.normal.y * 10 + 1));
                }
            });
        }
    }

    private void drawAABB(Graphics graphics, IShape shape) {
        shape.computeAABB();
        graphics.setColor(Color.GRAY);
        graphics.drawRect((int) shape.aabb.min.x, (int) shape.aabb.min.y,
                (int) (shape.aabb.max.x - shape.aabb.min.x),
                (int) (shape.aabb.max.y - shape.aabb.min.y));
    }
}