package model;

/**
 * This is an interface representing a rigid physicObject that can intefered in a physic processus
 */
import java.awt.geom.Point2D;
import java.util.List;


public interface PhysicObject {
	
	public double getWeight();
	public void setPosition(int x, int y);
	
	public List<Point2D> getForces();
}
