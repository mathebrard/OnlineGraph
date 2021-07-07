package og;

import java.util.Map;
import java.util.function.Consumer;

import toools.io.file.Directory;

public class FlatOnDiskDiskGraph extends DiskGraph {

	public FlatOnDiskDiskGraph(Directory d) {
		super(d, new FlatOnDiskElementSet(new Directory(d, "vertices")),
				new FlatOnDiskElementSet(new Directory(d, "edges")),
				new FlatOnDiskElementSet(new Directory(d, "arcs")));
	}

	@Override
	public void create() {
		super.create();
		((FlatOnDiskElementSet) vertices.impl).ensureExists();
		((FlatOnDiskElementSet) arcs.impl).ensureExists();
		((FlatOnDiskElementSet) edges.impl).ensureExists();
	}

	@Override
	public void cleanClose() {
		// nothing to do here as changes are synchronous
	}

}
