package backend.lists;

import java.util.ArrayList;
import java.util.Iterator;

import backend.geom.ContourLine;
import backend.geom.Line3D;
import backend.geom.Point3D;
/*
 * Convinence class for a list of the contourlines
 *
 * @since 2.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class ContourList implements Iterable<ContourLine>{

	private ArrayList<ContourLine> contours = new ArrayList<ContourLine>();
	private ArrayList<Point3D> points = new ArrayList<Point3D>();
	private ArrayList<Line3D> lines = new ArrayList<Line3D>();
	
	public ContourList(ArrayList<ContourLine> contours){
		if (null!=contours){
			this.contours = contours;
			for (ContourLine contour: contours){
				points.addAll(contour.getPathPoints());
				lines.addAll(contour.getPathLines());
			}
		}
	}
	
	public ArrayList<Line3D> getLines(){
		return lines;
	}
	
	public ArrayList<Point3D> getPoints(){
		return points;
	}
	
	public void addContour(ContourLine contour){
		contours.add(contour);
		points.addAll(contour.getPathPoints());
		lines.addAll(contour.getPathLines());
	}

	@Override
	public Iterator<ContourLine> iterator() {
		return contours.iterator();
	}
}
