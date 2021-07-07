package og;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import toools.io.file.Directory;
import toools.io.file.RegularFile;

public abstract class DiskGraph<V, E> extends Graph {
	final Directory d;
	private final PersistentChanges changes;

	public DiskGraph(Directory d, ElementSet v, ElementSet e, ElementSet a) {
		super(v, e, a);
		this.d = d;
		this.changes = new PersistentChanges(new Directory(d, "changes"));

	}

	public void create() {
		d.ensureExists();
		setProperties(new HashMap<>());
	}

	@Override
	public Map<String, String> getProperties() {
		return new RegularFile(d, "properties.txt").getContentAsJavaProperties();
	}

	@Override
	public void setProperties(Map<String, String> m) {
		new RegularFile(d, "properties.txt").setContentAsJavaProperties(m);
	}

	@Override
	public String toString() {
		return d.getPath();
	}

	public boolean exists() {
		return d.exists();
	}

	@Override
	public void commitNewChange(Change c) {
		changes.add(c);
	}

	@Override
	public void forEachChange(int since, Consumer<Change> c) {
		changes.start(since, c);
	}

	public abstract void cleanClose();
}
