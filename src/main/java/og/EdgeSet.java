package og;

import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphService.EdgeInfo;

public class EdgeSet extends GraphElementSet {

	public EdgeSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long e) {
		throw new IllegalStateException("you must use add(from, to)");
	}

	public long add(long from, long to) {
		long e = ThreadLocalRandom.current().nextLong();
		impl.add(e);
		impl.set(e, "ends", new long[] { from, to });
		graph.vertices.alter(from, "outEdges", () -> new LongArrayList(), (LongList outs) -> outs.add(e));
		graph.vertices.alter(to, "inEdges", () -> new LongArrayList(), (LongList ins) -> ins.add(e));
//		vertices.alter(from, "outVertices", null, (LongList outs) -> outs.add(to));
		var i = new EdgeInfo();
		i.id = e;
		i.from = from;
		i.to = to;
		graph.addChange(new Change.AddEdge(i));
		return e;
	}

	@Override
	public void remove(long e) {
		var ends = ends(e);
		var from = ends[0];
		var to = ends[1];
		graph.vertices.alter(from, "outEdges", null, (LongList set) -> set.removeLong(set.indexOf(e)));
		graph.vertices.alter(to, "inEdges", null, (LongList set) -> set.removeLong(set.indexOf(e)));
//		vertices.alter(from, "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(to)));
		impl.remove(e);
		graph.addChange(new Change.RemoveEdge(e));
	}

	public long[] ends(long e) {
		return (long[]) get(e, "ends", null);
	}

	public long source(long e) {
		return ends(e)[0];
	}

	public long destination(long e) {
		return ends(e)[1];
	}

	@Override
	public void clear() {
		impl.clear();
		graph.vertices.impl.clear();
		graph.addChange(new Change.Clear());
	}

	@Override
	public void set(long id, String key, Object content) {
		super.set(id, key, content);
		graph.addChange(new Change.EdgeDataChange(id, key));
	}

}
