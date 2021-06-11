package og;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
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

		var g = new Graph(new Directory(baseDirectory, "demo_graph"));

		if (g.d.exists()) {
			g.clear();
		}

		System.out.println("creating demo graph");
		gnm(g, 3, 5);

		{
			var u = g.pickRandomVertex();
			var p = new HashMap<String, Object>();
			p.put("size", "30");
			p.put("borderWidth", "5");
			p.put("image", "" + 30);
			p.put("color.border", "blue");
			p.put("background coolor", "white");
			p.put("hidden", "false");
			p.put("label", "vertex");
			p.put("mass", "4");
			p.put("shape", "circle");
			p.put("value", 13);
			p.put("foo", 1);
			p.put("bar", "hello");
			g.writeVertex(u, "properties", p);
		}

		{
			var e = g.pickRandomEdge();
			var p = new HashMap<String, Object>();
			p.put("directed", "yes");
			p.put("arrow image", "http://img.com/arrow.png");
			p.put("arrow scale", "1");
			p.put("arrow type", "round");
			p.put("color", "black");
			p.put("dashes", "1010");
			p.put("label", "relation");
			p.put("mass", "1");
			p.put("width", "5");
			g.writeEdge(e, "properties", p);
		}

	}

	private void gnm(Graph g, int n, int m) {
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

	public static Graph getGraph(String graphID) {
		var graphDir = new Directory(baseDirectory, graphID);
		return new Graph(graphDir);
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

		g.traverseEdges(e -> {
			var i = new EdgeInfo();
			i.id = e;
			var ends = g.ends(e);
			i.from = ends[0];
			i.to = ends[1];
			i.props = (Map<String, String>) g.readEdge(e, "properties", () -> new HashMap<>());
			edges.add(i);
		});

		return edges;
	}

	public static class Info implements Serializable {
		public long id;
		Map<String, String> props;
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

		g.traverseVertices(v -> {
			var e = new VertexInfo();
			e.id = v;
			e.props = (Map<String, String>) g.readVertex(v, "properties", () -> new HashMap<>());
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
	public String importDot(String gid, byte[] dot) {
		int n = 0;
		Cout.debugSuperVisible(n++);

		var g = getGraph(gid);
		Cout.debugSuperVisible(n++);
		g.create();
		Cout.debugSuperVisible(n++);
		GraphParser parser = new GraphParser(new ByteArrayInputStream(dot));
		Cout.debugSuperVisible(n++);
		Map<String, GraphNode> nodes = parser.getNodes();
		Cout.debugSuperVisible(n++);
		Map<String, GraphEdge> edges = parser.getEdges();
		Cout.debugSuperVisible(n++);
		return "dot parsed: " + nodes + edges;
	}

	@IdawiOperation
	public String importEdges(String gid, byte[] edges) throws IOException {
		importLines(gid, edges, (g, newLine) -> {
			var t = newLine.trim().split("[^0-9]+");
			long src = Long.valueOf(t[0]);
			long dest = Long.valueOf(t[1]);
			g.addEdge(src, dest);
		});

		return "ok";
	}

	@IdawiOperation
	public long imporADJ(String gid, byte[] edges) throws IOException {
		AtomicLong nbEdges = new AtomicLong();

		importLines(gid, edges, (g, line) -> {
			var t = line.trim().split("[^0-9]*");
			long src = Long.valueOf(t[0]);

			for (int i = 1; i < t.length; ++i) {
				long dest = Long.valueOf(t[i]);
				g.addEdge(src, dest);
				nbEdges.incrementAndGet();
			}
		});

		return nbEdges.get();
	}

	private void importLines(String gid, byte[] edges, BiConsumer<AbstractGraph, String> c) throws IOException {
		// String gid = "graph-" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		var g = getGraph(gid);
		Cout.debugSuperVisible(g);

		if (!g.exists()) {
			g.create();
		}

		var r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(edges)));

		while (true) {
			String line = r.readLine();

			if (line == null) {
				break;
			}

			c.accept(g, line);
		}
	}

}
