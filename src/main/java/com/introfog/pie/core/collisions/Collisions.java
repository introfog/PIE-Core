package com.introfog.pie.core.collisions;

public class Collisions {
    public static CollisionCallback[][] table = {
            {CollisionCircleCircle.instance, CollisionCirclePolygon.instance},
            {CollisionPolygonCircle.instance, CollisionPolygonPolygon.instance}
    };
}