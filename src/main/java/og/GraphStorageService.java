package og;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import idawi.Component;
import idawi.IdawiOperation;
import idawi.Service;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import toools.io.Cout;
import toools.io.file.Directory;

public class GraphStorageService extends Service {
	public static final Directory baseDirectory = new Directory("$HOME/.og");

	static {
		baseDirectory.ensureExists();
	}

	public GraphStorageService(Component component) {
		super(component);

		var g = new BasicGraph(new Directory(baseDirectory, "demo_graph"));

		if (!g.d.exists()) {
			System.out.println("creating demo graph");
			gnm(g, 10, 20);
		}
	}

	private void gnm(BasicGraph g, int n, int m) {
		g.create();

		for (int i = 0; i < n; ++i) {
			g.addVertex();
		}

		for (int i = 0; i < m; ++i) {
			var u = g.pickRandomVertex();
			var v = g.pickRandomVertex();
			g.addEdge(u, v);
		}
	}

	public static AbstractGraph getGraph(String graphID) {
		var graphDir = new Directory(baseDirectory, graphID);
		return new BasicGraph(graphDir);
	}

	@IdawiOperation
	public Set<String> listGraphs() {
		var directories = baseDirectory.listDirectories();
		var graphIDs = directories.stream().map(d -> d.getName()).collect(Collectors.toSet());
		return new TreeSet<>(graphIDs);
	}

	@IdawiOperation
	public long addVertex(String graphID) {
		return getGraph(graphID).addVertex();
	}

	@IdawiOperation
	public void removeVertex(String graphID, long v) {
		getGraph(graphID).removeVertex(v);
	}

	@IdawiOperation
	public void clear(String graphID) {
		getGraph(graphID).clear();
	}

	@IdawiOperation
	public long addEdge(String graphID, long from, long to) {
		return getGraph(graphID).addEdge(from, to);
	}

	@IdawiOperation
	public void removeEdge(String graphID, long e) {
		getGraph(graphID).removeEdge(e);

	}

	@IdawiOperation
	public List<EdgeInfo> edges(String gid) {
		var g = getGraph(gid);
		List<EdgeInfo> edges = new ArrayList<>();

		g.traverseEdges((id, from, to, props) -> {
			var e = new EdgeInfo();
			e.id = id;
			e.from = from;
			e.to = to;
			e.props = props;
			edges.add(e);
		});

		return edges;
	}

	public static class Info implements Serializable {
		public long id;
		Properties props;
	}

	public static class EdgeInfo extends Info {
		public long from, to;
	}

	public static class VertexInfo extends Info {
	}

	public static class GraphInfo implements Serializable {
		public String name;
		List<VertexInfo> vertices;
		List<EdgeInfo> edges;
	}

	@IdawiOperation
	public LongList verticesIDs(String gid) {
		var g = getGraph(gid);
		LongList l = new LongArrayList();

		g.traverseVertices(u -> {
			l.add(u);
		});

		return l;
	}

	@IdawiOperation
	public List<VertexInfo> vertices(String gid) {
		var g = getGraph(gid);
		List<VertexInfo> vertices = new ArrayList<>();

		g.traverseVertices((id, props) -> {
			var e = new VertexInfo();
			e.id = id;
			e.props = props;
			vertices.add(e);
		});

		return vertices;
	}

	@IdawiOperation
	public GraphInfo get(String gid) {
		var g = getGraph(gid);
		var gi = new GraphInfo();
		gi.edges = edges(gid);
		gi.vertices = vertices(gid);
		return gi;
	}

	@IdawiOperation
	public void create(String gid) {
		var g = getGraph(gid);
		g.create();
	}

	@IdawiOperation
	public long pickRandomVertex(String gid) {
		var g = getGraph(gid);
		return g.pickRandomVertex();
	}

	@IdawiOperation
	public long pickRandomEdge(String gid) {
		var g = getGraph(gid);
		return g.pickRandomEdge();
	}

	/*
	 * Uses https://github.com/paypal/digraph-parser
	 */
	@IdawiOperation
	public String importDot(String gid, String dot) {
		int n = 0;
		Cout.debugSuperVisible(n++);
		
		var g = getGraph(gid);
		Cout.debugSuperVisible(n++);
		g.create();
		Cout.debugSuperVisible(n++);
		GraphParser parser = new GraphParser(new ByteArrayInputStream(dot.getBytes()));
		Cout.debugSuperVisible(n++);
		Map<String, GraphNode> nodes = parser.getNodes();
		Cout.debugSuperVisible(n++);
		Map<String, GraphEdge> edges = parser.getEdges();
		Cout.debugSuperVisible(n++);
		return "dot parsed: " + nodes + edges;
	}
	
	/*
	 * Uses https://github.com/paypal/digraph-parser
	 */
	@IdawiOperation
	public void importEdgeList(String gid, String edges) {
		//String gid = "graph-" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		create(gid);
		var g = getGraph(gid);
		
		var r = new StringReader(edges);
		var br = new BufferedReader(r);
		
		while (true) {
			String l = br.readLine();
			
			if (l)
		}
	}


}
