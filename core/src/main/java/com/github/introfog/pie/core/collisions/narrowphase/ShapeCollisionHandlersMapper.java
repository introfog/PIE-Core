package com.github.introfog.pie.core.collisions.narrowphase;

import com.github.introfog.pie.core.collisions.narrowphase.impl.CircleCircleCollisionHandler;
import com.github.introfog.pie.core.collisions.narrowphase.impl.CirclePolygonCollisionHandler;
import com.github.introfog.pie.core.collisions.narrowphase.impl.PolygonPolygonCollisionHandler;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.shape.ShapePair;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that allows to map pair of subclasses of {@link IShape} to current {@link IShapeCollisionHandler}
 * instance to handle collision between subclasses instances.
 */
public class ShapeCollisionHandlersMapper {
    private final Map<Class<? extends IShape>, Map<Class<? extends IShape>, IShapeCollisionHandler>> mapping;

    /**
     * Creates a new {@link ShapeCollisionHandlersMapper} instance with empty handlers map.
     */
    public ShapeCollisionHandlersMapper() {
        mapping = new HashMap<>();
    }

    /**
     * Creates a new {@link ShapeCollisionHandlersMapper} instance based on
     * other mapper instance. Handlers map will be copied from passed instance.
     *
     * @param other the mapper copy of which will be created
     */
    public ShapeCollisionHandlersMapper(ShapeCollisionHandlersMapper other) {
        this.mapping = new HashMap<>();
        this.mapping.putAll(other.mapping);
    }

    /**
     * Creates and gets default handlers mapping.
     *
     * @return the default mapping
     */
    public static ShapeCollisionHandlersMapper createAndGetDefaultMapping() {
        ShapeCollisionHandlersMapper instance = new ShapeCollisionHandlersMapper();
        instance.putMapping(Circle.class, Circle.class, new CircleCircleCollisionHandler());
        instance.putMapping(Circle.class, Polygon.class, new CirclePolygonCollisionHandler());
        instance.putMapping(Polygon.class, Circle.class, new CirclePolygonCollisionHandler());
        instance.putMapping(Polygon.class, Polygon.class, new PolygonPolygonCollisionHandler());

        return instance;
    }

    /**
     * Puts a new handler to the map.
     *
     * @param firstShape the first key class
     * @param secondShape the second key class
     * @param narrowPhaseHandler the handler that maps to the pair of passed classes
     */
    public void putMapping(Class<? extends IShape> firstShape, Class<? extends IShape> secondShape,
            IShapeCollisionHandler narrowPhaseHandler) {
        mapping.putIfAbsent(firstShape, new HashMap<>());
        mapping.get(firstShape).put(secondShape, narrowPhaseHandler);
    }

    /**
     * Gets the handler that maps to a passed shape pair classes.
     *
     * @param shapePair the shape pair
     * @return the handler that maps to the shape pair classes, otherwise null
     */
    public IShapeCollisionHandler getMapping(ShapePair shapePair) {
        return getMapping(shapePair.getFirst().getClass(), shapePair.getSecond().getClass());
    }

    /**
     * Gets the handler that maps to a passed shape pair classes.
     *
     * @param firstShape the first key class
     * @param secondShape the second key class
     * @return the handler that maps to the shape pair classes, otherwise null
     */
    public IShapeCollisionHandler getMapping(Class<? extends IShape> firstShape, Class<? extends IShape> secondShape) {
        Map<Class<? extends IShape>, IShapeCollisionHandler> tempMapping = mapping.get(firstShape);
        if (tempMapping == null) {
            return null;
        } else {
            return tempMapping.get(secondShape);
        }
    }
}
