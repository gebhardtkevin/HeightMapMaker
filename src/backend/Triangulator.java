package backend;
/*
 * Triangulates via conditioned Delaunay-Triangulation with saving all the contourlines
 *  
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

import backend.geom.ContourLine;
import backend.geom.Line3D;
import backend.geom.Point3D;
import backend.geom.Triangle;
import backend.lists.ContourList;
import backend.lists.SideList;
import backend.lists.TriangleList;

public class Triangulator {
	TriangleList triangles = new TriangleList();

	public Triangulator(Point2D dimensions, double baseHeight, ContourList contourList){
	contourList = normaliseContourList(dimensions,baseHeight,contourList);
	SideList sideList = collectVisibleSides(contourList);
	this.triangles = makeTriangles(sideList,contourList);
	this.triangles = makeDelaunay(this.triangles,contourList);
	}
	
	private TriangleList makeDelaunay(TriangleList triangles, ContourList contourList) {
		boolean hasFlipped = true;
		while (hasFlipped){
			hasFlipped=false;
			Iterator<Triangle> it = triangles.iterator();
				while(it.hasNext()&&!hasFlipped){
					Triangle triangle = it.next();
					for (Triangle neighbour:triangle.getNeighbours(triangles)){
						if (!(triangle.isDelaunay(neighbour, contourList))){
							triangles.addAll(triangle.flip(neighbour));
							triangles.remove(triangle);
							triangles.remove(neighbour);
							hasFlipped=true;
							break;
						}
					}
				}
		}
		return triangles;
	}

	private SideList collectVisibleSides(ContourList contourList) {
		SideList sideList = new SideList();
		sideList.addAll(contourList.getLines());
		for (Point3D point: contourList.getPoints()){
			for (Point3D other: contourList.getPoints()){
				Boolean intersects=false;
				if (point.equals(other)){
					continue;
				}
				Line3D testSide = new Line3D(point, other);
				for (Line3D side:sideList){
					if (side.intersectsLine(testSide)){
						intersects = true;
						break;
					}
				}
				if (!intersects){
					sideList.add(testSide);
				}
			}
		}
		return sideList;
	}


	private TriangleList makeTriangles(SideList sideList,ContourList contours) {
		TriangleList triangles = new TriangleList();
		for (Line3D firstSide : sideList){
			Line3D[] secondSide = new Line3D[2];
			SideList connected = sideList.getConnected(firstSide.getP1Z());
			double minimalAnglePlus = Double.MAX_VALUE;
			double maximalAngleMinus = -Double.MAX_VALUE;
			for (Line3D other: connected){
				if (!(firstSide.equals(other))&&firstSide.isVisible(other,sideList)){
					double angle = firstSide.angleBeetween(other);
					if ((angle>0) && (minimalAnglePlus>angle)){
						minimalAnglePlus=angle;
						secondSide[0]=other;
					}
					if ((angle<0) && (maximalAngleMinus<angle)){
						maximalAngleMinus=angle;
						secondSide[1]=other;
					}					
				}
			}
			if (null!=secondSide[0]){//math says always true
				triangles.add(new Triangle(firstSide, secondSide[0]));
			}
			if (null!=secondSide[1]){
				triangles.add(new Triangle(firstSide, secondSide[1]));
			}
		}
		return triangles;
	}

	private ContourList normaliseContourList(Point2D dimensions, double baseHeight, ContourList contourList){
		Path2D base = new Path2D.Double();
		base.moveTo(0,0);
		base.lineTo(dimensions.getX(),0);
		base.lineTo(dimensions.getX(),dimensions.getY());
		base.lineTo(0,dimensions.getY());
		base.closePath();
		contourList.addContour(new ContourLine("-1_CL_"+baseHeight,base));
		return contourList;
	}
	
	public TriangleList getTriangles(){
		return this.triangles;
	}
}
