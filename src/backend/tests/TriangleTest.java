package backend.tests;

import static org.junit.Assert.*;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import backend.geom.ContourLine;
import backend.geom.Line3D;
import backend.geom.Point3D;
import backend.geom.Triangle;
import backend.lists.ContourList;
import backend.lists.TriangleList;

public class TriangleTest {
	
	private static Point3D a;
	private static Point3D b;
	private static Point3D c;
	private static Triangle first;
	
	@BeforeClass
	static public void init(){
	a = new Point3D(0,0,0);
	b = new Point3D(1,0,10);
	c = new Point3D(0,1,100);
	first = new Triangle(a, b, c);
	}
	
	@Test
	public void constructorsShouldDoTheSame(){
		
		Triangle second = new Triangle(new Line3D(a,b),new Line3D(b,c));
		
		assertEquals("The triangles should be equal in each contructor", first, second);
	}
	
	@Test
	public void hasVertexShouldReturnTrueOnVertex(){
		assertTrue("This Vertex should be found in the triangle",first.hasVertex(a.flat()));
		assertFalse("This Vertex should not be found in the triangle",first.hasVertex(new Point2D.Double(0,5)));		
	}

	@Test
	public void getOppositeVertexShouldGiveExactlyThat(){
		Point3D d = new Point3D(1,1,1);
		assertSame("This should give the vertex c(0,1,100)", c,first.getOppositeVertex(new Line3D(a,b)));
		assertNull("If side is not in triangle, null should bne returned", first.getOppositeVertex(new Line3D(a,d)));

	}
	
	@Test
	public void containsShouldReturnTrueForPointInsideTriangle(){
		assertTrue("This is a point inside the trinagle, hence we should get true",first.contains(new Point3D(0.5,0.5,0).flat()));
		assertTrue("A point on the border should be inside of the Triangle",first.contains(new Point3D(0,0.5,0).flat()));
		assertTrue("A vertex is should be inside the triangle",first.contains(new Point3D(0,0,0).flat()));
		assertFalse("A Point outside the triangle is not inside",first.contains(new Point3D(0,-1,0).flat()));
	}
	
	@Test 
	public void flipShouldReturnTheSameOuterLineButAFlippedCommonSide(){
		Point3D d = new Point3D(1,1,1);
		Triangle second = new Triangle(b,c,d);
		ArrayList<Triangle> flipped = first.flip(second);
		Line3D newCommonSide = new Line3D(a,d);
		Point3D point1 = flipped.get(0).getOppositeVertex(newCommonSide);
		Point3D point2 = flipped.get(1).getOppositeVertex(newCommonSide);
		if (point1.flat().equals(a.flat()) || point1.flat().equals(d.flat())){
			fail("Flip failed");
		}
		if (point2.flat().equals(a.flat()) || point2.flat().equals(d.flat())){
			fail("Flip failed");
		}
	}
	
	@Test
	public void getNeighboursShouldReturnAllNeighbours(){
		Point3D d = new Point3D(1,1,1);
		Triangle second = new Triangle(b,c,d);
		Point3D e = new Point3D(0.5,-0.5,0);
		Triangle third = new Triangle(a,b,e);
		Point3D f = new Point3D(0,2,0);
		Triangle noNeighbour = new Triangle(b,d,f);
		TriangleList triangles = new TriangleList();
		triangles.add(first);
		triangles.add(second);
		triangles.add(third);
		triangles.add(noNeighbour);
		TriangleList neighbours = first.getNeighbours(triangles);
		assertTrue("Neighbour missing",neighbours.contains(second));
		assertTrue("Neighbour missing",neighbours.contains(third));
		assertFalse("Found myself as a neighbour",neighbours.contains(first));
		assertFalse("Triangle is not a neighbour",neighbours.contains(noNeighbour));
	}
	
	@Test 
	public void interpolateShouldCalulateCorrect(){
		Point2D point = new Point2D.Double(0.25,0.25);
		double value = first.interpolate(point);
		double aim = 12.65625;
		assertTrue("Interpolation is plain wrong", value-aim>0.000001);
	}
	
	@Test
	public void isDelaunayShouldFindPointsInCirmumcircle(){
		Point3D in = new Point3D(0.75,0.75,0);
		Point3D border = new Point3D(1,1,0);
		Point3D out = new Point3D(2,2,0);
		Triangle second = new Triangle(b, c, in);
		assertFalse("Neighbour should not be Delaunay, but is", first.isDelaunay(second, null));
		second = new Triangle(b, c, border);
		assertTrue("Neighbour on Circumcircle should be Delaunay, but isn't", first.isDelaunay(second, null));
		second = new Triangle(b, c, out);
		assertTrue("Neighbour should be Delaunay, but isn't", first.isDelaunay(second, null));
		Path2D path = new Path2D.Double();
		path.moveTo(b.getX(),b.getY());
		path.lineTo(c.getX(), c.getY());
		ContourLine contour = new ContourLine("1_CL_0", path);
		ArrayList<ContourLine> contours = new ArrayList<>();
		contours.add(contour);
		ContourList cl = new ContourList(contours);
		assertTrue("contourline should not be ignored", first.isDelaunay(second, cl));
	}	
}