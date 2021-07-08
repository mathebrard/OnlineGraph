package og;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class PersistentChanges {
	private final Directory d;

	public PersistentChanges(Directory d) {
		this.d = d;
		d.ensureExists();
	}

	public <E> void start(int i, Consumer<E> c) {
		for (int b = i / 1000;; ++b) {
			var f = new RegularFile(d, b + ".ser");

			if (!f.exists())
				break;

			var l = (List<E>) f.getContentAsJavaObject();

			// if first block
			if (b == i / 1000) {
				l = l.subList(i % 1000, l.size());
			}

			l.forEach(c);
		}
	}

	public void add(Object o) {
		int len = size();
		int bi = len / 1000;
		List<Object> l;
		var f = new RegularFile(d, bi + ".ser");

		if (f.exists()) {
			l = (List<Object>) f.getContentAsJavaObject();
		} else {
			l = new ArrayList<>();
		}

		l.add(o);
		f.setContentAsJavaObject(l);

		new RegularFile(d, "size.ser").setContentAsJavaObject(len + 1);
	}

	public int size() {
		return (Integer) new RegularFile(d, "size.ser").getContentAsJavaObject();
	}
}
