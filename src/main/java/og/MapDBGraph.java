package og;

import toools.io.file.Directory;

public class MapDBGraph extends DiskGraph {

	public MapDBGraph(Directory d) {
		super(d, new MapDBElementSet(new Directory(d, "vertices")), new MapDBElementSet(new Directory(d, "arcs")),
				new MapDBElementSet(new Directory(d, "edges")));
	}

}
