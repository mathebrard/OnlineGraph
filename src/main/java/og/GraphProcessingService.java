package og;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import idawi.Component;
import idawi.IdawiOperation;
import idawi.Service;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import toools.progression.LongProcess;
import toools.thread.Threads;

public class GraphProcessingService extends Service {
	public GraphProcessingService(Component component) {
		super(component);
	}

	private Graph getGraph(String graphID) {
		return component.lookupService(GraphStorageService.class).getGraph(graphID);
	}

	public static class BFSResult implements Serializable {
		LongList visitOrder;
		Long2LongMap distances;
		int nbVerticesVisited;

		@Override
		public String toString() {
			return "BFS [visitOrder=" + visitOrder + ", distances=" + distances + "]";
		}

	}

	@IdawiOperation
	public BFSResult bfs(String graphID, long source, long maxDistance, long maxNbVerticesVisited) {
		var g = getGraph(graphID);
		long nbVertices = g.nbVertices();
		BFSResult r = new BFSResult();

		AtomicBoolean completed = new AtomicBoolean(false);

		Threads.newThread_loop_periodic(1000, () -> completed.get(), () -> {
			// reply(triggerMsg, new ProgressRatio(nbVertices, r.nbVerticesVisited));
		});

		var lp = new LongProcess("BFS (classic)", " vertex", nbVertices);
		r.distances = new Long2LongOpenHashMap();
		var q = new LongArrayList();
		q.add(source);
		r.distances.put(source, 0L);
		long nbVerticesVisited = 1;

		while (!q.isEmpty()) {
			++lp.sensor.progressStatus;

			var v = q.removeLong(0);

			if (r.visitOrder != null) {
				r.visitOrder.add(v);
			}

			var d = r.distances.get(v);

			if (d <= maxDistance) {
				for (var e : g.outEdges(v)) {
					var n = g.destination(e);

					if (!r.distances.containsKey(n)) {
						r.distances.put(n, d + 1);

						if (nbVerticesVisited++ >= maxNbVerticesVisited)
							break;

						q.add(n);

					}
				}
			}
		}

		lp.end();
		completed.set(true);
		// reply(triggerMsg, r);
		return r;
	}

	@IdawiOperation
	public double clusteringCoefficient(String graphID, long v) {
		var g = getGraph(graphID);
		var neighbors = (LongList) g.getVertexValue(v, "outNeighbors", null);

		int count = 0;

		for (var n : neighbors) {
			for (var nn : (LongList) g.getVertexValue(n, "outNeighbors", null)) {
				if (neighbors.contains(nn)) {
					++count;
				}
			}
		}

		double degree = neighbors.size();
		return count / (degree * (degree - 1));
	}

}
