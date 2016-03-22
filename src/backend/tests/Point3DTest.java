package backend.tests;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.BeforeClass;
import org.junit.Test;

import backend.geom.Point3D;

public class Point3DTest {
	static private double x;
	static private double y;
	static private double z;
	
	static private Point3D first;
	
	
	@BeforeClass
	static public void init(){
		x = 1d;
		y = 2d;
		z = 4d;
		first = new Point3D(x,y,z);
	}
	
	@Test
	public void constructorsShouldDoTheSame() {
		Point3D second = new Point3D(new Point2D.Double(x,y), z);
		assertEquals("No equal Point on the contructors", first,second);
	}

	@Test
	public void testFlatDistPoint3D() {
		Point3D second = new Point3D(0, 0, 0);
		assertEquals("distance calculated wrong", Math.sqrt(5), first.flatDist(second),0.00001);
	}

	@Test
	public void testFlatDistPoint2D() {
		Point2D second = new Point2D.Double(0, 0);
		assertEquals("distance calculated wrong", Math.sqrt(5), first.flatDist(second),0.00001);
	}

	@Test
	public void testFlatDistDoubleDouble() {
		assertEquals("distance calculated wrong", Math.sqrt(5), first.flatDist(0,0),0.00001);
	}

	@Test
	public void testDist() {
		Point3D second = new Point3D(0, 0, 0);
		assertEquals("distance calculated wrong", Math.sqrt(21), first.dist(second),0.00001);
	}
}
