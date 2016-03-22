package backend.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({TriangleTest.class, Point3DTest.class, Line3DTest.class, ContourListTest.class, ContourLineTest.class})
public class svg2sclTestSuite {
//	private String testInput1 = "\\resources\\testinput1.svg";
//	private String testInput2 = "\\resources\\testinput1.svg";
	
//	private String savePath = "\\resources\\output.scl";
//	private String saveTriangles = "\\resources\\triangles.svg";

// An IntegrationTest for the whole Process. checks for valid scl-files and tries to check, if the Trianglulation is fine.
@Test
public void svg2sclTest () {
//	TriangleTest triangleTest = new TriangleTest();
	//triangleTest.ConstructorsShouldDoTheSame();
	  // MyClass is tested
    //Svg2scl 
	//tester = new Svg2scl();
   // String[] args = new String[]{testInput1,savePath};
   // Svg2scl.main(args);
    // assert statements
    //readOutput
    
    //check for valid xml
    
    //check for different values in heightmap
    
    //check for right value on line
    
    //check or baseHeigth
    
    //check, if triangles overlap
    
    //check if triangfles fill the whole area
    //assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));
    //assertEquals("0 x 10 must be 0", 0, tester.multiply(0, 10));
    //assertEquals("0 x 0 must be 0", 0, tester.multiply(0, 0));
  }
//Triangle

}
