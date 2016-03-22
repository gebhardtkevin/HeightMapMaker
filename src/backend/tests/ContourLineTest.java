package backend.tests;

import static org.junit.Assert.*;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import backend.geom.ContourLine;
import backend.geom.Line3D;
import backend.geom.Point3D;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;

public class ContourLineTest {

	private static Element pathElem;
	private static ContourLine first;
	private static Path2D path;
	
	@BeforeClass
	public static void init(){
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = docBuilder.newDocument();
		pathElem = doc.createElement("path");
		pathElem.setAttribute("style", "fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:0.24281722px");
		pathElem.setAttribute("d", "M 0 0 L 10 0 10 10 0 10 Z");
		pathElem.setAttribute("id", "1_CL_100");
		pathElem.setAttribute("shouldBeIgnored", "gibberish");
		first = new ContourLine(pathElem);
		
		path = new Path2D.Double();
		path.moveTo(0, 0);
		path.lineTo(10, 0);
		path.lineTo(10, 10);
		path.lineTo(0, 10);
		path.closePath();
	}
	@Test
	public void constructorsShouldDoTheSame() {
		Path2D path = new Path2D.Double();
		path.moveTo(0, 0);
		path.lineTo(10, 0);
		path.lineTo(10, 10);
		path.lineTo(0, 10);
		path.closePath();
		ContourLine second = new ContourLine("1_CL_100",path);
		assertTrue("Constructors should build equal objects",first.equals(second));
	}

	@Test
	public void testGetHeight() {
		assertEquals("Height is wrong",first.getHeight(), 100d,0);
	}

	@Test
	public void testGetOriginalPath() {
		assertTrue("Original Path wrong", first.getOriginalPath().equals("M 0 0 L 10 0 10 10 0 10 Z"));
	}

	@Test
	public void testGetCalculatedPath() {
		assertTrue("Java Calculated Path wrong",first.getCalculatedPath().equals("M 0.0 0.0 10.0 0.0 10.0 10.0 0.0 10.0 Z"));
	}

	@Test
	public void testGetJavaPath() {
		boolean tester = true;
		PathIterator it1 = first.getJavaPath().getPathIterator(null);
		PathIterator it2 = path.getPathIterator(null);
		double[] coords1 = new double[6]; 
		double[] coords2 = new double[6];
		
		while (it1.isDone()){
			if (it2.isDone()){
				tester=false;
				break;
			}
			tester=false;
			if (it1.currentSegment(coords1)==it2.currentSegment(coords2)){
				if (coords1.equals(coords2)){
					tester=true;
				}
			}
		it1.next();
		it2.next();
		}
		
		assertTrue("Java Path2D failure",tester);
	}

	@Test
	public void testGetPathPoints() {
		 ArrayList<Point3D> points = first.getPathPoints();
		 assertTrue("Point in pointList missing", points.contains(new Point3D(0,0,100)));
		 assertTrue("Point in pointList missing", points.contains(new Point3D(0,10,100)));
		 assertTrue("Point in pointList missing", points.contains(new Point3D(10,10,100)));
		 assertTrue("Point in pointList missing", points.contains(new Point3D(10,0,100)));
		 assertTrue("To many points in pointList", points.size()==4);
	}

	@Test
	public void testGetPathLines() {
		 ArrayList<Line3D> lines = first.getPathLines();
		 assertTrue("Line in lineList missing", lines.contains(new Line3D(0,0,100,0,10,100)));
		 assertTrue("Line in lineList missing", lines.contains(new Line3D(0,10,100,10,10,100)));
		 assertTrue("Line in lineList missing", lines.contains(new Line3D(10,10,100,10,0,100)));
		 assertTrue("Line in lineList missing", lines.contains(new Line3D(10,0,100,0,0,100)));
		 assertTrue("To many lines in lineList", lines.size()==4);
	}
}
