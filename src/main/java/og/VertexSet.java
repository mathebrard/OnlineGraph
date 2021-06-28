package og;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphStorageService.VertexInfo;

public class VertexSet extends GraphElementSet {

	public VertexSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long u) {
		impl.add(u);
		set(u, "outEdges", new LongArrayList());
		set(u, "inEdges", new LongArrayList());
		set(u, "outVertices", new LongArrayList());
		var i = new VertexInfo();
		i.id = u;
		graph.addChange(new Change.AddVertex(i));
	}

	@Override
	public void remove(long u) {
		for (var e : new LongArrayList(outEdges(u))) {
			graph.edges.remove(e);
		}

		for (var e : new LongArrayList(inEdges(u))) {
			graph.edges.remove(e);
		}

		impl.remove(u);
		graph.addChange(new Change.RemoveVertex(u));
	}

	public LongList inEdges(long v) {
		return get(v, "inEdges", () -> emptyList);
	}

	public final LongList emptyList = new LongArrayList();

	public LongList outEdges(long v) {
		return get(v, "outEdges", () -> emptyList);
	}

	public boolean isIsolated(long u) {
		return outEdges(u).isEmpty() && inEdges(u).isEmpty();
	}

	public boolean isSink(long u) {
		return outEdges(u).isEmpty();
	}


	@Override
	public void clear() {
		impl.clear();
		graph.edges.impl.clear();
		graph.addChange(new Change.Clear());
	}
}
