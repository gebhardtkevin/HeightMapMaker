package backend.lists;

import java.util.HashSet;
import java.util.Iterator;

import backend.geom.Line3D;
import backend.geom.Point3D;
/*
 * Convinence class for a list of the sides
 *
 * @since 2.0
 * @Version 1.0
 * @author Kevin Gebhardt gebhardt.kevin@gmail.com
 */
public class SideList extends HashSet<Line3D>{

private static final long serialVersionUID = 4924785819522487160L;
	
	public SideList getConnected(Point3D point){
		SideList connected = new SideList();
		Iterator<Line3D> it = this.iterator();
		while(it.hasNext()){
			Line3D side = it.next();
			if (side.getP1().equals(point.flat())||side.getP2().equals(point.flat())){
				connected.add(side);
			}	
		}
		return connected;
	}

}