package backend.lists;

import java.util.Collection;
import java.util.HashSet;

import backend.geom.Triangle;

/*
 * Convinence class for a list of the triangles
 * @since 2.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class TriangleList extends HashSet<Triangle>{

	private static final long serialVersionUID = 3077977010490732372L;

	public TriangleList(Collection<Triangle> triangles){
		this.addAll(triangles);
	}
	
	public TriangleList(){
	}
}