package model;

/**
 * This class represent a force vector
 */
import java.awt.Point;
import java.awt.geom.Point2D;

public class ForceField {
	//ATTRIBUTES/FIELDS
	public Point2D vector;
	
	
	//CONSTRUCTORS
	public ForceField(Point2D vector){
		this.vector = vector;	
	}
	public ForceField(double x, double y){
		this(new Point2D.Double(x, y));
	}
	public ForceField(){
		this(0,0);
	}
	
	//METHODES
	public void changeVector(Point v){
		setVector(
				vector.getX() + v.getX(),
				vector.getY() + v.getY()
				);
	}
	
	
	//GETTERS ANS SETTERS
	public Point2D getVector() {
		return vector;
	}
	public void setVector(Point2D vector) {
		this.vector = vector;
	}
	
	public void setVector(double x, double y) {
		this.vector.setLocation(x,y);
	}

}
