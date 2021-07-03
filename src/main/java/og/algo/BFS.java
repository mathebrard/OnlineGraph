package og.algo;

import java.io.Serializable;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import og.Graph;
import toools.io.Cout;

public class BFS {
	public static class BFSResult implements Serializable {
		LongList visitOrder;
		Long2LongMap distances;
		int nbVerticesVisited;

		@Override
		public String toString() {
			return "BFS [visitOrder=" + visitOrder + ", distances=" + distances + "]";
		}

	}

	public static BFSResult bfs(Graph g, long source, long maxDistance, long maxNbVerticesVisited) {
			if (source == -1) {
			source = g.vertices.random();
		}

		Cout.debugSuperVisible("bfs");
		long nbVertices = g.vertices.nbEntries();
		BFSResult r = new BFSResult();
		r.visitOrder = new LongArrayList();

//		AtomicBoolean completed = new AtomicBoolean(false);

		// Threads.newThread_loop_periodic(1000, () -> completed.get(), () -> {
		// reply(triggerMsg, new ProgressRatio(nbVertices, r.nbVerticesVisited));
		// });

//		var lp = new LongProcess("BFS (classic)", " vertex", nbVertices);
		r.distances = new Long2LongOpenHashMap();
		var q = new LongArrayList();
		q.add(source);
		r.distances.put(source, 0L);
		long nbVerticesVisited = 1;

		while (!q.isEmpty()) {
			Cout.debug(q.size() + " elements in queue");
//			++lp.sensor.progressStatus;

			var v = q.removeLong(0);

			if (r.visitOrder != null) {
				r.visitOrder.add(v);
			}

			var d = r.distances.get(v);

			if (d <= maxDistance) {
				for (var e : g.vertices.outArcs(v)) {
					var n = g.arcs.destination(e);

					if (!r.distances.containsKey(n)) {
						r.distances.put(n, d + 1);

						if (nbVerticesVisited++ >= maxNbVerticesVisited)
							break;

						q.add(n);

					}
				}
			}
		}

//		lp.end();
//		completed.set(true);
		// reply(triggerMsg, r);

		Cout.debugSuperVisible("bfs completed");

		return r;
	}
}
