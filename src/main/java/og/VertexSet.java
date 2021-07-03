package og;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphService.VertexInfo;

public class VertexSet extends GraphElementSet {

	public VertexSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long u) {
		impl.add(u);
		set(u, "outArcs", new LongArrayList());
		set(u, "inArcs", new LongArrayList());
		set(u, "outVertices", new LongArrayList());
		var i = new VertexInfo();
		i.id = u;
		graph.commitNewChange(new Change.AddVertex(i));
	}

	@Override
	public void remove(long u) {
		for (var e : new LongArrayList(outArcs(u))) {
			graph.arcs.remove(e);
		}

		for (var e : new LongArrayList(inArcs(u))) {
			graph.arcs.remove(e);
		}

		impl.remove(u);
		graph.commitNewChange(new Change.RemoveVertex(u));
	}

	public LongList inArcs(long v) {
		return get(v, "inArcs", () -> emptyList);
	}

	public final LongList emptyList = new LongArrayList();

	public LongList outArcs(long v) {
		return get(v, "outArcs", () -> emptyList);
	}
	
	public LongList edges(long v) {
		return get(v, "edges", () -> emptyList);
	}

	public boolean isIsolated(long u) {
		return outArcs(u).isEmpty() && inArcs(u).isEmpty();
	}

	public boolean isSink(long u) {
		return outArcs(u).isEmpty();
	}


	@Override
	public void clear() {
		impl.clear();
		graph.arcs.impl.clear();
		graph.commitNewChange(new Change.Clear());
	}
	
	@Override
	public void set(long id, String key, Object content) {
		super.set(id, key, content);
		graph.commitNewChange(new Change.VertexDataChange(id, key));
	}

}
