package og;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import toools.gui.GraphViz;
import toools.gui.GraphViz.COMMAND;
import toools.gui.GraphViz.OUTPUT_FORMAT;
import toools.io.Cout;
import toools.io.file.Directory;
import toools.thread.Threads;

public class GraphStorageService extends Service {

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

		var g = new HashGraph();
		m.put("demo_graph", g);

		{
			var p = new HashMap<String, String>();
			p.put("background color", "dark grey");
			p.put("default vertex size", "20");
			p.put("default vertex borderWidth", "1");
			p.put("default vertex image", "" + 30);
			p.put("default vertex color.border", "blue");
			p.put("default vertex background color", "white");
			p.put("default vertex hidden", "false");
			p.put("default vertex label", "vertex");
			p.put("default vertex mass", "1");
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
			p.put("default edge width", "1");
			g.setProperties(p);
		}

		Threads.newThread_loop(1000, () -> true, () -> {
			Cout.debug("checking graph");
			g.check();
			Cout.debug("#vertices: " + g.nbVertices());
			double targetN = 10;
			double targetD = 20;
			double nbVertices = g.nbVertices();
			double nbEdges = g.nbEdges();
			double degree = nbEdges / nbVertices;

			if (Math.random() < -0.5 * nbVertices / targetN + 1) {
				g.addVertex();
			}

			if (Math.random() < 0.5 * nbVertices / targetN) {
				g.removeVertex(g.pickRandomVertex());
			}

			if (Math.random() < -degree / targetD + 1) {
				g.addEdge(g.pickRandomVertex(), g.pickRandomVertex());
			}

			if (Math.random() < degree / targetD) {
				g.removeEdge(g.pickRandomEdge());
			}

			if (Math.random() < 0.4 && g.nbVertices() > 0) {
				var u = g.pickRandomVertex();
				var p = g.getVertexValue(u, "properties", () -> new HashMap<String, String>());
				double pp = 0.5;

				if (Math.random() < pp)
					p.put(VertexProperties.scale.getName(), VertexProperties.scale.random());
				if (Math.random() < pp)
					p.put(VertexProperties.borderWidth.getName(), VertexProperties.borderWidth.random());
				if (Math.random() < pp)
					p.put(VertexProperties.borderColor.getName(), VertexProperties.borderColor.random());
				if (Math.random() < pp)
					p.put(VertexProperties.fillColor.getName(), VertexProperties.fillColor.random());
				if (Math.random() < pp)
					p.put(VertexProperties.hidden.getName(), VertexProperties.hidden.random());
				if (Math.random() < pp)
					p.put(VertexProperties.labelColor.getName(), VertexProperties.labelColor.random());
				if (Math.random() < pp)
					p.put(VertexProperties.label.getName(), VertexProperties.label.random());
				if (Math.random() < pp)
					p.put(VertexProperties.shape.getName(), VertexProperties.shape.random());
				if (Math.random() < pp)
					if (Math.random() < pp)
						p.put("foo", "" + Math.random());
				if (Math.random() < pp)
					p.put("bar", "" + (Math.random() * 20));

				g.setVertexValue(u, "properties", p);
			}

			if (Math.random() < 0.4 && g.nbEdges() > 0) {
				var e = g.pickRandomEdge();
				var p = g.getEdgeValue(e, "properties", () -> new HashMap<String, String>());
				double pp = 0.5;

				if (Math.random() < pp)
					p.put(EdgeProperties.directed.getName(), EdgeProperties.directed.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.arrowSize.getName(), EdgeProperties.arrowShape.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.arrowShape.getName(), EdgeProperties.arrowShape.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.color.getName(), EdgeProperties.color.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.style.getName(), EdgeProperties.style.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.label.getName(), EdgeProperties.label.random());
				if (Math.random() < pp)
					p.put(EdgeProperties.width.getName(), EdgeProperties.width.random());

				g.setEdgeValue(e, "properties", p);
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

	private static final List<String> dotNodeProperties = Arrays.asList(new String[] { "" });

	@IdawiOperation
	public String toDOT(String gid) {
		var g = getGraph(gid);
		var bos = new ByteArrayOutputStream();
		var out = new PrintStream(bos);
		out.println("# graph \"" + gid + "\" has " + g.nbVertices() + " vertices and " + g.nbEdges() + " edges");
		out.println("digraph {");

		g.traverseVertices(u -> {
			out.print("\t" + u);
			var p = g.getVertexValue(u, "properties", () -> new HashMap<String, String>());
			var gvp = new HashMap<String, String>();

			var shape = p.get(VertexProperties.shape.getName());

			if (shape != null) {
				gvp.put("shape", VertexProperties.shape.toGraphviz(shape));
			}

			var borderColor = p.get(VertexProperties.borderColor.getName());

			if (borderColor != null) {
				gvp.put("color", VertexProperties.borderColor.toGraphviz(borderColor));
			}

			var fillColor = p.get(VertexProperties.fillColor.getName());

			if (fillColor != null) {
				gvp.put("style", "filled");
				gvp.put("fillcolor", VertexProperties.fillColor.toGraphviz(fillColor));
			}

			var borderWidth = p.get(VertexProperties.borderWidth.getName());
			gvp.put("penwidth", borderWidth == null ? "1" : "" + borderWidth);

			var label = p.get(VertexProperties.label.getName());

			if (label != null) {
				gvp.put("label", VertexProperties.label.toGraphviz(label));
			}
			var hidden = p.get(VertexProperties.hidden.getName());
			var scale = p.get(VertexProperties.scale.getName());

			if (scale != null) {
				gvp.put("width", VertexProperties.scale.toGraphviz(scale));
				gvp.put("height", VertexProperties.scale.toGraphviz(scale));
			}
			
//var labelColor = p.get(VertexProperties.labelColor.getName());

			f(gvp, out);
			out.println();
		});

		g.traverseEdges(e -> {
			var ends = g.ends(e);
			out.print("\t" + ends[0] + " -> " + ends[1]);
			var p = g.getEdgeValue(e, "properties", () -> new HashMap<String, String>());
			var gvp = new HashMap<String, String>();

			var arrowShape = p.get(EdgeProperties.arrowShape.getName());

			if (arrowShape != null) {
				gvp.put("arrowhead", EdgeProperties.arrowShape.toGraphviz(arrowShape));
			}

			var style = p.get(EdgeProperties.style.getName());

			if (style != null) {
				gvp.put("style", EdgeProperties.style.toGraphviz(style));
			}

			f(gvp, out);
			out.println();
		});

		out.println("}");
		out.flush();
		return new String(bos.toByteArray());
	}

	@IdawiOperation
	public byte[] graphviz(String gid, String command, String outputFormat) {
		return GraphViz.toBytes(COMMAND.valueOf(command), toDOT(gid), OUTPUT_FORMAT.valueOf(outputFormat));
	}

	private void f(Map<String, String> p, PrintStream out) {
		// p.keySet().removeIf(k -> p.get(k) == null);

		out.print("\t [");
		var i = p.entrySet().iterator();

		while (i.hasNext()) {
			var e = i.next();
			out.print(e.getKey() + "=\"" + e.getValue() + "\"");

			if (i.hasNext()) {
				out.print(", ");
			}
		}

		out.print("]");
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
