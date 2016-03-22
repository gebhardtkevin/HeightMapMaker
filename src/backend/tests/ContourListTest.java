package backend.tests;

import static org.junit.Assert.*;

import java.awt.geom.Path2D;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import backend.geom.ContourLine;
import backend.geom.Line3D;
import backend.geom.Point3D;

public class ContourListTest {
	private static ContourLine contour;
	@BeforeClass
	public static void init() {
		Path2D path = new Path2D.Double();
		path.moveTo(0, 0);
		path.lineTo(10, 0);
		path.lineTo(10, 10);
		path.lineTo(0, 10);
		path.closePath();
		contour = new ContourLine("1_CL_0", path);
	}

	@Test
	public void testGetLines() {
		ArrayList<Line3D> lines = contour.getPathLines();
		assertTrue("line in contourline missing", lines.contains(new Line3D(0,0,0,10,0,0)));
		assertTrue("line in contourline missing", lines.contains(new Line3D(10,0,0,10,10,0)));
		assertTrue("line in contourline missing", lines.contains(new Line3D(10,10,0,0,10,0)));
		assertTrue("line in contourline missing", lines.contains(new Line3D(0,10,0,0,0,0)));
		assertTrue("to many lines", lines.size()==4);
	}

	@Test
	public void testGetPoints() {
		ArrayList<Point3D> points = contour.getPathPoints();
		assertTrue("point in contourline missing", points.contains(new Point3D(0,0,0)));
		assertTrue("point in contourline missing", points.contains(new Point3D(10,0,0)));
		assertTrue("point in contourline missing", points.contains(new Point3D(10,10,0)));
		assertTrue("point in contourline missing", points.contains(new Point3D(0,10,0)));
		assertTrue("to many points", points.size()==4);
	}
}
