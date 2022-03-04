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
import idawi.TypedInnerOperation;
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

		if (false) {
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

	public class gnm extends TypedInnerOperation {
		public void f(String graphID, int n, int m) {
			GNM.gnm(getGraph(graphID), n, m);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class listGraphs extends TypedInnerOperation {
		public Set<String> f() {
			return new TreeSet<>(m.keySet());
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class addRandomVertex extends TypedInnerOperation {
		public long f(String graphID) {
			return getGraph(graphID).vertices.add();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class addVertices extends TypedInnerOperation {
		public void f(String graphID, LongSet s) {
			s.forEach((long u) -> getGraph(graphID).vertices.add(u));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsEdges extends TypedInnerOperation {
		public String getVertexProperty(String graphID, long u, String name) {
			return getGraph(graphID).vertices.get(u, "properties", () -> new HashMap<String, String>()).get(name);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class setVertexProperty extends TypedInnerOperation {
		public void f(String graphID, long u, String name, String value) {
			getGraph(graphID).vertices.alter(u, "properties", () -> new HashMap<String, String>(),
					(Map<String, String> p) -> p.put(name, value));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class removeVertex extends TypedInnerOperation {
		public void f(String graphID, LongList v) {
			var g = getGraph(graphID);

			while (!v.isEmpty()) {
				g.vertices.remove(v.removeLong(v.size() - 1));
			}
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class addEdge extends TypedInnerOperation {
		public long f(String graphID, long from, long to) {
			var e = getGraph(graphID).arcs.add(from, to);
//		Thread
			return e;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class removeEdge extends TypedInnerOperation {
		public void f(String graphID, LongList e) {
			var g = getGraph(graphID);

			while (!e.isEmpty()) {
				g.arcs.remove(e.removeLong(e.size() - 1));
			}
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class showInVis extends TypedInnerOperation {
		public byte[] f(String graphID) {
			var g = getGraph(graphID);
			var html = new JavaResource(getClass(), "display/graph.html").getByteArray();
			return html;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class arcs extends TypedInnerOperation {
		public List<ArcInfo> f(String gid) {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class edges extends TypedInnerOperation {
		public List<EdgeInfo> f(String gid) {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
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

	public class verticesIDs extends TypedInnerOperation {
		public LongList f(String gid) {
			var g = getGraph(gid);
			LongList l = new LongArrayList();

			g.vertices.forEach(u -> {
				l.add(u);
				return true;
			});

			return l;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class verticesIDsRAW extends TypedInnerOperation {
		public byte[] f(String gid) throws IOException {
			var g = getGraph(gid);
			return g.vertices.ids();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class edgesIDsRAW extends TypedInnerOperation {
		public byte[] f(String gid) throws IOException {
			var g = getGraph(gid);
			return g.arcs.ids();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class vertices extends TypedInnerOperation {
		public List<VertexInfo> f(String gid) {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class get extends TypedInnerOperation {

		public GraphInfo get(String gid) {
			var g = getGraph(gid);
			var gi = new GraphInfo();
			gi.nbChanges = g.nbChanges();
			gi.arcs = lookup(arcs.class).f(gid);
			gi.edges = lookup(edges.class).f(gid);
			gi.vertices = lookup(vertices.class).f(gid);
			gi.properties = g.getProperties();
			Cout.debugSuperVisible(gi.properties);
			return gi;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class GraphSize implements Serializable {
		long nbVertices, nbArcs;
	}

	public class size extends TypedInnerOperation {
		public GraphSize f(String gid) {
			var g = getGraph(gid);
			var s = new GraphSize();
			s.nbVertices = g.vertices.nbEntries();
			s.nbArcs = g.edges.nbEntries();
			return s;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class listProblems extends TypedInnerOperation {
		public List<String> f(String gid) {
			return getGraph(gid).listProblems();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsVertex extends TypedInnerOperation {
		public BooleanList f(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).vertices, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsVertices extends TypedInnerOperation {
		public BooleanList f(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).vertices, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsArcs extends TypedInnerOperation {
		public BooleanList containsArcs(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).arcs, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class toDOT extends TypedInnerOperation {
		public String f(String gid) {
			return og.algo.io.GraphViz.toDOT(getGraph(gid));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class graphviz extends TypedInnerOperation {
		public byte[] f(String gid, String command, String outputFormat) {
			var dot = lookup(toDOT.class).f(gid);
			return GraphViz.toBytes(COMMAND.valueOf(command), dot, OUTPUT_FORMAT.valueOf(outputFormat));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class getGraphInfo extends TypedInnerOperation {
		public Map getGraphInfo(String gid) {
			var g = getGraph(gid);
			var m = new HashMap<>();
			m.put("id", gid);
			m.put("nbVertices", g.vertices.nbEntries());
			m.put("nbEdges", g.arcs.nbEntries());
			return m;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class create extends TypedInnerOperation {
		public void f(String gid) throws NoSuchMethodException, SecurityException {
			lookup(create2.class).f(gid, HashGraph.class.getName());
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class create2 extends TypedInnerOperation {
		public void f(String gid, String className) throws NoSuchMethodException, SecurityException {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class pickRandomVertex extends TypedInnerOperation {
		public LongSet pickRandomVertex(String gid, long n) {
			LongSet s = new LongOpenHashSet();
			var g = getGraph(gid);

			while (s.size() < n) {
				s.add(g.vertices.random());
			}

			return s;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class pickRandomEdge extends TypedInnerOperation {
		public LongSet pickRandomEdge(String gid, long n) {
			LongSet s = new LongOpenHashSet();
			var g = getGraph(gid);

			while (s.size() < n) {
				s.add(g.arcs.random());
			}

			return s;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class changes extends TypedInnerOperation {
		public List<Change> f(String gid, int since) {
			List<Change> l = new ArrayList<>();
			getGraph(gid).forEachChange(since, c -> l.add(c));
			return l;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class history extends TypedInnerOperation {
		public List<Change> history(String gid) {
			return lookup(changes.class).f(gid, 0);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/*
	 * Uses https://github.com/paypal/digraph-parser
	 */
	public class importDot extends TypedInnerOperation {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class importEdges extends TypedInnerOperation {
		public String importEdges(String gid, byte[] edges) throws IOException {
			importLines(gid, edges, (g, newLine) -> {
				var t = newLine.trim().split("[^0-9]+");
				long src = Long.valueOf(t[0]);
				long dest = Long.valueOf(t[1]);
				g.arcs.add(src, dest);
			});

			return "ok";
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class imporADJ extends TypedInnerOperation {
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

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
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

	public class bfs extends TypedInnerOperation {
		public BFS.BFSResult bfs(String graphID, long source, long maxDistance, long maxNbVerticesVisited) {
			return BFS.bfs(getGraph(graphID), source, maxDistance, maxNbVerticesVisited);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class randomWalk extends TypedInnerOperation {
		public LongList randomWalk(String graphID, long source, long maxDistance) {
			return RandomWalk.randomWalk(getGraph(graphID), source, maxDistance);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class clusteringCoefficient extends TypedInnerOperation {
		public double clusteringCoefficient(String graphID, long v) {
			return CC.clusteringCoefficient(getGraph(graphID), v);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void close() {
		for (var g : m.values()) {
			if (g instanceof DiskGraph) {
				((DiskGraph) g).cleanClose();
			}
		}
	}
}
