package og;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public abstract class AbstractGraph implements GraphPrimitives {

	public LongList outNeighbors(long v) {
		var r = new LongArrayList();

		for (var e : outEdges(v)) {
			r.add(destination(e));
		}

		return r;
	}

}
