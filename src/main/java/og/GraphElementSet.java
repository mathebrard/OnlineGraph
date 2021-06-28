package og;

import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;

public abstract class GraphElementSet extends ElementSet {

	protected final ElementSet impl;
	protected final Graph graph;

	public GraphElementSet(ElementSet set, Graph g) {
		this.impl = set;
		this.graph = g;
	}

	@Override
	public long nbEntries() {
		return impl.nbEntries();
	}

	@Override
	public long random() {
		return impl.random();
	}

	@Override
	public boolean contains(long id) {
		return impl.contains(id);
	}

	@Override
	public Set<String> getKeys(long id) {
		return impl.getKeys(id);
	}

	@Override
	public void forEach(Long2BooleanFunction c) {
		impl.forEach(c);
	}

	@Override
	public <E> E get(long id, String key, Supplier<E> defaultValueSupplier) {
		return impl.get(id, key, defaultValueSupplier);
	}

	@Override
	public void set(long id, String key, Object content) {
		impl.set(id, key, content);
	}


}
