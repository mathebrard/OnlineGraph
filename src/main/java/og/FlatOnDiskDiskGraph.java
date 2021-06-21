package og;

import toools.io.file.Directory;

public class FlatOnDiskDiskGraph extends DiskGraph {

	public FlatOnDiskDiskGraph(Directory d) {
		super(d, new FlatOnDiskElementSet(new Directory(d, "vertices")),
				new FlatOnDiskElementSet(new Directory(d, "edges")));
	}

	@Override
	public void create() {
		super.create();
		((FlatOnDiskElementSet) vertices).ensureExists();
		((FlatOnDiskElementSet) edges).ensureExists();
	}
}
