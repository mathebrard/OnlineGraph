package og;

import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphService.ArcInfo;

public class ArcSet extends GraphElementSet {

	public ArcSet(ElementSet set, Graph g) {
		super(set, g);
	}

	@Override
	public void add(long e) {
		throw new IllegalStateException("you must use add(from, to)");
	}

	public long add(long from, long to) {
		if (!graph.vertices.contains(from))
			throw new IllegalArgumentException("source vertex does not exist : " + from);

		if (!graph.vertices.contains(to))
			throw new IllegalArgumentException("destination vertex does not exist : " + to);

		long e = ThreadLocalRandom.current().nextLong();
		impl.add(e);
		impl.set(e, "ends", new long[] { from, to });
		graph.vertices.alter(from, "outArcs", () -> new LongArrayList(), (LongList outs) -> outs.add(e));
		graph.vertices.alter(to, "inArcs", () -> new LongArrayList(), (LongList ins) -> ins.add(e));
//		vertices.alter(from, "outVertices", null, (LongList outs) -> outs.add(to));
		var i = new ArcInfo();
		i.id = e;
		i.from = from;
		i.to = to;
		graph.commitNewChange(new Change.AddArc(i));
		return e;
	}

	@Override
	public void remove(long e) {
		var ends = ends(e);
		var from = ends[0];
		var to = ends[1];
		graph.vertices.alter(from, "outArcs", null, (LongList set) -> set.removeLong(set.indexOf(e)));
		graph.vertices.alter(to, "inArcs", null, (LongList set) -> set.removeLong(set.indexOf(e)));
//		vertices.alter(from, "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(to)));
		impl.remove(e);
		graph.commitNewChange(new Change.RemoveArc(e));
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
		graph.commitNewChange(new Change.Clear());
	}

	@Override
	public void set(long id, String key, Object content) {
		super.set(id, key, content);
		graph.commitNewChange(new Change.ArcDataChange(id, key));
	}

}
