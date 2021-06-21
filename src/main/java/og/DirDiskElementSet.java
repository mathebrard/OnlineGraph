package og;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import fr.cnrs.i3s.Cache;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import toools.io.Cout;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class DirDiskElementSet extends OnDiskElementSet {

	private final Cache<Long> nbEntries;

	public DirDiskElementSet(Directory d) {
		super(d);
		this.nbEntries = new Cache<Long>(null, "nbEntries", d, () -> 0L);
	}

	@Override
	public long nbEntries() {
		return nbEntries.get();
	}

	private RegularFile file(long id, String ext) {
		return dir(id).getChildRegularFile(ext);
	}

	private Directory dir(long id) {
		return new Directory("" + id);
	}

	@Override
	public void add(long id) {
		dir(id).mkdirs();
		nbEntries.set(nbEntries.get() + 1);
	}

	@Override
	public void remove(long id) {
		var f = dir(id);

		if (!f.exists())
			throw new IllegalArgumentException("no such element " + id);

		f.deleteRecursively();
		nbEntries.set(nbEntries.get() - 1);
	}

	@Override
	public long random() {
		if (nbEntries() == 0)
			throw new IllegalArgumentException("set is empty");

		var files = d.javaFile.list();
		var f = files[ThreadLocalRandom.current().nextInt(files.length)];
		return Long.valueOf(f);
	}

	@Override
	public void forEach(LongConsumer c) {
		for (var f : d.javaFile.list()) {
			c.accept(Long.parseLong(f));
		}
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
		if (ext.equals("element"))
			throw new IllegalArgumentException();

		var f = file(id, ext);
		Cout.debug("creating " + f);
		f.setContentAsJavaObject(content);
		var elementFile = file(id, "element");
		elementFile.alterContent((Set<String> lines) -> lines.add(ext));
	}

	@Override
	public void clear() {
		d.deleteRecursively();
		d.create();
		nbEntries.set(0L);
	}
}
