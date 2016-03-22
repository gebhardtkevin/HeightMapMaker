package backend;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import backend.geom.ContourLine;
import backend.geom.Point3D;
import backend.geom.Triangle;
import backend.lists.ContourList;
import backend.lists.TriangleList;
/*
 * appends the HeightMap to the file
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class HeightMapCreator {

	/*
	 * starts the heighmap creation process. after creating the object no further action is needed
	 * mainly gehts the writers ready and starts the further process  
	 * @param fileWriter for the gnu-file. Is for debugging only and can be removed later
	 * @param xml-document containing the contourline-paths
	 */
	public HeightMapCreator(Document contourDoc, String savePath, FileWriter contourFile){
			XMLOutputFactory factory      = XMLOutputFactory.newInstance();
			try {
			     XMLStreamWriter writer = factory.createXMLStreamWriter(contourFile);
			 	 makeHeightMap(writer,contourDoc,savePath);
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * writes the heightmap to the scl-file
		 * @param Streamwriter for writing the nodes without getting into memory trouble
		 * @param document containing the svg-paths
		 */
		private void makeHeightMap(XMLStreamWriter writer, Document contourDoc,String savePath) {		
			Point2D dimensions = getDimensions(contourDoc);
			double baseHeight;
			try{
			baseHeight = Double.parseDouble(((Element) contourDoc.getElementsByTagName("scl").item(0)).getAttribute("ground"));
			} 
			catch (NumberFormatException e){
			baseHeight = 0;	
			}
			try {
				writer.writeStartDocument();
				writer.writeStartElement("scl");
				writer.writeAttribute("xmlns", "http://www.gebhardtKevin.de/scsu");
				writer.writeAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
				writer.writeAttribute("xsi:schemaLocation", "http://www.gebhardtKevin.de/scsu scl.xsd");
				writer.writeAttribute("version", "1.1");
				writer.writeAttribute("width", String.valueOf(dimensions.getX()));
				writer.writeAttribute("heigth", String.valueOf(dimensions.getY()));
				writer.writeAttribute("ground",String.valueOf(baseHeight));
				
				writer.writeStartElement("paths");
				NodeList paths = contourDoc.getElementsByTagName("path");
				for (int i=0;i<paths.getLength();i++){
					Element path = (Element)paths.item(i);
					writer.writeStartElement("path");
					writer.writeAttribute("style", path.getAttribute("style"));
					writer.writeAttribute("id", path.getAttribute("id"));
					writer.writeAttribute("d", path.getAttribute("d"));
					writer.writeEndElement();
				}
				writer.writeEndElement();
				
				writer.writeStartElement("heightMap");
				
				ContourList contourList = new ContourList(getJavaContourList(contourDoc));
				
				TriangleList triangleList = new Triangulator(dimensions, baseHeight, contourList).getTriangles();
				write1TriangleFile(triangleList, contourList, savePath.substring(0,savePath.indexOf(".scl")));
				DecimalFormat df = new DecimalFormat("0.###");
				for (int x = 0;x<=dimensions.getX();x++){
					writer.writeStartElement("X");
					writer.writeAttribute("x", String.valueOf(x));
					for (int y = 0;y<=dimensions.getY();y++){
						writer.writeStartElement("p");
						writer.writeAttribute("y", String.valueOf(y));
						writer.writeAttribute("h", df.format(getHeight(triangleList, x, y)));
						writer.writeEndElement();
					}
					writer.writeEndElement();
				}
				writer.writeEndElement();
				writer.writeEndDocument();
				writer.flush();
				writer.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}

		/*
		 * retrieving the Terrain-size from the svg-Document
		 * @param document containing the svg-paths
		 * @returns a point containing the dimensions
		 */
		private Point getDimensions(Document contourDoc){
			double hDim = -1;
			double wDim = -1;
			
			String hDimStr = ((Element)contourDoc.getElementsByTagName("scl").item(0)).getAttribute("heigth");
			String wDimStr = ((Element)contourDoc.getElementsByTagName("scl").item(0)).getAttribute("width");
			
			try{
				hDim = Double.parseDouble(hDimStr);
			}catch (NumberFormatException e){
				//no height defined
				hDim=-1;
			}
			try{
				wDim = Double.parseDouble(wDimStr);
			}catch (NumberFormatException e){
				//no width defined
				wDim=-1;
			}
			
			Point dimensions = new Point((int)wDim,(int)hDim);
		return dimensions;
		}
		
		/*
		 * pulls the contourlines from the svg-paths
		 * @param document containing the svg-paths
		 * @returns a list of paths, representing the contourlines
		 */
		private ArrayList<ContourLine> getJavaContourList(Document contourDoc){		
			ArrayList<ContourLine> pathList = new ArrayList<ContourLine>();
			NodeList paths = contourDoc.getElementsByTagName("path");
			for (int i=0;i<paths.getLength();i++){
				Element path = (Element) paths.item(i);
				pathList.add(new ContourLine(path));
			}
			return pathList;
		}

	/*
	 * calculates the (interpolated) height of a point in the given triangulation
	 * @param the triangulation
	 * @param x coordinate of the point, that will be interpolated
	 * @param y coordinate of the point, that will be interpolated
	 * @returns height of the point
	 */
	public double getHeight(TriangleList triangleList, double x, double y){	
		Point2D interpolationPoint = new Point2D.Double(x,y);
		for (Triangle triangle:triangleList){
			if (triangle.contains(interpolationPoint)){
				if (x==1&&y==91){
					@SuppressWarnings("unused")
					int stop = 1;
				}
				double height = triangle.interpolate(interpolationPoint);
				return height;
			}
		}
		return 0;
	}
	
	/*
//	 * writes a debug file with all the triangles as svg. Debug only. May be removed later
//	 * @param all the triangles
//	 * @param all the contourlines
//	 */
	static void write1TriangleFile(TriangleList triangleList, ContourList contours,String savePath){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(savePath + "debug.svg")));
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" id=\"svg3338\" viewBox=\"0 0 744.09448819 1052.3622047\" height=\"297mm\" width=\"210mm\">");
			int i=0;
			for (Triangle triangle : triangleList){
				writer.write("<path style=\"fill:blue;fill-rule:evenodd;stroke:#000000;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\" id=\"id_" + i + "\" d=\"M " + triangle.getA().getX() + "," + triangle.getA().getY() + " L " + triangle.getB().getX() + "," + triangle.getB().getY() + " L " + triangle.getC().getX() + "," + triangle.getC().getY() + " Z\"/>");
				i++;
			}
			if (null!=contours){
				for (ContourLine contour : contours){
					writer.write("<path style=\"fill:none;fill-rule:evenodd;stroke:#00FF00;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\" id=\"id_" + i + "\" d=\"M ");
					for (Point3D point :contour.getPathPoints()){
						writer.write(point.getX() + " " +point.getY() + " ");
					}
					writer.write(" Z\"/>");
					i++;
				}
			}
			writer.write("</svg>");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	static public void writeSidesFile(SideList sidesList, ContourList contours,String savePath){
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(savePath + "debugSides.svg")));
//			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><svg xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" id=\"svg3338\" viewBox=\"0 0 744.09448819 1052.3622047\" height=\"297mm\" width=\"210mm\">");
//			int i=0;
//			for (Line3D line : sidesList){
//				writer.write("<path style=\"fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\" id=\"id_" + i + "\" d=\"M " + line.getP1().getX() + "," + line.getP1().getY() + " L " + line.getP2().getX() + "," + line.getP2().getY() + " Z\"/>");
//				i++;
//			}
//			if (null!=contours){
//				for (ContourLine contour : contours){
//					writer.write("<path style=\"fill:none;fill-rule:evenodd;stroke:#FF0000;stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\" id=\"id_" + i + "\" d=\"M ");
//					for (Point3D point :contour.getPathPoints()){
//						writer.write(point.getX() + " " +point.getY() + " ");
//					}
//					writer.write(" Z\"/>");
//					i++;
//				}
//			}
//			writer.write("</svg>");
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
