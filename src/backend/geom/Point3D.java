package backend.geom;

import java.awt.geom.Point2D;
/*
 * A Point in 3D, as we can't use the Java 8 javafx classes 
 *
 * @since 2.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class Point3D {
	/*
	 * x coordinate
	 */
	private double x;
	/*
	 * y coordinate
	 */
	private double y;
	/*
	 * z coordinate
	 */
	private double z;
	
	
	/*
	 * Build a 3D point from coordinates
	 * @param x-coordinate
	 * @param y-coordinate
	 * @param z-coordinate
	 */
	public Point3D(double x, double y, double z){
		this.x =x;
		this.y=y;
		this.z=z;
	}

	/*
	 * Build a 3D point from a 2D-Point and a height
	 * @param a 2D point(x,y)
	 * @param z-coordinate
	 */
	public Point3D(Point2D point2d, double z) {
		this.x = point2d.getX();
		this.y = point2d.getY();
		this.z = z;
	}

	/*
	 * Getter for the x coordinate
	 * @return x coordinate
	 */
	public double getX() {
	return x;
	}

	/*
	 * Getter for the y coordinate
	 * @return y coordinate
	 */
	public double getY() {
	return y;
	}

	/*
	 * Getter for the z coordinate
	 * @return z coordinate
	 */
	public double getZ() {
	return z;
	}
		
	/*
	 * flattens the Point to it's projection on (x,y)
	 * @returns a 2D representation of the point
	 */
	public Point2D flat(){
	return new Point2D.Double(x,y);
	}
	
	/*
	 * calculates the distance between two points, ignoring the height
	 * @param a 3D point
	 * @returns distance in 2D
	 */
	public double flatDist(Point3D point){ 
	return Math.sqrt(Math.pow(point.getX()-this.x,2)+Math.pow(point.getY()-this.y,2));
	}

	/*
	 * calculates the distance between two points, ignoring the height
	 * @param a 2D point
	 * @returns distance in 2D
	 */
	public double flatDist(Point2D point ){
	return Math.sqrt(Math.pow(point.getX()-this.x,2)+Math.pow(point.getY()-this.y,2));
	}
	
	/*
	 * calculates the distance between two points, ignoring the height
	 * @param x coordinate of the other point
	 * @param y coordinate of the other point
	 * @returns distance in 2D
	 */
	public double flatDist(double x,double y){
	return Math.sqrt(Math.pow(x-this.x,2)+Math.pow(y-this.y,2));
	}
	
	/*
	 * calculates the distance between two points, considering the height
	 * @param the other Point 
	 * @returns distance in 3D
	 */
	public double dist(Point3D point){
	return Math.sqrt(Math.pow(point.getX()-this.x,2)+Math.pow(point.getY()-this.y,2)+Math.pow(point.getZ()-this.z,2));
	}
	
	@Override
	public int hashCode(){
		return (int)(x+y*1000);
	}
	
	@Override
	public boolean equals(Object other){
		Point3D otherPoint = (Point3D)other;
		if (this.getX() == otherPoint.getX() && this.getY() == otherPoint.getY()){
			return true;
		}
		return false;
	}
}