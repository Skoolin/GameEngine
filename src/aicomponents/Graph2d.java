package aicomponents;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

/**
 * 
 * contains a Bézier path (a Number of Bézier curves), in an Array of 2d Vectors
 * 
 * @author skolin
 *
 */

public class Graph2d {

	/*
	 * Structure: 0 = node 1 (Curve 1) 1 = cont 1 (Curve 1) 2 = cont 2 (Curve 1)
	 * 3 = node 2 (Curve 1 + 2) 4 = cont 3 (Curve 2) 5 = cont 4 (Curve 2) 6 =
	 * node 3 (Curve 2 + 3) . . . 3n-2 node n (Curve n - 1)
	 */
	private List<Vector2f> points;

	public Graph2d(List<Vector2f> nodes, float scale) {
		if (nodes.size() < 3) {
			points = new ArrayList<Vector2f>();
			for (Vector2f point: nodes) {
				points.add(point);
			}
			return;
		}
	    for (int i = 0; i < nodes.size(); i++)
	    {
	        if (i == 0) // is first
	        {
	            Vector2f p1 = nodes.get(i);
	            Vector2f p2 = nodes.get(i + 1);                
	 
	            Vector2f tangent = Vector2f.sub(p2, p1, null);
	            Vector2f q1 = Vector2f.add(p1, (Vector2f) tangent.scale(scale), null);
	 
	            points.add(p1);
	            points.add(q1);
	        }
	        else if (i == nodes.size() - 1) //last
	        {
	            Vector2f p0 = nodes.get(i-1);
	            Vector2f p1 = nodes.get(i);
	            Vector2f tangent = Vector2f.sub(p1, p0, null);
	            Vector2f q0 = Vector2f.sub(p1, (Vector2f) tangent.scale(scale), null);
	 
	            points.add(q0);
	            points.add(p1);
	        }
	        else
	        {
	            Vector2f p0 = nodes.get(i-1);
	            Vector2f p1 = nodes.get(i);
	            Vector2f p2 = nodes.get(i+1);
	            Vector2f tangent = Vector2f.sub(p2, p0, null);
	            tangent.normalise();
	            Vector2f q0 = Vector2f.sub(p1, (Vector2f) tangent.scale(scale), null);
	            q0.scale((float) (Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * p1.y - p0.y)));
	            Vector2f q1 = Vector2f.add(p1, (Vector2f) tangent.scale(scale), null);
	            q1.scale((float) Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)));
	 
	            points.add(q0);
	            points.add(p1);
	            points.add(q1);
	        }
	    }
	}
	
	public Vector2f getPosition(float progression) {
		
		
		
		
		return new Vector2f();
	}
}
