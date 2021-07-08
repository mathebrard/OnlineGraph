package og;

import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import og.GraphService.EdgeInfo;

public class EdgeSet extends GraphElementSet {

	public EdgeSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long e) {
		add(graph.vertices.random(), graph.vertices.random());
	}

	public void add(long... vertices) {
		var ends = new LongOpenHashSet(vertices.length);

		for (var u : vertices) {
			ends.add(u);
		}

		add(ends);
	}

	public long add(LongSet ends) {
		var e = ThreadLocalRandom.current().nextLong();
		add(e, ends);
		return e;
	}

	public void add(long e, LongSet ends) {
		synchronized (graph) {
			for (var u : ends)
				if (!graph.vertices.contains(u))
					throw new IllegalArgumentException("incident vertex does not exist : " + u);

			impl.add(e);
			impl.set(e, "ends", ends);

			for (var u : ends) {
				graph.vertices.alter(u, "edges", () -> new LongOpenHashSet(), (LongSet edges) -> edges.add(e));
			}

//		vertices.alter(from, "outVertices", null, (LongList outs) -> outs.add(to));
			var i = new EdgeInfo();
			i.id = e;
			i.ends = ends;
			graph.commitNewChange(new Change.AddEdge(i));
		}
	}

	@Override
	public void remove(long e) {
		synchronized (graph) {
			for (var u : ends(e)) {
				graph.vertices.alter(u, "edges", null, (LongSet edges) -> edges.remove(e));
			}

//		vertices.alter(from, "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(to)));
			impl.remove(e);
			graph.commitNewChange(new Change.RemoveEdge(e));
		}
	}

	public LongSet ends(long e) {
		synchronized (graph) {
			return (LongSet) get(e, "ends", null);
		}
	}

	public boolean isLoop(long e) {
		return ends(e).size() == 1;
	}

	public boolean isHyperEdge(long e) {
		return ends(e).size() > 2;
	}

	@Override
	public void clear() {
		synchronized (graph) {
			impl.clear();
			graph.vertices.impl.clear();
			graph.commitNewChange(new Change.Clear());
		}
	}

	@Override
	public void set(long id, String key, Object content) {
		synchronized (graph) {
			super.set(id, key, content);
			graph.commitNewChange(new Change.EdgeDataChange(id, key));
		}
	}

}
