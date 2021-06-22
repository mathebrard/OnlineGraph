package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toools.io.Cout;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.util.Date;

public class DiskGraph<V, E> extends Graph {
	final Directory d;

	public DiskGraph(Directory d, ElementSet v, ElementSet e) {
		super(v, e);
		this.d = d;
	}

	public void create() {
		d.ensureExists();
		setProperties(new HashMap<>());
	}

	@Override
	public Map<String, String> getProperties() {
		return (Map<String, String>) new RegularFile(d, "properties.ser").getContentAsJavaObject();
	}

	@Override
	public void setProperties(Map<String, String> m) {
		new RegularFile(d, "properties.ser").setContentAsJavaObject(m);
	}

	@Override
	public String toString() {
		return d.getPath();
	}

	public boolean exists() {
		return d.exists();
	}

	@Override
	public synchronized List<Change> getHistory() {
		var f = new RegularFile(d, "history.ser");

		if (f.exists()) {
			return (List<Change>) f.getContentAsJavaObject();
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public synchronized void addChange(Change c) {
		Cout.debugSuperVisible(c);
		var f = new RegularFile(d, "history.ser");
		List<Change> l = null;

		if (f.exists()) {
			l = (List<Change>) f.getContentAsJavaObject();

			var i = l.iterator();
			var now = Date.time();

			while (i.hasNext() && i.next().date < now - 5) {
				i.remove();
			}

		} else {
			l = new ArrayList<>();
		}

		l.add(c);
		f.setContentAsJavaObject(l);
	}

}
