package og;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongConsumer;

public class RAMElementSet extends ElementSet {

	private final Long2ObjectMap<Map<String, Object>> m = new Long2ObjectOpenHashMap<>();

	@Override
	public long nbEntries() {
		return m.size();
	}

	@Override
	public void add(long id) {
		m.put(id, new HashMap<>());
	}

	@Override
	public void remove(long id) {
		m.remove(id);
	}

	@Override
	public long random() {
		if (nbEntries() == 0)
			throw new IllegalArgumentException("set is empty");

		var it = m.keySet().iterator();

		for (long i = ThreadLocalRandom.current().nextLong(nbEntries()); i > 0; --i) {
			it.nextLong();
		}

		return it.nextLong();
	}

	@Override
	public void forEach(LongConsumer c) {
		var i = m.keySet().iterator();

		while (i.hasNext()) {
			c.accept(i.nextLong());
		}
	}

	@Override
	public <E> E get(long id, String ext, Supplier<E> defaultValueSupplier) {
		if (!m.containsKey(id))
			throw new IllegalArgumentException("no such element: " + id);

		var e = m.get(id);

		if (e.containsKey(ext)) {
			return (E) e.get(ext);
		} else if (defaultValueSupplier != null) {
			return defaultValueSupplier.get();
		} else {
			throw new IllegalArgumentException("no such ext and no default value available: " + ext);
		}
	}

	@Override
	public Set<String> get(long id) {
		return m.get(id).keySet();
	}

	@Override
	public void set(long id, String ext, Object content) {
		m.get(id).put(ext, content);
	}

	@Override
	public void clear() {
		m.clear();
	}
}