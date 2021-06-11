package og;

import toools.io.file.Directory;

public abstract class DiskStore extends Store {
	public final Directory d;

	public DiskStore(Directory d) {
		this.d = d;
	}
}
