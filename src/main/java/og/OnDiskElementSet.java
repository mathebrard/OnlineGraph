package og;

import toools.io.file.Directory;

public abstract class OnDiskElementSet extends ElementSet {
	public final Directory d;

	public OnDiskElementSet(Directory d) {
		this.d = d;
	}

	public void ensureExists() {
		if (!d.exists()) {
			d.mkdirs();
		}
	}

}
