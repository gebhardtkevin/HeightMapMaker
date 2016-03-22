package backend.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import backend.geom.Line3D;
import backend.geom.Point3D;
import backend.lists.SideList;

public class Line3DTest {
	private static Point3D a;
	private static Point3D b;
	private static Line3D first;
	
	@BeforeClass
	static public void init(){
		a = new Point3D(0,0,0);
		b = new Point3D(0,1,1);
		first = new Line3D(a,b);
	}
	
	@Test
	public void constructorsShouldDoTheSame() {
		Line3D second = new Line3D(a.flat(),a.getZ(),b.flat(),b.getZ());
		Line3D third = new Line3D(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
		assertEquals("Constructors should construct the same object",first, second);
		assertEquals("Constructors should construct the same object",third, second);
	}

	@Test
	public void testAngleBeetween() {
		Line3D second = new Line3D(0, 0, 0, 1, 0,1);
		assertEquals("Angle beetween lines calculated wrong", -Math.PI/2, first.angleBeetween(second), 0.000001);
	}

	@Test
	public void testIntersectsLineLine2D() {
		Line3D second = new Line3D(-1, 0.5, 1, 1, 0.5,1);
		Line3D third = new Line3D(0, 0, 0, 1, 0,1);
		Line3D fourth = new Line3D(-1, 2, 1, 1, 2,1);
		assertTrue("Lines should intersect whith themself", first.intersectsLine(first));
		assertTrue("Lines should intersect", first.intersectsLine(second));
		assertFalse("Lines that only meet on the vertex should not intersect", first.intersectsLine(third));
		assertFalse("Lines should not intersect", first.intersectsLine(fourth));
	}

	@Test
	public void testIsVisible() {
		Line3D second = new Line3D(0,0,0,1,0,0);
		Line3D badContour = new Line3D(0, 0, 0, 2, 2, 0);
		Line3D goodContour = new Line3D(2, 0, 0, 0, 2, 0);
		SideList goodSites = new SideList();
		goodSites.add(goodContour);
		SideList badSites = new SideList();
		badSites.add(badContour);
		assertTrue("Lines should be visible to each other",     first.isVisible(second, goodSites));		
		assertFalse("Lines shouldn't be visible to each other", first.isVisible(second, badSites));	
	}
}
