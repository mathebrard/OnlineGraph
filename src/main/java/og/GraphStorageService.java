package og;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import toools.thread.Threads;

public class GraphStorageService extends Service {
	String[] colors = new String[] { "blue", "red", "green", "purple", "cyan", "yellow", "grey", "white" };
	String[] shapes = new String[] { "box", "polygon", "ellipse", "oval", "circle", "point", "egg", "triangle" };
	String[] arrowTypes = new String[] { "box", "crow", "diamond", "dot", "normal", "vee" };

	public static final Directory baseDirectory = new Directory("$HOME/.og");

	static {
		baseDirectory.ensureExists();
	}

	public GraphStorageService(Component component) {
		super(component);

		baseDirectory.listDirectories().forEach(d -> {
			m.put(d.getName(), new FlatOnDiskDiskGraph(d));
		});

		/*
		 * var g = new FlatOnDiskDiskGraph(new Directory(baseDirectory, "demo_graph"));
		 * if (g.exists()) { Cout.debug("clearing demo graph"); g.clear(); } else {
		 * Cout.debug("creating demo graph"); g.create(); }
		 */

		var g = new RAMGraph();
		m.put("demo_graph", g);

		{
			var p = new HashMap<String, String>();
			p.put("background color", "dark grey");
			p.put("default vertex size", "30");
			p.put("default vertex borderWidth", "5");
			p.put("default vertex image", "" + 30);
			p.put("default vertex color.border", "blue");
			p.put("default vertex background color", "white");
			p.put("default vertex hidden", "false");
			p.put("default vertex label", "vertex");
			p.put("default vertex mass", "4");
			p.put("default vertex shape", "circle");
			p.put("default vertex value", "" + 13);
			p.put("default vertex foo", "" + 1);
			p.put("default vertex bar", "hello");

			p.put("default edge directed", "yes");
			p.put("default edge arrow image", "http://img.com/arrow.png");
			p.put("default edge arrow scale", "1");
			p.put("default edge arrow type", "round");
			p.put("default edge color", "black");
			p.put("default edge dashes", "true");
			p.put("default edge label", "relation");
			p.put("default edge width", "5");
			g.setProperties(p);
		}

		Threads.newThread_loop(1000, () -> true, () -> {
			Cout.debug("checking graph");
			g.check();
			double r = Math.random();
			Cout.debug("#vertices: " + g.nbVertices());

			if (r < -0.1 * g.nbVertices() + 1) {
				Cout.debugSuperVisible("ADD VERTEX");
				g.addVertex();
			} else if (r < 0.1 * g.nbVertices()) {
				Cout.debugSuperVisible("REMOVE VERTEX");
				g.removeVertex(g.pickRandomVertex());
			} else if (r < 1 + -(g.nbEdges() / (double) g.nbVertices())) {
				Cout.debugSuperVisible("ADD EDGE");
				g.addEdge(g.pickRandomVertex(), g.pickRandomVertex());
			} else if (r < g.nbEdges() / (double) g.nbVertices()) {
				Cout.debugSuperVisible("REMOVE EDGE");
				g.removeEdge(g.pickRandomEdge());
			} else {
				Cout.debugSuperVisible("CHANGE PROPERTY");
				int n = 20;
				double rr = Math.random();
				double interval = 1d / n;

				if (g.nbVertices() > 0) {
					var u = g.pickRandomVertex();
					var p = new HashMap<String, Object>();

					if (rr < 0.05)
						p.put("size", 10 + 50 * Math.random());
					else if ((rr -= interval) < 0.05)
						p.put("borderWidth", Math.random() * 20);
					else if ((rr -= interval) < 0.05)
						p.put("image", Math.random() * 50);
					else if ((rr -= interval) < 0.05)
						p.put("color.border", colors[new Random().nextInt(colors.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("background color", colors[new Random().nextInt(colors.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("hidden", Math.random() < 0.5 ? "true" : "false");
					else if ((rr -= interval) < 0.05)
						p.put("label", colors[new Random().nextInt(colors.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("mass", Math.random() * 20);
					else if ((rr -= interval) < 0.05)
						p.put("shape", shapes[new Random().nextInt(shapes.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("value", Math.random() * 100);
					else if ((rr -= interval) < 0.05)
						p.put("foo", Math.random());
					else if ((rr -= interval) < 0.05)
						p.put("bar", "" + (Math.random() * 20));

					g.setVertexValue(u, "properties", p);
				}

				if (g.nbEdges() > 0) {
					var e = g.pickRandomEdge();
					var p = new HashMap<String, Object>();

					if ((rr -= interval) < 0.05)
						p.put("directed", Math.random() < 0.5 ? "true" : "false");
					else if ((rr -= interval) < 0.05)
						p.put("arrow image", "http://img.com/arrow.png");
					else if ((rr -= interval) < 0.05)
						p.put("arrow scale", Math.random() * 100);
					else if ((rr -= interval) < 0.05)
						p.put("arrow type", arrowTypes[new Random().nextInt(arrowTypes.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("color", colors[new Random().nextInt(colors.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("dashes", Math.random() < 0.5 ? "true" : "false");
					else if ((rr -= interval) < 0.05)
						p.put("label", colors[new Random().nextInt(colors.length)]);
					else if ((rr -= interval) < 0.05)
						p.put("width", "" + (Math.random() * 15));

					g.setEdgeValue(e, "properties", p);
				}
			}
		});
	}

	private void gnm(Graph g, int n, int m) {
		for (int i = 0; i < n; ++i) {
			g.addVertex();
		}

		for (int i = 0; i < m; ++i) {
			var u = g.pickRandomVertex();
			var v = g.pickRandomVertex();
			g.addEdge(u, v);
		}
	}

	Map<String, Graph> m = new HashMap<>();

	public Graph getGraph(String graphID) {
		return m.get(graphID);
	}

	@IdawiOperation
	public Set<String> listGraphs() {
		var directories = baseDirectory.listDirectories();
		var graphIDs = directories.stream().map(d -> d.getName()).collect(Collectors.toSet());
		return new TreeSet<>(graphIDs);
	}

	@IdawiOperation
	public long addRandomVertex(String graphID) {
		return getGraph(graphID).addVertex();
	}

	@IdawiOperation
	public void addVertex(String graphID, long u) {
		getGraph(graphID).addVertex(u);
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
			i.props = g.getEdgeValue(e, "properties", () -> new HashMap<String, String>());
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
		Map<String, String> props;
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
			e.props = g.getVertexValue(v, "properties", () -> new HashMap<String, String>());
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
		gi.props = g.getProperties();
		return gi;
	}

	@IdawiOperation
	public String toDOT(String gid) {
		var g = getGraph(gid);
		var bos = new ByteArrayOutputStream();
		var out = new PrintStream(bos);
		out.println("# graph \"" + gid + "\" has " + g.nbVertices() + " vertices and " + g.nbEdges() + " edges");
		out.println("digraph {");

		g.traverseVertices(u -> {
			out.println("\t" + u);
		});

		g.traverseEdges(e -> {
			var ends = g.ends(e);
			out.println("\t" + ends[0] + " -> " + ends[1]);
		});

		out.println("}");
		out.flush();
		return new String(bos.toByteArray());
	}

	@IdawiOperation
	public Map getGraphInfo(String gid) {
		var g = getGraph(gid);
		var m = new HashMap<>();
		m.put("id", gid);
		m.put("nbVertices", g.nbVertices());
		m.put("nbEdges", g.nbEdges());
		return m;
	}

	@IdawiOperation
	public void create(String gid) {
		var g = new FlatOnDiskDiskGraph(new Directory(baseDirectory, gid));
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

	@IdawiOperation
	public List<Change> changes(String gid, double since) {
		return getGraph(gid).getHistory().stream().filter(c -> c.date >= since).collect(Collectors.toList());
	}

	@IdawiOperation
	public List<Change> history(String gid) {
		var g = getGraph(gid);
		return g.getHistory();
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
//		g.create();
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

	private void importLines(String gid, byte[] edges, BiConsumer<Graph, String> c) throws IOException {
		// String gid = "graph-" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		var g = getGraph(gid);
		Cout.debugSuperVisible(g);

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
