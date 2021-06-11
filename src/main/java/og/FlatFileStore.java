package og;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class FlatFileStore extends DiskStore {
	private long nbEntries = 0;

	public FlatFileStore(Directory d) {
		super(d);
	}

	@Override
	public long nbEntries() {
		return nbEntries;
	}

	private RegularFile file(long id, String ext) {
		return d.getChildRegularFile(id + "." + ext);
	}

	@Override
	public void add(long name) {
		file(name, "element").create();
		++nbEntries;
	}

	@Override
	public void remove(long name) {
		var f = file(name, "element");

		for (var ext : f.getLines()) {
			file(name, ext).delete();
		}

		f.delete();
		--nbEntries;
	}

	@Override
	public long random() {
		var files = d.javaFile.list();

		while (true) {
			var f = files[ThreadLocalRandom.current().nextInt(files.length)];

			if (f.endsWith(".element")) {
				return filename2id(f);
			}
		}
	}

	private long filename2id(String n) {
		return Long.valueOf(n.substring(0, n.lastIndexOf('.')));
	}

	@Override
	public void files(LongConsumer c) {
		d.listRegularFiles().forEach(f -> {
			String n = f.getName();

			if (n.endsWith(".element")) {
				long id = filename2id(n);
				c.accept(id);
			}
		});
	}

	@Override
	public <E> E get(long id, String ext, Supplier<E> defaultValueSupplier) {
		var f = file(id, ext);

		if (f.exists()) {
			return (E) f.getContentAsJavaObject();
		} else if (defaultValueSupplier == null) {
			throw new IllegalArgumentException("no such file  " + f + " and no default value available");
		} else {
			return defaultValueSupplier.get();
		}
	}

	@Override

	public List<String> get(long id) {
		return (List<String>) file(id, null).getLines();
	}

	@Override
	public void set(long id, String ext, Object content) {
		file(id, ext).setContentAsJavaObject(content);
		var elementFile = file(id, "element");
		var lines = new HashSet<>(elementFile.getLines());
		lines.add(ext);
		elementFile.setLines(lines);
	}
}
