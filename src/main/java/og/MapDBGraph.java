package og;

import java.util.Map;
import java.util.function.Consumer;

import toools.io.file.Directory;

public class MapDBGraph extends DiskGraph {

	public MapDBGraph(Directory d) {
		super(d, new MapDBElementSet(new Directory(d, "vertices")), new MapDBElementSet(new Directory(d, "arcs")),
				new MapDBElementSet(new Directory(d, "edges")));
	}

	@Override
	public void cleanClose() {
		((MapDBElementSet) arcs.impl).cleanClose();
		((MapDBElementSet) vertices.impl).cleanClose();
		((MapDBElementSet) edges.impl).cleanClose();
	}


}
