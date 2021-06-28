package og.algo;

import java.util.Random;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.Graph;
import toools.io.Cout;

public class RandomWalk {
	public static LongList randomWalk(Graph g, long source, long maxDistance) {
		if (source == -1) {
			source = g.vertices.random();
		}

		Cout.debugSuperVisible("random search");

		var l = new LongArrayList();

		while (l.size() < maxDistance) {
			l.add(source);
			var succ = new LongArrayList();

			for (var e : g.vertices.outEdges(source)) {
				var s = g.edges.destination(e);

				if (!l.contains(s)) {
					succ.add(s);
				}
			}

			if (succ.isEmpty())
				break;

			source = succ.getLong(new Random().nextInt(succ.size()));
		}

		return l;
	}
}
