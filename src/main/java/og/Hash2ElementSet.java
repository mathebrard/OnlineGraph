package og;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;

public class Hash2ElementSet extends ElementSet {

	private final Map<Long, Set<String>> m;
	private final Map<String, Object> m2;

	public Hash2ElementSet(Map<Long, Set<String>> m, Map<String, Object> m2) {
		this.m = m;
		this.m2 = m2;
	}

	@Override
	public long nbEntries() {
		return m.size();
	}

	@Override
	public void add(long id) {
		if (m.containsKey(id))
			throw new IllegalArgumentException("vertex already exists " + id);

		m.put(id, new HashSet<>());
	}

	@Override
	public void remove(long id) {
		var s = m.remove(id);

		if (s == null)
			throw new IllegalArgumentException("no such element " + id);

		s.forEach(a -> m2.remove(id + "/" + a));
	}

	@Override
	public void forEach(Long2BooleanFunction c) {
		var i = m.keySet().iterator();

		while (i.hasNext()) {
			if (!c.get(i.next().longValue())) {
				return;
			}
		}
	}

	@Override
	public <E> E get(long id, String ext, Supplier<E> defaultValueSupplier) {
		if (!m.containsKey(id))
			throw new IllegalArgumentException("no such element: " + id);

		var e = m.get(id);

		if (e.contains(ext)) {
			return (E) m2.get(e + "/" + ext);
		} else if (defaultValueSupplier != null) {
			return defaultValueSupplier.get();
		} else {
			throw new IllegalArgumentException("no such ext and no default value available: " + ext);
		}
	}

	@Override
	public Set<String> getKeys(long id) {
		return m.get(id);
	}

	@Override
	public void set(long id, String ext, Object content) {
		var keys = m.get(id);
		keys.add(ext);
		m.put(id, keys);
		m2.put(id + "/" + ext, content);
	}

	@Override
	public boolean contains(long r) {
		return m.containsKey(r);
	}

	@Override
	public void clear() {
		m.clear();
		m2.clear();
	}
}
