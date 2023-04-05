package og;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import og.GraphService.VertexInfo;

public class VertexSet extends GraphElementSet {

	public VertexSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long u) {
		synchronized (graph) {
			impl.add(u);
			set(u, "outArcs", new LongOpenHashSet());
			set(u, "inArcs", new LongOpenHashSet());
			set(u, "edges", new LongOpenHashSet());
			set(u, "outVertices", new LongOpenHashSet());
			var i = new VertexInfo();
			i.id = u;
			graph.commitNewChange(new Change.AddVertex(i));
		}
	}

	@Override
	public void remove(long u) {
		synchronized (graph) {
			for (var e : new LongArrayList(outArcs(u))) {
				graph.arcs.remove(e);
			}

			for (var e : new LongArrayList(inArcs(u))) {
				graph.arcs.remove(e);
			}

			for (var e : new LongArrayList(edges(u))) {
				graph.edges.remove(e);
			}

			impl.remove(u);
			graph.commitNewChange(new Change.RemoveVertex(u));
		}
	}

	public LongSet inArcs(long v) {
		return get(v, "inArcs", () -> emptySet);
	}

	public final LongList emptyList = new LongArrayList();
	public final LongSet emptySet = new LongOpenHashSet();

	public LongSet outArcs(long v) {
		return get(v, "outArcs", () -> emptySet);
	}

	public LongSet edges(long v) {
		return get(v, "edges", () -> emptySet);
	}

	public boolean isIsolated(long u) {
		return outArcs(u).isEmpty() && inArcs(u).isEmpty();
	}

	public boolean isSink(long u) {
		return outArcs(u).isEmpty();
	}

	@Override
	public void clear() {
		synchronized (graph) {
			impl.clear();
			graph.arcs.impl.clear();
			graph.commitNewChange(new Change.Clear());
		}
	}

	@Override
	public void set(long id, String key, Object content) {
		super.set(id, key, content);
		graph.commitNewChange(new Change.VertexDataChange(id, key));
	}

}
