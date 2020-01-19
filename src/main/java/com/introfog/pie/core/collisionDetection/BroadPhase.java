package com.introfog.pie.core.collisionDetection;

import com.introfog.pie.core.*;
import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.Vector2f;

import javafx.util.Pair;

import java.util.*;

public class BroadPhase{
	public static int INTERSECTED_COUNTER = 0;
	private LinkedList <Body> bodies;
	
	//for my realisation sweep and prune method
	private LinkedList <Body> xAxisProjection;
	private LinkedList <Body> yAxisProjection;
	private LinkedList <Body> activeList;
	
	//for sweep and prune method (dispersion)
	private int CURRENT_AXIS = 0;
	private Vector2f p = new Vector2f ();
	private Vector2f s = new Vector2f ();
	private Vector2f s2 = new Vector2f ();
	
	//for spatial hashing method
	private float averageMaxBodiesSize = 0f;
	private SpatialHash spatialHash;
	
	
	public BroadPhase (LinkedList <Body> bodies){
		this.bodies = bodies;
		
		xAxisProjection = new LinkedList <> ();
		yAxisProjection = new LinkedList <> ();
		activeList = new LinkedList <> ();
		
		spatialHash = new SpatialHash ();
	}
	
	public void bruteForce (LinkedList <Pair <Body, Body>> mayBeCollision){
		//сложность O(n^2), каждый объект с каждым проверяем
		
		INTERSECTED_COUNTER = 0;
		Body a;
		Body b;
		for (int i = 0; i < bodies.size (); i++){
			for (int j = i + 1; j < bodies.size (); j++){
				a = bodies.get (i);
				b = bodies.get (j);
				
				a.shape.computeAABB ();
				b.shape.computeAABB ();
				
				INTERSECTED_COUNTER++;
				if (AABB.isIntersected (a.shape.aabb, b.shape.aabb)){
					mayBeCollision.add (new Pair <> (a, b));
				}
			}
		}
	}
	
	public void sweepAndPruneMyRealisation (LinkedList <Pair <Body, Body>> mayBeCollision){
		//Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем
		// возможные пересечения по оси Х, а потом bruteForce
		
		INTERSECTED_COUNTER = 0;
		bodies.forEach ((body) -> body.shape.computeAABB ());
		xAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.min.x - b.shape.aabb.min.x));
		//TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)
		
		activeList.add (xAxisProjection.getFirst ());
		float currEnd = xAxisProjection.getFirst ().shape.aabb.max.x;
		
		for (int i = 1; i < xAxisProjection.size (); i++){
			if (xAxisProjection.get (i).shape.aabb.min.x <= currEnd){
				activeList.add (xAxisProjection.get (i));
			}
			else{
				Body first = activeList.removeFirst ();
				activeList.forEach ((body) -> {
					INTERSECTED_COUNTER++;
					if (AABB.isIntersected (first.shape.aabb, body.shape.aabb)){
						mayBeCollision.add (new Pair <> (first, body));
					}
				});
				if (!activeList.isEmpty ()){
					i--;
				}
				else{
					activeList.add (xAxisProjection.get (i));
				}
				currEnd = activeList.getFirst ().shape.aabb.max.x;
			}
		}
		if (!activeList.isEmpty ()){
			int size = activeList.size ();
			for (int i = 0; i < size; i++){
				Body first = activeList.removeFirst ();
				activeList.forEach ((body) -> {
					INTERSECTED_COUNTER++;
					if (AABB.isIntersected (first.shape.aabb, body.shape.aabb)){
						mayBeCollision.add (new Pair <> (first, body));
					}
				});
			}
		}
		
		activeList.clear ();
	}
	
	public void sweepAndPrune (LinkedList <Pair <Body, Body>> mayBeCollision){
		//Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем возможные
		// пересечения по текущей оси, а потом bruteForce. Каждый раз через десперсию выбираем следующую ось
		
		INTERSECTED_COUNTER = 0;
		bodies.forEach ((body) -> body.shape.computeAABB ());
		
		if (CURRENT_AXIS == 0){
			xAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.min.x - b.shape.aabb.min.x));
		}
		else{
			yAxisProjection.sort ((a, b) -> (int) (a.shape.aabb.min.y - b.shape.aabb.min.y));
		}
		//TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)
		
		p.set (0f, 0f);
		s.set (0f, 0f);
		s2.set (0f, 0f);
		float numBodies = bodies.size ();
		
		AABB currAABB;
		for (int i = 0; i < bodies.size (); i++){
			if (CURRENT_AXIS == 0){
				currAABB = xAxisProjection.get (i).shape.aabb;
			}
			else{
				currAABB = yAxisProjection.get (i).shape.aabb;
			}
			
			p.x = (currAABB.min.x / 2f + currAABB.max.x / 2f) / numBodies;
			p.y = (currAABB.min.y / 2f + currAABB.max.y / 2f) / numBodies;
			
			s.add (p);
			p.x *= p.x * numBodies;
			p.y *= p.y * numBodies;
			s2.add (p);
			
			for (int j = i + 1; j < bodies.size (); j++){
				if (CURRENT_AXIS == 0 && xAxisProjection.get (j).shape.aabb.min.x > currAABB.max.x){
					break;
				}
				else if (yAxisProjection.get (j).shape.aabb.min.y > currAABB.max.y){
					break;
				}
				
				INTERSECTED_COUNTER++;
				if (CURRENT_AXIS == 0 && AABB.isIntersected (xAxisProjection.get (j).shape.aabb, currAABB)){
					mayBeCollision.add (new Pair <> (xAxisProjection.get (j), xAxisProjection.get (i)));
				}
				else if (AABB.isIntersected (yAxisProjection.get (j).shape.aabb, currAABB)){
					mayBeCollision.add (new Pair <> (yAxisProjection.get (j), yAxisProjection.get (i)));
				}
			}
		}
		
		//с помощью дисперсии выбираем следуюущую ось (ищем ось, по которой координаты объектов больше всего различаются)
		//что бы меньше проверок делать и сводить алогритм к  O(k*n)
		s.x *= s.x;
		s.y *= s.y;
		s2.sub (s);
		CURRENT_AXIS = 0;
		if (s.y > s.x){
			CURRENT_AXIS = 1;
		}
	}
	
	public void spatialHashing (LinkedList <Pair <Body, Body>> mayBeCollision){
		//сложность O(n) если минимальный и максимальный размер объектов не сильно отличаются, но если очень сильно,
		//то сложность близиться к O(n^2)
		
		INTERSECTED_COUNTER = 0;
		spatialHash.setCellSize ((int) averageMaxBodiesSize * 2);
		spatialHash.clear ();
		
		bodies.forEach ((body) -> spatialHash.optimizedInsert (body));
		
		spatialHash.computeCollisions ().forEach ((pair) -> {
			BroadPhase.INTERSECTED_COUNTER++;
			if (AABB.isIntersected (pair.getKey ().shape.aabb, pair.getValue ().shape.aabb)){
				mayBeCollision.add (pair);
			}
		});
	}
	
	public void addBody (Shape shape){
		xAxisProjection.add (shape.body);
		yAxisProjection.add (shape.body);
		
		shape.computeAABB (); //after bodies.add (shape); in class World
		averageMaxBodiesSize *= (bodies.size () - 1);
		averageMaxBodiesSize += Math.max (shape.aabb.max.x - shape.aabb.min.x, shape.aabb.max.y - shape.aabb.min.y);
		averageMaxBodiesSize /= bodies.size ();
	}
}