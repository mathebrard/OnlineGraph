package og;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphStorageService.EdgeInfo;
import og.GraphStorageService.VertexInfo;

public abstract class Graph {
	public final ElementSet vertices, edges;

	public Graph(ElementSet vertices, ElementSet edges) {
		this.vertices = vertices;
		this.edges = edges;
	}

	// graph-related methods

	public abstract List<Change> getHistory();

	public abstract void addChange(Change c);

	public abstract Map<String, String> getProperties();

	public abstract void setProperties(Map<String, String> m);

	public boolean isIsolated(long u) {
		return outEdges(u).isEmpty() && inEdges(u).isEmpty();
	}

	public boolean isSink(long u) {
		return outEdges(u).isEmpty();
	}

	public void addVertex(long u) {
		vertices.add(u);
		vertices.set(u, "outEdges", new LongArrayList());
		vertices.set(u, "inEdges", new LongArrayList());
		vertices.set(u, "outVertices", new LongArrayList());
		var i = new VertexInfo();
		i.id = u;
		addChange(new Change.AddVertex(i));
	}

	public void removeVertex(long u) {
		for (var e : new LongArrayList(outEdges(u))) {
			removeEdge(e);
		}

		for (var e : new LongArrayList(inEdges(u))) {
			removeEdge(e);
		}

		vertices.remove(u);
		addChange(new Change.RemoveVertex(u));
	}

	public long addEdge(long from, long to) {
		long e = ThreadLocalRandom.current().nextLong();
		edges.add(e);
		edges.set(e, "ends", new long[] { from, to });
		vertices.alter(from, "outEdges", () -> new LongArrayList(), (LongList outs) -> outs.add(e));
		vertices.alter(to, "inEdges", () -> new LongArrayList(), (LongList ins) -> ins.add(e));
//		vertices.alter(from, "outVertices", null, (LongList outs) -> outs.add(to));
		var i = new EdgeInfo();
		i.id = e;
		i.from = from;
		i.to = to;
		addChange(new Change.AddEdge(i));
		return e;
	}

	public void check() {
		edges.forEach(e -> {
			var ends = ends(e);
			var from = ends[0];

			if (!vertices.contains(from))
				throw new IllegalStateException("unknown source : " + from);

			var to = ends[1];

			if (!vertices.contains(to))
				throw new IllegalStateException("unknown destination : " + to);

			return true;
		});

		vertices.forEach(v -> {
			for (var e : outEdges(v)) {
				if (!edges.contains(e))
					throw new IllegalStateException("unknown out edge : " + e);
			}

			for (var e : inEdges(v)) {
				if (!edges.contains(e))
					throw new IllegalStateException("unknown in edge : " + e);
			}

			return true;
		});
	}

	public void removeEdge(long e) {
		var ends = ends(e);
		var from = ends[0];
		var to = ends[1];
		vertices.alter(from, "outEdges", null, (LongList set) -> set.removeLong(set.indexOf(e)));
		vertices.alter(to, "inEdges", null, (LongList set) -> set.removeLong(set.indexOf(e)));
//		vertices.alter(from, "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(to)));
		edges.remove(e);
		addChange(new Change.RemoveEdge(e));
	}

	public long[] ends(long e) {
		return (long[]) edges.get(e, "ends", null);
	}

	public long source(long e) {
		return ends(e)[0];
	}

	public long destination(long e) {
		return ends(e)[1];
	}

	public LongList inEdges(long v) {
		return vertices.get(v, "inEdges", () -> emptyList);
	}

	public final LongList emptyList = new LongArrayList();

	public LongList outEdges(long v) {
		return vertices.get(v, "outEdges", () -> emptyList);
	}

	public void traverseVertices(Long2BooleanFunction c) {
		vertices.forEach(c);
	}

	public void traverseEdges(Long2BooleanFunction c) {
		edges.forEach(c);
	}

	public void clear() {
		edges.clear();
		vertices.clear();
	}
}
