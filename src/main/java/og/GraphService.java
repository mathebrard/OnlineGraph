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

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import idawi.Component;
import idawi.IdawiOperation;
import idawi.Service;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import og.algo.BFS;
import og.algo.CC;
import og.algo.GNM;
import og.algo.RandomWalk;
import og.dynamics.GridEvolver;
import og.dynamics.Grow;
import og.dynamics.RandomEvolver;
import og.dynamics.TreeEvolver;
import toools.gui.GraphViz;
import toools.gui.GraphViz.COMMAND;
import toools.gui.GraphViz.OUTPUT_FORMAT;
import toools.io.Cout;
import toools.io.JavaResource;
import toools.io.file.Directory;
import toools.reflect.Clazz;

public class GraphService extends Service {

	public static final Directory baseDirectory = new Directory("$HOME/.og");

	static {
		baseDirectory.ensureExists();
	}

	Map<String, Graph> m = new HashMap<>();

	public GraphService(Component component) {
		super(component);

		baseDirectory.listDirectories().forEach(d -> {
			m.put(d.getName(), new FlatOnDiskDiskGraph(d));
		});

		var randomGraph = new HashGraph();
		new RandomEvolver(randomGraph);
		m.put("randomGraph", randomGraph);

		if (false) {
			var d = new Directory(baseDirectory, "randomPersistentGraph");
			var randomPersistentGraph = new MapDBGraph(d);
			new RandomEvolver(randomPersistentGraph);
			m.put("randomPersistentGraph", randomPersistentGraph);
		}

		if (false) {
			var grid = new HashGraph();
			new GridEvolver(grid, 10, 10, 1);
			m.put("grid", grid);
		}

		if (true) {
			var growingGraph = new HashGraph();
			new Grow(growingGraph, 1.5, 3);
			m.put("growingGraph", growingGraph);
		}

		if (false) {
			var tree = new HashGraph();
			new TreeEvolver(tree);
			m.put("tree", tree);
		}

		for (var g : m.values()) {
			g.setProperties(g.defaultProperties());
		}
	}

	public Graph getGraph(String gid) {
		var g = m.get(gid);

		if (g == null)
			throw new IllegalArgumentException("no such graph " + gid);

		return g;
	}

	@IdawiOperation
	public void gnm(String graphID, int n, int m) {
		GNM.gnm(getGraph(graphID), n, m);
	}

	@IdawiOperation
	public Set<String> listGraphs() {
		return new TreeSet<>(m.keySet());
	}

	@IdawiOperation
	public long addRandomVertex(String graphID) {
		return getGraph(graphID).vertices.add();
	}

	@IdawiOperation
	public void addVertices(String graphID, LongSet s) {
		s.forEach((long u) -> getGraph(graphID).vertices.add(u));
	}

	@IdawiOperation
	public String getVertexProperty(String graphID, long u, String name) {
		return getGraph(graphID).vertices.get(u, "properties", () -> new HashMap<String, String>()).get(name);
	}

	@IdawiOperation
	public void setVertexProperty(String graphID, long u, String name, String value) {
		getGraph(graphID).vertices.alter(u, "properties", () -> new HashMap<String, String>(),
				(Map<String, String> p) -> p.put(name, value));
	}

	@IdawiOperation
	public void removeVertex(String graphID, LongList v) {
		var g = getGraph(graphID);

		while (!v.isEmpty()) {
			g.vertices.remove(v.removeLong(v.size() - 1));
		}
	}

	@IdawiOperation
	public long addEdge(String graphID, long from, long to) {
		var e = getGraph(graphID).arcs.add(from, to);
//		Thread
		return e;
	}

	@IdawiOperation
	public void removeEdge(String graphID, LongList e) {
		var g = getGraph(graphID);

		while (!e.isEmpty()) {
			g.arcs.remove(e.removeLong(e.size() - 1));
		}
	}

	@IdawiOperation
	public byte[] showInVis(String graphID) {
		var g = getGraph(graphID);
		var html = new JavaResource(getClass(), "display/graph.html").getByteArray();
		return html;
	}

	@IdawiOperation
	public List<ArcInfo> arcs(String gid) {
		var g = getGraph(gid);
		List<ArcInfo> edges = new ArrayList<>();

		g.arcs.forEach(e -> {
			var i = new ArcInfo();
			i.id = e;
			var ends = g.arcs.ends(e);
			i.from = ends[0];
			i.to = ends[1];
			i.properties = g.arcs.get(e, "properties", () -> null);
			edges.add(i);
			return true;
		});

		return edges;
	}

	@IdawiOperation
	public List<EdgeInfo> edges(String gid) {
		var g = getGraph(gid);
		List<EdgeInfo> edges = new ArrayList<>();

		g.edges.forEach(e -> {
			var i = new EdgeInfo();
			i.id = e;
			i.ends = g.edges.ends(e);
			i.properties = g.edges.get(e, "properties", () -> null);
			edges.add(i);
			return true;
		});

		return edges;
	}

	public static class Info implements Serializable {
		public long id;
		Map<String, String> properties;
	}

	public static class ArcInfo extends Info {
		public long from, to;
	}

	public static class EdgeInfo extends Info {
		public LongSet ends;
	}

	public static class VertexInfo extends Info {
	}

	public static class GraphInfo implements Serializable {
		public String name;
		Map<String, String> properties;
		List<VertexInfo> vertices;
		List<ArcInfo> arcs;
		List<EdgeInfo> edges;
		int nbChanges;
	}

	@IdawiOperation
	public LongList verticesIDs(String gid) {
		var g = getGraph(gid);
		LongList l = new LongArrayList();

		g.vertices.forEach(u -> {
			l.add(u);
			return true;
		});

		return l;
	}

	@IdawiOperation
	public byte[] verticesIDsRAW(String gid) throws IOException {
		var g = getGraph(gid);
		return g.vertices.ids();

	}

	@IdawiOperation
	public byte[] edgesIDsRAW(String gid) throws IOException {
		var g = getGraph(gid);
		return g.arcs.ids();
	}

	@IdawiOperation
	public List<VertexInfo> vertices(String gid) {
		var g = getGraph(gid);
		List<VertexInfo> vertices = new ArrayList<>();

		g.vertices.forEach(v -> {
			var e = new VertexInfo();
			e.id = v;
			e.properties = g.vertices.get(v, "properties", () -> null);
			vertices.add(e);
			return true;

		});

		return vertices;
	}

	@IdawiOperation
	public GraphInfo get(String gid) {
		var g = getGraph(gid);
		var gi = new GraphInfo();
		gi.nbChanges = g.nbChanges();
		gi.arcs = arcs(gid);
		gi.edges = edges(gid);
		gi.vertices = vertices(gid);
		gi.properties = g.getProperties();
		Cout.debugSuperVisible(gi.properties);
		return gi;
	}

	@IdawiOperation
	public List<String> listProblems(String gid) {
		return getGraph(gid).listProblems();
	}

	@IdawiOperation
	public BooleanList containsVertex(String gid, LongList s) {
		return ElementSet.contains(getGraph(gid).vertices, s);
	}

	@IdawiOperation
	public BooleanList containsEdges(String gid, LongList s) {
		return ElementSet.contains(getGraph(gid).vertices, s);
	}

	@IdawiOperation
	public BooleanList containsArcs(String gid, LongList s) {
		return ElementSet.contains(getGraph(gid).arcs, s);
	}

	@IdawiOperation
	public String toDOT(String gid) {
		return og.algo.io.GraphViz.toDOT(getGraph(gid));
	}

	@IdawiOperation
	public byte[] graphviz(String gid, String command, String outputFormat) {
		return GraphViz.toBytes(COMMAND.valueOf(command), toDOT(gid), OUTPUT_FORMAT.valueOf(outputFormat));
	}

	@IdawiOperation
	public Map getGraphInfo(String gid) {
		var g = getGraph(gid);
		var m = new HashMap<>();
		m.put("id", gid);
		m.put("nbVertices", g.vertices.nbEntries());
		m.put("nbEdges", g.arcs.nbEntries());
		return m;
	}

	@IdawiOperation
	public void create(String gid) throws NoSuchMethodException, SecurityException {
		create2(gid, HashGraph.class.getName());
	}

	@IdawiOperation
	public void create2(String gid, String className) throws NoSuchMethodException, SecurityException {
		var g = m.get(gid);

		if (g != null) {
			throw new IllegalArgumentException("graphp already exists");
		}

		Class<? extends Graph> c = Clazz.findClass(className);

		if (c == null)
			throw new IllegalArgumentException("can't get class " + className);

		g = null;

		if (DiskGraph.class.isAssignableFrom(c)) {
			var cc = c.getConstructor(Directory.class);
			var dg = (DiskGraph) Clazz.makeInstance(cc, new Directory(baseDirectory, gid));
			dg.create();
			g = dg;
		} else {
			g = Clazz.makeInstance(c);
		}

		m.put(gid, g);
	}

	@IdawiOperation
	public LongSet pickRandomVertex(String gid, long n) {
		LongSet s = new LongOpenHashSet();
		var g = getGraph(gid);

		while (s.size() < n) {
			s.add(g.vertices.random());
		}

		return s;
	}

	@IdawiOperation
	public LongSet pickRandomEdge(String gid, long n) {
		LongSet s = new LongOpenHashSet();
		var g = getGraph(gid);

		while (s.size() < n) {
			s.add(g.arcs.random());
		}

		return s;
	}

	@IdawiOperation
	public List<Change> changes(String gid, int since) {
		List<Change> l = new ArrayList<>();
		getGraph(gid).forEachChange(since, c -> l.add(c));
		return l;
	}

	@IdawiOperation
	public List<Change> history(String gid) {
		return changes(gid, 0);
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
			g.arcs.add(src, dest);
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
				g.arcs.add(src, dest);
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

	@IdawiOperation
	public BFS.BFSResult bfs(String graphID, long source, long maxDistance, long maxNbVerticesVisited) {
		return BFS.bfs(getGraph(graphID), source, maxDistance, maxNbVerticesVisited);
	}

	@IdawiOperation
	public LongList randomWalk(String graphID, long source, long maxDistance) {
		return RandomWalk.randomWalk(getGraph(graphID), source, maxDistance);
	}

	@IdawiOperation
	public double clusteringCoefficient(String graphID, long v) {
		return CC.clusteringCoefficient(getGraph(graphID), v);
	}

	public void close() {
		for (var g : m.values()) {
			if (g instanceof DiskGraph) {
				((DiskGraph) g).cleanClose();
			}
		}
	}
}
