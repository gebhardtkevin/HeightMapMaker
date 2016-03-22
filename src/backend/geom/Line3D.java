package backend.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import backend.lists.SideList;
/*
 * A Line in "2.5D". All operations are calculated for the projection of the lines in 2D, 
 * but the lines store a height for the end-points
 *  
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class Line3D extends Line2D.Double{

	private static final long serialVersionUID = 3846401357530339197L;
	private double z1;
	private double z2;
	private boolean used=false;
	
	public Line3D(Point2D p1, double z1, Point2D p2, double z2){
		super(p1,p2);
		this.z1 = z1;
		this.z2 = z2;
	}

	public Line3D(Point3D p1, Point3D p2){
		super(p1.flat(),p2.flat());
		this.z1 = p1.getZ();
		this.z2 = p2.getZ();
	}
	
	public Line3D(double x1,double y1,double z1,double x2,double y2, double z2){
		super(x1,y1,x2,y2);
		this.z1 = z1;
		this.z2 = z2;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed() {
		this.used = true;
	}
	
	public double lenght(){
	return this.getP1().distance(this.getP2());	
	}
	
	private Point2D[] getCommonPoint(Line3D other){
		Point2D[] points =new Point2D[3];
		Point2D commonPoint=null;
		Point2D dirPoint1=null;
		Point2D dirPoint2=null;
		if (this.getP1().equals(other.getP1())){
			commonPoint = this.getP1();
			dirPoint1 =this.getP2();
			dirPoint2 =other.getP2();
		}
		if (this.getP1().equals(other.getP2())){
			commonPoint = this.getP1();
			dirPoint1 =this.getP2();
			dirPoint2 =other.getP1();
		}
		if (this.getP2().equals(other.getP1())){
			commonPoint = this.getP2();
			dirPoint1 =this.getP1();
			dirPoint2 =other.getP2();
		}
		if (this.getP2().equals(other.getP2())){
			commonPoint = this.getP2();
			dirPoint1 =this.getP1();
			dirPoint2 =other.getP1();
		}		
		points[0]=commonPoint;
		points[1]=dirPoint1;
		points[2]=dirPoint2;
		return points;
	}
	
	public double angleBeetween(Line3D other){
		Point2D[] common = getCommonPoint(other);
		Point2D commonPoint=common[0];
		Point2D dirPoint1=common[1];
		Point2D dirPoint2=common[2];
		
		double angle1 = Math.atan2(dirPoint1.getY() - commonPoint.getY(),
                dirPoint1.getX() - commonPoint.getX());
		double angle2 = Math.atan2(dirPoint2.getY() - commonPoint.getY(),
                dirPoint2.getX() - commonPoint.getX());
		return angle2-angle1;
	} 
	
	public Point3D getP1Z(){
		return new Point3D(getP1(),z1);
	}

	public Point3D getP2Z() {
		return new Point3D(getP2(),z2);	
	}
	
	@Override
	public int hashCode(){
		return (int) Math.round(getP2().distance(getP1())*1000);
	}
	
	@Override
	public boolean equals(Object other){
		Line3D otherLine = (Line3D)other;
		HashSet<Point2D> testSet = new HashSet<Point2D>();
		testSet.add(this.getP1());
		testSet.add(this.getP2());
		testSet.add(otherLine.getP1());
		testSet.add(otherLine.getP2());
		if (testSet.size()<=2){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean intersectsLine(Line2D l){
		if (super.intersectsLine(l)){
			if (this.equals(l)){
				return true;
			}
			if (!((this.getP1().equals(l.getP1()))||
				  (this.getP1().equals(l.getP2()))||
				  (this.getP2().equals(l.getP2()))||
				  (this.getP2().equals(l.getP1())))){
				return true;
			}
		}
		return false;
		
	}

	public boolean isVisible(Line3D other, SideList sides) {
		Point2D[] common = getCommonPoint(other);
		Point2D dirPoint1=common[1];
		Point2D dirPoint2=common[2];
		Line3D testSide = new Line3D(dirPoint1,0,dirPoint2,0);
		for (Line3D side:sides){
			if (!testSide.equals(side)){
				if (testSide.intersectsLine(side)){
						return false;
				}
			}
		}
		return true;
	}
}
