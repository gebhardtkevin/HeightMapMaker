package backend;

/*
 * svg2scl is a inkscape plugin for converting contourlines drawn in inkscape to a heightmap. It takes svg-paths, marked as countourlines by their id, +
 * validates them, and exportes them to a scl-File. Furthermore a grid of interpolated heights is calculated from the contourline and is also written to the scl-file
 *
 * @since 1.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class HeightMapMaker{
	/*
	 * suffix for svg files
	 */
	private final static String FILETYPESVG = ".svg";
	/*
	 * suffix for scl-files
	 */
	private final static String FILETYPECL = ".scl";
	
	private final static String pathString="C:\\Users\\FusionSystems\\workspace\\svg2scl\\src\\test\\resources\\example.svg";
	private final static String savePathString="C:\\Users\\FusionSystems\\workspace\\svg2scl\\src\\test\\resources\\test.scl";
	private final static boolean DEBUG = false;
	/*
	 * manages the arguments and paths and calls the ClExtractor 
	 */
	
	public static void main(String[] args) {
		String path=null;
		String savePath=null;
		if (args.length == 2) {
			path = args[0];
			savePath = args[1];
		}else if (DEBUG){
			path = pathString;
			savePath = savePathString;
		}
		
		
		else{
				System.err.print(help());
				System.exit(1);
		}
		if (!path.toLowerCase().contains(".")){
			path = path.concat(FILETYPESVG);
		}
		if (!savePath.toLowerCase().endsWith(FILETYPECL)){
			path = path.concat(FILETYPECL);
		}
		new CLExtractor(path, savePath);
		System.err.print("Konvertierung erfolgreich. Keine Fehler :)");
		System.exit(0);
	}
	
	/*
	 * prints a help-output in case the arguments are wrong
	 */
	private static String help(){
		return String.format(
		"NAME%n" + 
		"   svg2CL - generates CountorLine-Files for SceneSuite from SVG%n" + 
		"%n" +			
		"%n" +
		"SYNOPSIS%n" +
		"   svg2cl  PATH_TO_SVG  PATH_FOR_CL ...%n" +
		"%n" +			
		"%n" +
		"DESCRIPTION%n" +
		"   svg2cl takes a SVG-File, checks it for valid countour-lines and saves the %n" +
		"   found countour-lines in a file ready for parsing into the Scene-Suite to- %n" +
		"   pology. Valid Countor-lines are SVG-Nodes of the type Element with an id  %n" +
		"   in the Format [Counter_]CL_height%n" +
		"   Arguments:%n" +
		"    	PATH_TO_SVG ... Path to the SVG-File to be parsed%n" +
		"    	PATH_FOR_CL ... Path where the CL-File for SceneSuite should be stored%n" +
		"%n" +			
		"%n" +
		"EXIT STATUS%n" +
		"       0                     All good :) %n"+
		"%n" +
		"       -1                    SVG file not found%n"+
		"%n" +
		"       -2                    Can not create output File%n"+
		"%n" +
		"        1                    Failure in parsing the Countour-Lines%n"+
		"%n" +
		"%n");
	}
}
