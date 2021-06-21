package og;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import fr.cnrs.i3s.Cache;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class FlatOnDiskElementSet extends OnDiskElementSet {

	private final Cache<Long> nbEntries;

	public FlatOnDiskElementSet(Directory d) {
		super(d);
		this.nbEntries = new Cache<Long>(null, "nbEntries", d, () -> 0L);
	}

	@Override
	public long nbEntries() {
		return nbEntries.get();
	}

	private RegularFile file(long id, String ext) {
		return d.getChildRegularFile(id + (ext == null ? "" : "." + ext));
	}

	@Override
	public void add(long id) {
		file(id, null).setContentAsJavaObject(new HashSet<String>());
		nbEntries.set(nbEntries.get() + 1);
	}

	@Override
	public void remove(long id) {
		var f = file(id, null);

		if (!f.exists())
			throw new IllegalArgumentException("no such element " + id);

		((Set<String>) f.getContentAsJavaObject()).forEach(ext -> file(id, ext).delete());
		f.delete();
		nbEntries.set(nbEntries.get() - 1);
	}

	@Override
	public long random() {
		if (nbEntries() == 0)
			throw new IllegalArgumentException("set is empty");

		var files = d.javaFile.list();

		while (true) {
			var f = files[ThreadLocalRandom.current().nextInt(files.length)];

			// file has no extension
			if (f.indexOf(".") == -1) {
				return filename2id(f);
			}
		}
	}

	private long filename2id(String n) {
		int i = n.lastIndexOf('.');

		if (i > 0) {
			n = n.substring(0, i);
		}

		return Long.parseLong(n);
	}

	@Override
	public void forEach(LongConsumer c) {
		d.listRegularFiles().forEach(f -> {
			String n = f.getName();

			if (f.getName().indexOf(".") == -1) {
				c.accept(Long.parseLong(f.getName()));
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
			var defaultValue = defaultValueSupplier.get();
//			f.setContentAsJavaObject(defaultValue);
			return defaultValue;
		}
	}

	@Override
	public Set<String> get(long id) {
		return (Set<String>) file(id, null).getContentAsJavaObject();
	}

	@Override
	public void set(long id, String ext, Object content) {
		var f = file(id, ext);
//		Cout.debug("creating " + f);
		f.setContentAsJavaObject(content);
		var elementFile = file(id, null);
		elementFile.alterContent((Set<String> lines) -> lines.add(ext));
	}

	@Override
	public void clear() {
		d.deleteRecursively();
		d.create();
		nbEntries.set(0L);
	}
}
