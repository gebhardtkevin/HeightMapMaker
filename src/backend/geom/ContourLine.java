package backend.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

import org.w3c.dom.Element;
/*
 * A ContourLine is a line of constant height defined as svg-path in inkscape and marked as souch by an id like "NUMBER_CL_HEIGHT" 
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class ContourLine{
/*
 * prefix marking a path as contourline
 */
public static final String ID_PREFIX = "CL_";
/*
 * svg name element
 */
public static final String NAME_ATTRIBUTE = "id";
/*
 * svg path element
 */
public static final String POINTS_ATTRIBUTE = "d";
/*
 * regex for checking the paths for validy
 */
public static final String REGEX_D = "[0-9MmLlHhVvQqTtCcSsZz,\\- \\.]+";

/*
 * the Points of the path as String
 */
private String points;

/*
 * the path as object
 */
private Path2D path;
/*
 * height of the contour
 */
private double height;
/*
 * is there more than one loop in the path?
 */
private boolean singular=true;
/*
 * is the path a valid svg-path?
 */
private boolean valid = true;
/*
 * list of all the transformation applied to the path in the svg
 */
private LinkedList<double[]> transformationList = new LinkedList<>();
/*
 * area defined by the path
 */
private Area area;
/*
 * id of the path
 */
private String name;

/*
 * constructs a contourline from a svg-path
 * @param a xml-Element containing a svg-path
 */
	public ContourLine(Element path){
		parse(path);
	}

/*
 * constructs a contourline from a java-path
 * @param name of the path (in the form NUMBER_CL_HEIGHT)
 * @param a xml-Element containing a svg-path
 */
	public ContourLine(String name, Path2D path){
		this.points="";
		this.name = name;
		this.path=path;
		this.height=getHeightFromName(name);
		
		double coords[] = new double[6]; 
		path.getPathIterator(null).currentSegment(coords);
		this.area = new Area(path);
	}

	/*
	 * parses a svg-path-Element to a contourline
	 * @param a svg-path-Element
	 */
	private void parse(Element svgLine){
		checkforTransformation(svgLine);
		Element parent;
		try {
			parent = (Element)svgLine.getParentNode();
		}catch (ClassCastException e){
			parent = null;
		}
		while (parent!=null){
			checkforTransformation(parent);
			try {
				parent = (Element)parent.getParentNode();
			}catch (ClassCastException e){
				parent = null;
			}
		}
		
		this.name = svgLine.getAttribute(NAME_ATTRIBUTE);
		this.height = getHeightFromName(this.name);
		points = svgLine.getAttribute(POINTS_ATTRIBUTE);
		if (!points.matches(REGEX_D)){
			valid = false;		
			this.path = new Path2D.Double();
		}else{
			this.path = getJavaPath(points);
		}
		this.area=new Area(path);
	}

	/*
	 * extract the height from the paths name
	 * @param the paths name
	 * @returns the height
	 */
	private double getHeightFromName(String name){
		try{
		return Double.parseDouble(name.substring(name.indexOf(ID_PREFIX)+ID_PREFIX.length()));
		}catch (NumberFormatException e){
			return 0;
		}
	}
/*
 * stores all the transformations applied to the path in the svg file
 * @param a svg-element possibly containing transformations
 */
	private void checkforTransformation(Element transformable) {
		//rudimentary, can only handle translation and scale for now
		if (transformable.hasAttribute("transform")){
			String transformation = transformable.getAttribute("transform");
			String transformationType = transformation.substring(0,transformation.indexOf('('));
			switch (transformationType){
				case "translate":{
					String values = transformation.substring(transformation.indexOf('(')+1,transformation.indexOf(')')-1);
					String[] valuesArrayPre = values.split("(?=[, -])");
					LinkedList<Double> cleanValuesArray = new LinkedList<>();
					for (String value : valuesArrayPre){
						try {
							cleanValuesArray.addLast(Double.parseDouble(value.trim().replace(",","")));
						}catch (NumberFormatException e){
							continue;
						}
					}
					double x =cleanValuesArray.getFirst();
					double y = 0;
					if (cleanValuesArray.size()>1){
					y = cleanValuesArray.getLast();
					}
					//0 means translate
					double[] operation = {0,x,y};
					transformationList.addLast(operation);
					break;
				}
				case "scale":{
					String values = transformation.substring(transformation.indexOf('(')+1);
					String[] valuesArrayPre = values.split("(?=[, -])");
					LinkedList<Double> cleanValuesArray = new LinkedList<>();
					for (String value : valuesArrayPre){
						try {
							cleanValuesArray.addLast(Double.parseDouble(value.trim().replace(",","")));
						}catch (NumberFormatException e){
							continue;
						}
					}
					double x =cleanValuesArray.getFirst();
					double y = x;
					if (cleanValuesArray.size()>1){
					y = cleanValuesArray.getLast();
					}
					//1 means scale
					double[] operation = {1,x,y};
					transformationList.addLast(operation);
					break;
				}
				default:{
					System.err.print("Unhandled Transformation in " + transformable.getLocalName() + ". Only Translation and Scale are handled at the Moment ");
					System.exit(1);
					break;
				}
			}
		}
	}
/*
 * Getter for the height
 * @returns the height
 */
	public double getHeight(){
		return this.height;
	}
	/*
	 * Getter for the first point
	 * @returns the first path point
	 */

	public Point2D getFirstPoint(){
		return this.getPathPoints().get(0).flat();
	}

	/*
	 * Getter for the string representation of the svg-path
	 * @returns the svg-path as string
	 */
	public String getOriginalPath(){
	return this.points;
	}

	/*
	 * Getter for the string representation of the java-path
	 * @returns the java-path as string
	 */
	public String getCalculatedPath(){
		String calcPoints="";
		PathIterator iterator = path.getPathIterator(null);
		while (!iterator.isDone()){
			double[] coords = new double[6];
			switch (iterator.currentSegment(coords)){
				case PathIterator.SEG_MOVETO: {
					calcPoints +="M " + coords[0] + " " + coords[1] + " ";
					break;
					}  
				case PathIterator.SEG_LINETO:{
					calcPoints += coords[0] + " " + coords[1] + " ";
					break;
					}
				case PathIterator.SEG_CLOSE :{
					calcPoints+="Z";
					break;
				}
				case PathIterator.SEG_CUBICTO:
				case PathIterator.SEG_QUADTO:
				default:{
					System.err.print("Path " + this.name +" is not a Polygon! ");
					System.exit(1);
				}
			}
			iterator.next();
		}	
		return calcPoints;
	}

	/*
	 * Getter for the java-path
	 * @returns the java-path
	 */
	public Path2D getJavaPath(){
	return this.path;
	}
	
	/*
	 * Getter for the path-name
	 * @returns the name
	 */
	public String getName(){
	return this.name;
	}

	/*
	 * Getter for the java-path
	 * private version for constructor
	 * @returns the java-path
	 */
	private Path2D getJavaPath(String pathString){
		Point2D point = new Point2D.Double(0,0);
		Path2D path = new Path2D.Double();
		
		if (!(pathString.startsWith("M")||pathString.startsWith("m"))){
			valid=false;
		}else{
			String penAction="";
			int    pointCounter=0;
		//Split at Commands, keeping the Commands. RegEx is fun, eh?
		String[] pathAsArray = pathString.split("(?=[MmLlHhVvQqTtCcSsZz])");
		for (String command:pathAsArray){
			//Split at Commands, spaces, linebreaks and commas 
			String[] commandPoints = command.split("(?=[MmLlHhVvQqTtCcSsZz \\n\\r,])");
			for (String expression:commandPoints){
				expression = expression.replaceAll("[, ]", "");
				switch (expression.trim()){	
					case "M":penAction="M";pointCounter=0;break;
					case "m":penAction="m";pointCounter=0;break;
					case "L":penAction="L";pointCounter=0;break;
					case "l":penAction="l";pointCounter=0;break;
					case "H":penAction="L";pointCounter=0;break;
					case "h":penAction="l";pointCounter=0;break;
					case "V":penAction="L";pointCounter=0;break;
					case "v":penAction="l";pointCounter=0;break;
					case "Q":penAction="L";pointCounter=0;break;
					case "q":penAction="l";pointCounter=0;break;
					case "T":penAction="L";pointCounter=0;break;
					case "t":penAction="l";pointCounter=0;break;
					case "C":penAction="C";pointCounter=0;break;
					case "c":penAction="c";pointCounter=0;break;
					case "S":penAction="L";pointCounter=0;break;
					case "s":penAction="l";pointCounter=0;break;
					case "z":
					case "Z":path.closePath();pointCounter=0;break;
					case "" :break;
					default:
						pointCounter++;
						if (pointCounter%2==0){
							point.setLocation(point.getX(), Double.parseDouble(expression));
						}else{
							point.setLocation(Double.parseDouble(expression),point.getY());	
						}
						switch (penAction){
						case "M":{
							//point = transform(point);
							if ((pointCounter%2==0)&&(pointCounter>0)){
								if (null!=path.getCurrentPoint()){
									singular=false;
								}
								path.moveTo(point.getX(), point.getY());
								penAction="L";
							};break;
						}
						case "m":{
							//point = transform(point);
							if ((pointCounter%2==0)&&(pointCounter>0)){
								if (null==path.getCurrentPoint()){
									path.moveTo(point.getX(), point.getY());
								}else{
									path.moveTo(path.getCurrentPoint().getX()+point.getX(),path.getCurrentPoint().getY()+point.getY());
									singular=false;
								}
								penAction="l";
							}break;
						}
						case "L":{
							//point = transform(point);
							if ((pointCounter%2==0)&&(pointCounter>0)){
								path.lineTo(point.getX(), point.getY());
							};break;
						}
						case "l":{
							//point = transform(point);
							if ((pointCounter%2==0)&&(pointCounter>0)){
								if (null==path.getCurrentPoint()){
									path.lineTo(point.getX(), point.getY());
								}else{
									path.lineTo(path.getCurrentPoint().getX()+point.getX(),path.getCurrentPoint().getY()+point.getY());
								}
							};break;
						}
						case "H":{
							break;
						}
						case "h":{
							break;
						}
						case "V":{
							break;
						}
						case "v":{
							break;
						}
						default:{
							valid = false;
						}
						
						}
				}
	
			}
					
		}
		
		}
		
		for (double[] transformation : transformationList){
			AffineTransform affTransformation = new AffineTransform();;
			if (Math.round(transformation[0])==0){
				affTransformation.translate(transformation[1], transformation[2]);
			}
			if (Math.round(transformation[0])==1){
				affTransformation.scale(transformation[1], transformation[2]);
			}
			path.transform(affTransformation);
		}
	return path;
	}

	/*
	 * Getter for the path-points
	 * @returns the path as 3D-points
	 */
	public ArrayList<Point3D> getPathPoints() {
		ArrayList<Point3D> pathPoints = new ArrayList<Point3D>();
		PathIterator it = path.getPathIterator(null);
		while (!it.isDone()){
			double[] coords = new double[6];
			switch (it.currentSegment(coords)){
				case PathIterator.SEG_MOVETO:pathPoints.add(new Point3D(coords[0],coords[1],height));break;
				case PathIterator.SEG_LINETO:pathPoints.add(new Point3D(coords[0],coords[1],height));break;
				case PathIterator.SEG_CLOSE:break;
				default: System.err.print("SVG-Path" + this.name +"contains invalid segments (Not a Polygon?) ");
			}
			it.next();
		}
	return pathPoints;
	}
	
	public ArrayList<Line3D> getPathLines() {
		ArrayList<Point3D> pathPoints = getPathPoints();
		ArrayList<Line3D> pathLines = new ArrayList<>();
		for (int i = 1;i<pathPoints.size();i++){
			pathLines.add(new Line3D(pathPoints.get(i-1),pathPoints.get(i)));
		}
		pathLines.add(new Line3D(pathPoints.get(pathPoints.size()-1),pathPoints.get(0)));
	return pathLines;
	}
	
	/*
	 * Getter for the area
	 * @returns the surrounded area
	 */
	public Area getArea(){
	return this.area;
	}

	/*
	 * Getter for singular
	 * @returns true, if the path contains one single loop
	 */
	public boolean isSingular() {
	return singular;
	}
	
	/*
	 * Getter for valid
	 * @returns true if the path is valid
	 */
	public boolean isValid() {
	return valid;
	}
	
	@Override
	public boolean equals(Object otherObj){
		ContourLine other = (ContourLine)otherObj;
		if (this.getPathLines().equals(other.getPathLines())){
		return true;
		}
		return false;	
	}
}