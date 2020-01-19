package com.introfog.pie.core;

import com.introfog.pie.core.math.Vector2f;

public class AABB{ //Axis Aligned Bounding Box
	public Vector2f min;
	public Vector2f max;
	
	
	public AABB (){
		min = new Vector2f ();
		max = new Vector2f ();
	}
	
	public static boolean isIntersected (AABB a, AABB b){
		// Выходим без пересечения, потому что найдена разделяющая ось
		if(a.max.x < b.min.x || a.min.x > b.max.x){
			return false;
		}
		if(a.max.y < b.min.y || a.min.y > b.max.y){
			return false;
		}
		
		// Разделяющая ось не найдена, поэтому существует по крайней мере одна пересекающая ось
		return true;
	}
}
