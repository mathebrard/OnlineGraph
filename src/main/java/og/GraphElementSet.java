package og;

import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;

public abstract class GraphElementSet extends ElementSet {

	protected final ElementSet impl;
	protected final Graph graph;

	public GraphElementSet(ElementSet impl, Graph g) {
		this.impl = impl;
		this.graph = g;
	}

	@Override
	public long nbEntries() {
		synchronized (graph) {
			return impl.nbEntries();
		}
	}

	@Override
	public long random() {
		synchronized (graph) {
			return impl.random();
		}
	}

	@Override
	public boolean contains(long id) {
		synchronized (graph) {
			return impl.contains(id);
		}
	}

	@Override
	public Set<String> getKeys(long id) {
		synchronized (graph) {
			return impl.getKeys(id);
		}
	}

	@Override
	public void forEach(Long2BooleanFunction c) {
		synchronized (graph) {
			impl.forEach(c);
		}
	}

	@Override
	public <E> E get(long id, String key, Supplier<E> defaultValueSupplier) {
		synchronized (graph) {
			return impl.get(id, key, defaultValueSupplier);
		}
	}

	@Override
	public void set(long id, String key, Object content) {
		synchronized (graph) {
			impl.set(id, key, content);
		}
	}

}
