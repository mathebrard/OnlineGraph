package og;

import java.util.stream.Collectors;

import idawi.Component;
import idawi.IdawiOperation;
import idawi.MessageQueue;
import idawi.Service;
import toools.io.file.Directory;

public class GraphStorageService extends Service {
	public static final Directory baseDirectory = new Directory("$HOME/graph-server");

	static {
		baseDirectory.ensureExists();
	}

	public GraphStorageService(Component component) {
		super(component);

		AbstractGraph g = new BasicGraph(new Directory(baseDirectory, "demo_graph"));

		for (int i = 0; i < 10; ++i) {
			g.addVertex("v" + i);
		}

		for (int i = 0; i < 10; ++i) {
			g.addEdge("v" + i, "v" + (10 - i - 1));
		}

	}

	public static AbstractGraph getGraph(String graphID) {
		var graphDir = new Directory(baseDirectory, graphID);
		return new BasicGraph(graphDir);
	}

	@IdawiOperation
	public void listGraphs(MessageQueue q) {
		var triggerMsg = q.get_blocking();
		var graphList = baseDirectory.listDirectories().stream().map(d -> d.getName()).collect(Collectors.toSet());
		reply(triggerMsg, graphList);
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
