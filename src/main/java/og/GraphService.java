package og;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import og.algo.BFS;
import og.algo.CC;
import og.algo.RandomWalk;
import toools.gui.GraphViz;
import toools.gui.GraphViz.COMMAND;
import toools.gui.GraphViz.OUTPUT_FORMAT;
import toools.io.Cout;
import toools.io.file.Directory;
import toools.thread.Threads;

public class GraphService extends Service {

	public static final Directory baseDirectory = new Directory("$HOME/.og");

	static {
		baseDirectory.ensureExists();
	}

	public GraphService(Component component) {
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
			var p = new TreeMap<String, String>();
			p.put("background color", "dark grey");
	
			VertexProperties.forEach(pa ->  {
				p.put("default vertex " + pa.getName(), pa.getDefaultValue());
			});

			EdgeProperties.forEach(pa ->  {
				p.put("default edge " + pa.getName(), pa.getDefaultValue());
			});

			g.setProperties(p);
		}

		Threads.newThread_loop(1000, () -> true, () -> {
			RandomEvolver.apply(g);
		});
	}

	private void gnm(Graph g, int n, int m) {
		for (int i = 0; i < n; ++i) {
			g.vertices.add();
		}

		for (int i = 0; i < m; ++i) {
			var u = g.vertices.random();
			var v = g.vertices.random();
			g.edges.add(u, v);
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
		return getGraph(graphID).vertices.add();
	}

	@IdawiOperation
	public void addVertex(String graphID, long u) {
		getGraph(graphID).vertices.add(u);
	}

	@IdawiOperation
	public void removeVertex(String graphID, long v) {
		getGraph(graphID).vertices.remove(v);
	}

	@IdawiOperation
	public long addEdge(String graphID, long from, long to) {
		return getGraph(graphID).edges.add(from, to);
	}

	@IdawiOperation
	public void removeEdge(String graphID, long e) {
		getGraph(graphID).edges.remove(e);
	}

	@IdawiOperation
	public List<EdgeInfo> edges(String gid) {
		var g = getGraph(gid);
		List<EdgeInfo> edges = new ArrayList<>();

		g.edges.forEach(e -> {
			var i = new EdgeInfo();
			i.id = e;
			var ends = g.edges.ends(e);
			i.from = ends[0];
			i.to = ends[1];
			i.props = g.edges.get(e, "properties", () -> new HashMap<String, String>());
			edges.add(i);
			return true;
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

		g.vertices.forEach(u -> {
			l.add(u);
			return true;

		});

		return l;
	}

	@IdawiOperation
	public List<VertexInfo> vertices(String gid) {
		var g = getGraph(gid);
		List<VertexInfo> vertices = new ArrayList<>();

		g.vertices.forEach(v -> {
			var e = new VertexInfo();
			e.id = v;
			e.props = g.vertices.get(v, "properties", () -> new HashMap<String, String>());
			vertices.add(e);
			return true;

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
		m.put("nbEdges", g.edges.nbEntries());
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
		return g.vertices.random();
	}

	@IdawiOperation
	public long pickRandomEdge(String gid) {
		var g = getGraph(gid);
		return g.edges.random();
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
			g.edges.add(src, dest);
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
				g.edges.add(src, dest);
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
}
