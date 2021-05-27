package og;

import java.util.stream.Collectors;

import idawi.IdawiExposed;
import idawi.MessageQueue;
import idawi.Service;

public class GraphService extends Service {

	@IdawiExposed
	public void listGraphs(MessageQueue q) {
		var triggerMsg = q.get_blocking();
		var graphList = Graph.baseDirectory.listDirectories().stream().map(d -> d.getName())
				.collect(Collectors.toSet());
		reply(triggerMsg, graphList);
	}

	@IdawiExposed
	public void addVertex(String graph, String u) {
		new Graph(graph).addVertex(u);
	}

	@IdawiExposed
	public void removeVertex(String graph, String u) {
		new Graph(graph).removeVertex(u);
	}

	@IdawiExposed
	public void addEdge(String graph, String from, String to) {
		new Graph(graph).addEdge(from, to);
	}

	@IdawiExposed
	public void removeEdge(String graph, String from, String to) {
		new Graph(graph).removeEdge(from, to);
	}
}
