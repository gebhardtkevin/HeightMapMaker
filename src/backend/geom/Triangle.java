package backend.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import backend.lists.ContourList;
import backend.lists.SideList;
import backend.lists.TriangleList;
/*
 * A trinagle, projected on a 2D-plane (for aspects like area and side-lenght), but storing a heigth for each vertex
 *
 * @since 2.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class Triangle{
public static final int AB = 0; 
public static final int BC = 1;
public static final int CA = 2;	
/*
 * first vertex
 */
private Point3D a;
/*
 * second vertex
 */
private Point3D b;
/*
 * third vertex
 */
private Point3D c;

private ArrayList<Line3D> sides = new ArrayList<>();
/*
 * area of the Triangle
 * 
 */
private double area;

private SideList isFlipped = new SideList();
/*
 * Constructs a triangle from 3 Points, 
 * ensuring a mathematicaly positiv direction
 * and calculating the area
 * @param first vertex
 * @param second vertex
 * @param third vertex
 */
	public Triangle(Point3D a, Point3D b, Point3D c){
		this.a = a;
		this.b = b;
		this.c = c;
		if (this.det()<0){ //make triangle direction counterclockwise
			this.b=c;
			this.c=b;
		}				
		this.area = getArea(getLenght());
		this.sides.add(new Line3D(a,b));
		this.sides.add(new Line3D(b,c));
		this.sides.add(new Line3D(c,a));
	}
	
	public Triangle(Line3D side1, Line3D side2){
		HashSet<Point3D> points = new HashSet<>();
		points.add(side1.getP1Z());
		points.add(side1.getP2Z());
		points.add(side2.getP1Z());
		points.add(side2.getP2Z());
		points.remove(null);
		Iterator<Point3D> it = points.iterator();
		
		this.a = it.next();
		this.b = it.next();
		this.c = it.next();
		if (this.det()<0){ //make triangle direction counterclockwise
			Point3D temp = this.b;
			this.b = this.c;
			this.c=temp;
		}				
		this.area = getArea(getLenght());
		this.sides.add(new Line3D(a,b));
		this.sides.add(new Line3D(b,c));
		this.sides.add(new Line3D(c,a));
	}
	
	/*
	 *Getter for first vertex
	 *@returns first vertex 
	 */
	public Point3D getA(){
	return a;
	}
	
	/*
	 *Getter for second vertex
	 *@returns second vertex 
	 */
	public Point3D getB(){
	return b;
	}
	
	/*
	 *Getter for third vertex
	 *@returns third vertex 
	 */
	public Point3D getC(){
	return c;
	}
	
	/*
	 *checks, if a given point is a vertex of the triangle, not considering the height
	 *@returns true, if point is a vertex 
	 */
	public boolean hasVertex(Point2D vertex){
		if (a.flat().equals(vertex)||b.flat().equals(vertex)||c.flat().equals(vertex)){
			return true;
		}
	return false;
	}

	public Point3D getOppositeVertex(Line2D side){
		if (side.equals(this.sides.get(AB))){
			return c;
		}
		if (side.equals(this.sides.get(BC))){
			return a;
		}
		if (side.equals(this.sides.get(CA))){
			return b;
		}
		return null;
	}
	
	/*
	 *calculating the 2D-determinante of the triangle, needed for checking the triangles running direction 
	 *@returns the determinate 
	 */
	private double det() {
	return a.getX()*b.getY()+a.getY()*c.getX()+b.getX()*c.getY()-c.getX()*b.getY()-c.getY()*a.getX()-b.getX()*a.getY();
	}

	/*
	 *calculates the length of the triangles sides
	 *@returns a 3D-point, consisting of all three lengths 
	 */
	private Point3D getLenght() {
		return new Point3D(this.a.flatDist(b),this.b.flatDist(c),this.c.flatDist(a));
	}
	
	/*
	 *calculates the area of the triangle
	 *@returns the area 
	 */
	private double getArea(Point3D lenght){
		double s  = (lenght.getX()+lenght.getY()+lenght.getZ())/2;
		if (s<lenght.getX()||s<lenght.getY()||s<lenght.getY()){//degenerated case, Triangle is a line and double precission kicks in
			return 0;
		}
	return Math.sqrt(s*(s-lenght.getX())*(s-lenght.getY())*(s-lenght.getZ()));
	}

	/*
	 *checks, if a point is inside the triangle.Points directly on the triangles side will return false
	 *@returns true, if a point is inside the triangle, false otherwise 
	 */
	public boolean contains(Point2D point){
		 double epslyon = this.sides.get(1).lenght()*0.000000001;
		 double denominator = ((b.getY() - c.getY())*(a.getX() - c.getX()) + (c.getX() - b.getX())*(a.getY() - c.getY()));
		 if (denominator == 0){
			 return false;
		 }	 
		 double baryA = ((b.getY() - c.getY())*(point.getX() - c.getX()) + (c.getX() - b.getX())*(point.getY() - c.getY())) / denominator;
		 double	baryB = ((c.getY() - a.getY())*(point.getX() - c.getX()) + (a.getX() - c.getX())*(point.getY() - c.getY())) / denominator;
		 double baryC = 1 - baryA - baryB;

		 if ((0-epslyon <= baryA)&&(baryA <= 1+epslyon)&&(0-epslyon <= baryB)&&(baryB <= 1+epslyon)&&(0-epslyon <= baryC)&&(baryC <= 1+epslyon)){
			 return true;
		 }
		 return false;
	}

//	/*
//	 *2D-Version. Splits a triangle into tree, with each oh the new triangles consisting of the given Point and one side of the triangle
//	 *so that the triangles won't intersect, as long as the point is inside the triangle.
//	 *Insideness won't be checked by this method for performance reasons
//	 *@param a Point inside the triangle
//	 *@returns three smaller triangles 
//	 */
	private ArrayList<Triangle> split(Point2D point){
		ArrayList<Triangle> split = new ArrayList<>();
		Triangle firstInner =  new Triangle(b, c, new Point3D(point,0));
		Triangle secondInner = new Triangle(c, a, new Point3D(point,0));
		Triangle thirdInner = new Triangle(a, b, new Point3D(point,0));
		
		split.add(0,firstInner);//corresponding to point a
		split.add(1,secondInner);//corresponding to point b
		split.add(2,thirdInner);//corresponding to point c
	return split;
	}
//	
//	/*
//	 *checks, if a triangle is smaller than an other one 
//	 *@param the other triangle
//	 *@returns true, if this triangle is smaller, false otherwise 
//	 */
//	public boolean isSmallerThan(Triangle other){
//		if (this.area<other.area){
//			return true;
//		}else{
//			return false;
//		}
//	}

	/*
	 *calculated the height of a point inside the triangle by barycentric interpolation 
	 *Insideness won't be checked for performance reasons.
	 *@param a point inside the triangle
	 *@returns the height of the point 
	 */
	public double interpolate(Point2D interpolationPoint){
		if (this.area<0.0000001){
			return a.getZ();
		}
		ArrayList<Triangle> percentageTriangles = this.split(interpolationPoint);
	return percentageTriangles.get(0).area/this.area*a.getZ()+percentageTriangles.get(1).area/this.area*b.getZ()+percentageTriangles.get(2).area/this.area*c.getZ();
	}
	
	/*
	 *calculated the center of the circumcircle of a triangle
	 *@returns the center of the circumcicle 
	 */
	 private Point2D getSurroundingCenter(){

		 Point2D AB = new Point2D.Double(a.getX() + (b.getX()-a.getX())/(2),a.getY() + (b.getY()-a.getY())/(2));		 
		 Point2D AC = new Point2D.Double(a.getX() + (c.getX()-a.getX())/(2),a.getY() + (c.getY()-a.getY())/(2));
		 
		 Point2D normAB = new Point2D.Double(b.getY()-a.getY(),-(b.getX()-a.getX()));
		 Point2D normAC = new Point2D.Double(c.getY()-a.getY(),-(c.getX()-a.getX()));
		 double u = (AB.getY()*normAB.getX() + AC.getX()*normAB.getY() - AB.getX()*normAB.getY() - AC.getY()*normAB.getX())/(normAC.getY()*normAB.getX()-normAC.getX()*normAB.getY()); 
	 return new Point2D.Double(AC.getX() + u*normAC.getX(),AC.getY() + u*normAC.getY());
	 }
	 
	 @Override
	 public boolean equals(Object other){
		 if (((Triangle)other).hasVertex(a.flat())&&((Triangle)other).hasVertex(b.flat())&&((Triangle)other).hasVertex(c.flat())){
			 return true;
		 }
		 return false;
		 
	 }
	 
	 @Override
	 public int hashCode(){
		 return (int) Math.round(area*1000);
	 }
	 
	 /*
	 *checks, if two trinagles fullfill the Delaunay-criteria, this meaning that their smallest inner angle is as big as possible. 
	 *this will prevent roundign errors.
	 *@returns true, if the Delaunay-criteria is fullfiiled, false ohterwise
	 */	 
	 public boolean isDelaunay(Triangle neighbour, ContourList contourList){
		 Line3D commonside = null;
		 for (Line3D side : this.sides){
			 for (Line3D otherside : neighbour.sides){
				 if (side.equals(otherside)){
					 commonside=side;
					 break;
				 }
			 }
			 if (null!=commonside){
				 break;
			 }
		 }
		 
		 if (null==commonside){ //no neigbours, no need to flip
			 return true;
		 }
		 if (null!=contourList){
			 for (Line3D contour:contourList.getLines()){
				 if (contour.equals(commonside)) //common side is contour, don't flip, we need to save the contours
				 {
					 return true;
				 }
			 }
	 	 }
		 
		 Set<Point3D> testSet = new HashSet<>();
		 testSet.add(neighbour.a);
		 testSet.add(neighbour.b);
		 testSet.add(neighbour.c);
		 testSet.remove(commonside.getP1Z());
		 testSet.remove(commonside.getP2Z());
		 Point3D outerPoint = testSet.iterator().next();
		 Point2D center = getSurroundingCenter(); 
		 if (outerPoint.flat().distance(center)>=a.flat().distance(center)){
			 return true;
		 }
		 return false;
	 }
	 
	 /*
	 *flips the common side of two triangles to the other, formerly "free" vertexes
	 *If the triangles aren't neighbours, nothing is done and the original triangles will be returned
	 *@param the neighbour triangel
	 *@returns the two flipped triangels (if they are neighbours)
	 */	
	 public ArrayList<Triangle> flip(Triangle other){
		 ArrayList<Triangle> flipped = new ArrayList<Triangle>(2);
		 HashSet<Point3D> uncommonPoints= new HashSet<Point3D>();
		 
		 Line3D commonside = null;
		 for (Line3D side : this.sides){
			 for (Line3D otherside : other.sides){
				 if (side.equals(otherside)){
					 commonside=side;
					 break;
				 }
			 }
			 if (null!=commonside){
				 break;
			 }
		 }
		 uncommonPoints.add(other.a);
		 uncommonPoints.add(other.b);
		 uncommonPoints.add(other.c);
		 uncommonPoints.add(this.a);
		 uncommonPoints.add(this.b);
		 uncommonPoints.add(this.c);
		 uncommonPoints.remove(commonside.getP1Z());
		 uncommonPoints.remove(commonside.getP2Z());
		 
		 Object[] uncommonPointsArr = uncommonPoints.toArray();
		 Line3D newCommonSide = new Line3D((Point3D)uncommonPointsArr[0],(Point3D)uncommonPointsArr[1]);
		 Line3D newFirstSide = new Line3D((Point3D)uncommonPointsArr[0],(Point3D)commonside.getP1Z());
		 Line3D newSecondSide = new Line3D((Point3D)uncommonPointsArr[0],(Point3D)commonside.getP2Z());
		  
		 Triangle first = new Triangle(newCommonSide,newFirstSide);
		 Triangle second =  new Triangle(newCommonSide,newSecondSide);
		 first.isFlipped.add(newCommonSide);
		 second.isFlipped.add(newCommonSide);			
		 flipped.add(first);
		 flipped.add(second);
		 return flipped;
	 }
	 
	/*
	* Getter for the triangles area, calculated during construction
	*@returns the area of the triangle
	*/
	public double getArea() {
	return this.area;
	}

	public TriangleList getNeighbours(TriangleList triangles) {
		TriangleList neighbours = new TriangleList();
		Iterator<Triangle> it = triangles.iterator();
		while (it.hasNext()){
			Triangle possibleNeighbour=it.next();
			HashSet<Point3D> check = new HashSet<>();
			check.add(this.a);
			check.add(this.b);
			check.add(this.c);
			check.add(possibleNeighbour.a);
			check.add(possibleNeighbour.b);
			check.add(possibleNeighbour.c);
			if (check.size()==4){
				neighbours.add(possibleNeighbour);
				if (neighbours.size()==3){
					break;
				}
			}	
		}
		return neighbours;
	}
}