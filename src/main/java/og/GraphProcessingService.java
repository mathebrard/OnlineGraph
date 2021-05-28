package og;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import idawi.Component;
import idawi.IdawiOperation;
import idawi.MessageQueue;
import idawi.ProgressRatio;
import idawi.Service;
import toools.progression.LongProcess;
import toools.thread.Threads;

public class GraphProcessingService extends Service {
	public GraphProcessingService(Component component) {
		super(component);
	}

	private AbstractGraph getGraph(String graphID) {
		return component.lookupService(GraphStorageService.class).getGraph(graphID);
	}

	public static class BFSParms implements Serializable {
		String graphID;
		String source;
		long maxDistance;
		long maxNbVerticesVisited;
	}

	public static class BFSResult {
		List<String> visitOrder;
		Map<String, Long> distances;
		int nbVerticesVisited;

		@Override
		public String toString() {
			return "BFS [visitOrder=" + visitOrder + ", distances=" + distances + "]";
		}

	}

	@IdawiOperation
	public void bfs(MessageQueue in) {
		var triggerMsg = in.get_blocking();
		var parms = (BFSParms) triggerMsg.content;
		var g = getGraph(parms.graphID);
		long nbVertices = g.nbVertices();
		BFSResult r = new BFSResult();

		AtomicBoolean completed = new AtomicBoolean(false);
		
		Threads.newThread_loop_periodic(1000, () -> completed.get(), () -> {
			reply(triggerMsg, new ProgressRatio(nbVertices, r.nbVerticesVisited));
		});

		LongProcess lp = new LongProcess("BFS (classic)", " vertex", nbVertices);
		r.distances = new HashMap<>();
		var q = new ArrayList<String>();
		q.add(parms.source);
		r.distances.put(parms.source, 0L);
		long nbVerticesVisited = 1;

		while (!q.isEmpty()) {
			++lp.sensor.progressStatus;

			String v = q.remove(0);
			r.visitOrder.add(v);
			long d = r.distances.get(v);

			if (d <= parms.maxDistance) {
				for (String n : g.out(v)) {
					if (!r.distances.containsKey(n)) {
						r.distances.put(n, d + 1);

						if (nbVerticesVisited++ >= parms.maxNbVerticesVisited)
							break;

						q.add(n);
					}
				}
			}
		}

		lp.end();
		completed.set(true);
		reply(triggerMsg, r);
	}

	@IdawiOperation
	public void addVertex(String graphID, String u) {
		getGraph(graphID).addVertex(u);
	}

	@IdawiOperation
	public void removeVertex(String graphID, String u) {
		getGraph(graphID).removeVertex(u);
	}

	@IdawiOperation
	public void addEdge(String graphID, String from, String to) {
		getGraph(graphID).addEdge(from, to);
	}

	@IdawiOperation
	public void removeEdge(String graphID, String from, String to) {
		getGraph(graphID).removeEdge(from, to);
	}

}
