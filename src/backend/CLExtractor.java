package backend;

import java.awt.geom.Area;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import backend.geom.ContourLine;
/*
 * Converts the svg-path into contourlines and checks for validity
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class CLExtractor{
	
	private static final String SVG_PATH_NAME = "path";
	private static final String SCL_HEADER_1 = 
			String.format(
					 "<?xml version=\"1.0\"?>%n" +
					 "%n" +
					 "<scl xmlns=\"http://www.fusionsystems.de/scenesuite/scl\"%n" +
					 "     version=\"1.1\"%n" + 
					 "     xml:lang=\"en\"%n" + 
					 "     xmlns:scenesuite=\"http://www.fusionsystems.de/scenesuite\"%n");
	private static final String SCL_HEADER_2 = 
			String.format(
					 ">%n");
	private static final String SCL_FOOTER = 
			String.format("</scl>");
	private static final String PATH_HEADER =  
			String.format(
			"<path%n" + 
		    "style=\"fill:none;stroke:#3f3f3f;stroke-width:0.05px;stroke-opacity:1\"%n");
	private static final String PATH_ID_FRONT = 
			String.format(
			"id=\""); 
	private static final String PATH_ID_BACK = 
			String.format(
			"\"%n"); 
	private static final String PATH_D_FRONT = 
			String.format(
			"d=\""); 
	private static final String PATH_D_BACK = 
			String.format(
			"\"%n");  
	private static final String PATH_FOOTER = 
			String.format(		
			"/>%n" + 
			"%n");
	
	/*
	 * width of the svg-image and also the terrain
	 */
	private double imageHeight;
	/*
	 * heigth of the svg-image and also the terrain
	 */
	private double imageWidth;
	/*
	 * height outside of the outer contourline
	 */
	private String groundHeight;
	/*
	 *Area enclosing a point (not necesseraly "lower", but named for better understanding) 	
	 */
	public Area areaLow;
	/*
	 *next area not enclosing a point (not necesseraly "higher", but named for better understanding) 	
	 */
	public Area areaHigh;
	/*
	 * temporary area as intersection of two areas
	 */
	public Area intersectionArea;
	
	/*
	 * Constructing an object will start the extractor
	 * @param path to the svg file
	 * @param path to the save file
	 */
	public CLExtractor(String SVGPath, String savePath){
		try {
			if (saveCL(savePath+".temp", extractCL(readSVG(SVGPath)))){
				new HeightMapCreator(readSVG(savePath+".temp"),savePath,new FileWriter(savePath));
				new File(savePath+".temp").delete();
			}
		} catch (TransformerFactoryConfigurationError | IOException e) {
			System.err.print("SVG-File: " + SVGPath + " not found. ");
			e.printStackTrace();
		}
	}
	/*
	 * the "real" extractor
	 * @param the svg-document to extract the paths from
	 * @return the contourlines
	 */
	private ArrayList<ContourLine> extractCL(Document svg) {
		groundHeight = ((Element)svg.getElementsByTagName("svg").item(0)).getAttribute("scsuGround");
		String imageHeightString = ((Element)svg.getElementsByTagName("svg").item(0)).getAttribute("height");
		String testImageHeigth = imageHeightString.replaceAll("[\\s+a-zA-Z]","");
		if (imageHeightString.equals(testImageHeigth)){
			try{
				imageHeight = Double.parseDouble(imageHeightString);
			}catch (NumberFormatException e){
				//no height defined
				imageHeight=-1;
			}
		}else{
			System.err.print("Bildgröße muss in Pixeln angegeben werden!");
			System.exit(1);
		}
		
		String imageWidthString = ((Element)svg.getElementsByTagName("svg").item(0)).getAttribute("width");
		String testImageWidthString = imageWidthString.replaceAll("[\\s+a-zA-Z]","");
		if (imageWidthString.equals(testImageWidthString)){
			try{
				imageWidth = Double.parseDouble(imageWidthString);
			}catch (NumberFormatException e){
				//no height defined
				imageWidth=-1;
			}
		}else{
			System.err.print("Bildgröße muss in Pixeln angegeben werden!");
			System.exit(1);
		}
		NodeList pathList = svg.getElementsByTagName(SVG_PATH_NAME);
		ArrayList<ContourLine> list = new ArrayList<ContourLine>();
		
		for (int i=0;i<pathList.getLength();i++){
			Node path = pathList.item(i);
			if (path.getNodeType()==Node.ELEMENT_NODE){
				if (((Element)path).getAttribute(ContourLine.NAME_ATTRIBUTE).contains(ContourLine.ID_PREFIX))
					list.add(new ContourLine((Element)path));
			}
		}
		return list;
	}
	/*
	 * writes the valid contourlines to the scl-files (heightmap will be created later)
	 * @param the path, where to save the scl-file
	 * @param the extracted contourlines
	 * @returns true on success
	 */
	private boolean saveCL(String savePath, ArrayList<ContourLine> paths){
		if (!checkCL(paths)){
			return false;
		}
		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savePath), "utf-8"));
			writer.write(SCL_HEADER_1);
			if (imageWidth>-1){
				writer.write(String.format("width=\"" + imageWidth + "\"%n"));
			}
			if (imageHeight>-1){
				writer.write(String.format("heigth=\"" + imageHeight + "\"%n"));				
			}
			writer.write(String.format("ground=\"" + groundHeight + "\"%n"));
			writer.write(SCL_HEADER_2);
			for (ContourLine path : paths){			
				writer.write(PATH_HEADER);
				writer.write(PATH_ID_FRONT + path.getName() + PATH_ID_BACK);
				writer.write(PATH_D_FRONT + path.getCalculatedPath() + PATH_D_BACK);
				writer.write(PATH_FOOTER);
			}
			writer.write(SCL_FOOTER);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * checks the contourlines for validity
	 * @param the contourlines to check
	 * @returns true, if all contourlines are valid
	 */
	private boolean checkCL(ArrayList<ContourLine> unchecked){	
		String notClosed  = "";
		String intersects = "";			
		String notValid   = "";
		//check for unclosed lines
		for (ContourLine check : unchecked){
			if (!(check.getOriginalPath().trim().endsWith("z")||check.getOriginalPath().trim().endsWith("Z"))){
				if (!(check.getJavaPath().getCurrentPoint().equals(check.getFirstPoint()))){
					notClosed+=check.getName() + ";";
				}
			}			
		}
		if (unchecked.size()>1){
			ContourLine firstUnchecked = unchecked.get(0);
			for (int i=1;i<unchecked.size();i++){
				ContourLine secondUnchecked = unchecked.get(i);
				intersects += checkForIntersection(firstUnchecked,secondUnchecked);
				firstUnchecked=secondUnchecked;
			}
		}

		//check for validy
		for (ContourLine check : unchecked){
			if (!check.isValid()||!check.isSingular()){
				notValid+=check.getName() + ";";
			}
		}			

		if (intersects.isEmpty()&&notClosed.isEmpty()&&notValid.isEmpty()){
			return true;
		}else{
			if (!intersects.isEmpty()){
				String[] intersectList = intersects.split(";");
				for (String intersection:intersectList){
					System.err.print("Fehler: Ueberschneidung zwischen den Linien " + intersection.replace(",", " und ") + "!" ); 
				}
			}
			if (!notClosed.isEmpty()){
				String[] notClosedList = notClosed.split(";");
				for (String closeFailure:notClosedList){
					System.err.print("Fehler: " + closeFailure + " nicht geschlossen! "); 
				}
			}
			if (!notValid.isEmpty()){
				String[] notValidList = notValid.split(";");
				for (String validFailure:notValidList){
					System.err.print("Fehler: " + validFailure + " ist kein gueltiger Pfad! Eventuell kein Polygon? "); 
				}
			}
			return false;
		}
	}
	/*
	 * checks two contourlines for intersection
	 * @param first contourline
	 * @param second contourline
	 * @returns an empty string, if the contourlines don't intersect, the names of the contourlines otherwise
	 */
	private String checkForIntersection(ContourLine countourLine, ContourLine countourLine2) {
		if (countourLine.getHeight()<countourLine2.getHeight()){
			areaLow = new Area(countourLine.getJavaPath());
			areaHigh = new Area(countourLine2.getJavaPath());
		}else{
			areaLow = new Area(countourLine2.getJavaPath());
			areaHigh = new Area(countourLine.getJavaPath());
		}
		intersectionArea = (Area) areaHigh.clone();
	    intersectionArea.intersect(areaLow);
	    if (!intersectionArea.isEmpty()){
	    	if (!intersectionArea.equals(areaHigh)&&!intersectionArea.equals(areaLow)){
	    		return countourLine.getName() + "," + countourLine2.getName()+";"; 
	    	}
	    }
	    return "";
	}

	/*
	 * parses the svg to document via DOM-Parser
	 * @param path to the svg
	 * @return the svg-DOM-document
	 */
	private static Document readSVG(String path) throws FileNotFoundException {
		File svgFile = new File(path);
		if (!svgFile.exists()){
			throw new FileNotFoundException();
		}
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(svgFile);
		} catch (SAXException | IOException e) {
			System.err.print("Not a valid SVG-File: " + path + ". ");
			e.printStackTrace();
		}		
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		return doc;
	}
}