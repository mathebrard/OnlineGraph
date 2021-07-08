package og;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class HashElementSet extends ElementSet {

	private final Long2ObjectMap<Map<String, Object>> m = new Long2ObjectOpenHashMap<>();

	@Override
	public long nbEntries() {
		return m.size();
	}

	@Override
	public void add(long id) {
		if (m.containsKey(id))
			throw new IllegalArgumentException("vertex already exists " + id);

		m.put(id, new HashMap<>());
	}

	@Override
	public void remove(long id) {
		if (!m.containsKey(id))
			throw new IllegalArgumentException("no such element " + id);

		m.remove(id);
	}

	@Override
	public void forEach(Long2BooleanFunction c) {
		var i = m.keySet().iterator();

		while (i.hasNext()) {
			if (!c.get(i.nextLong())) {
				return;
			}
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
	public Set<String> getKeys(long id) {
		return m.get(id).keySet();
	}

	@Override
	public void set(long id, String ext, Object content) {
		m.get(id).put(ext, content);
	}

	@Override
	public boolean contains(long r) {
		return m.containsKey(r);
	}

	@Override
	public void clear() {
		m.clear();
	}
}
