package og;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphStorageService.EdgeInfo;
import og.GraphStorageService.VertexInfo;

public abstract class Graph implements GraphPrimitives {
	final ElementSet vertices, edges;

	// final LongArrayList emptyList = new LongArrayList();

	public Graph(ElementSet vertices, ElementSet edges) {
		this.vertices = vertices;
		this.edges = edges;
	}

	@Override
	public long nbVertices() {
		return vertices.nbEntries();
	}

	@Override
	public long nbEdges() {
		return edges.nbEntries();
	}

	@Override
	public void addVertex(long u) {
		vertices.add(u);
		vertices.set(u, "outEdges", new LongArrayList());
		vertices.set(u, "outVertices", new LongArrayList());
		var i = new VertexInfo();
		i.id = u;
		addChange(new Change.AddVertex(i));
	}

	@Override
	public void removeVertex(long u) {
		for (var e : outEdges(u)) {
			removeEdge(e);
		}

		vertices.remove(u);
		addChange(new Change.RemoveVertex(u));
	}

	@Override
	public long addEdge(long from, long to) {
		long e = ThreadLocalRandom.current().nextLong();
		edges.add(e);
		edges.set(e, "ends", new long[] { from, to });
		vertices.alter(from, "outEdges", null, (LongList outs) -> outs.add(e));
		vertices.alter(from, "outVertices", null, (LongList outs) -> outs.add(to));
		var i = new EdgeInfo();
		i.id = e;
		i.from = from;
		i.to = to;
		addChange(new Change.AddEdge(i));
		return e;
	}

	@Override
	public void removeEdge(long e) {
		var ends = ends(e);
		var from = ends[0];
		var to = ends[1];
		vertices.alter(from, "outEdges", null, (LongList set) -> set.removeLong(set.indexOf(e)));
		vertices.alter(from, "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(to)));
		edges.remove(e);
		addChange(new Change.RemoveEdge(e));
	}

	public <V> V readVertex(long u, String name, Supplier<V> defaultValueSupplier) {
		return vertices.get(u, name, defaultValueSupplier);
	}

	public void writeVertex(long u, String name, Object p) {
		vertices.set(u, name, p);
	}

	public <E> E readEdge(long e, String name, Supplier<E> defaultValueSupplier) {
		return (E) edges.get(e, name, defaultValueSupplier);
	}

	public void writeEdge(long e, String name, Object p) {
		edges.set(e, name, p);
	}

	public long[] ends(long e) {
		return (long[]) edges.get(e, "ends", null);
	}

	@Override
	public long source(long e) {
		return ends(e)[0];
	}

	@Override
	public long destination(long e) {
		return ends(e)[1];
	}

	@Override
	public LongList inEdges(long v) {
		return vertices.get(v, "inEdges", null);
	}

	@Override
	public LongList outEdges(long v) {
		return vertices.get(v, "outEdges", null);
	}

	@Override
	public long pickRandomVertex() {
		return vertices.random();
	}

	@Override
	public long pickRandomEdge() {
		return edges.random();
	}

	@Override
	public void traverseVertices(LongConsumer c) {
		vertices.forEach(c);
	}

	@Override
	public void traverseEdges(LongConsumer c) {
		edges.forEach(c);
	}

	@Override
	public void clear() {
		edges.clear();
		vertices.clear();
	}
}
