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
import idawi.InnerClassOperation;
import idawi.OperationParameterList;
import idawi.Service;
import idawi.TypedInnerClassOperation;
import idawi.messaging.MessageQueue;
import idawi.service.web.WebService;
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
import toools.exceptions.NotYetImplementedException;
import toools.io.Cout;
import toools.io.JavaResource;
import toools.io.file.Directory;
import toools.reflect.Clazz;
import toools.thread.Threads;

public class GraphService extends Service {

	public static final Directory baseDirectory = new Directory("$HOME/.og");

	static {
		baseDirectory.ensureExists();
		WebService.friendyName_service.put("graph", GraphService.class);
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

		registerOperation(new addEdge());
		registerOperation(new addRandomVertex());
		registerOperation(new addVertices());
		registerOperation(new arcs());
		registerOperation(new bfs());
		registerOperation(new changes());
		registerOperation(new clusteringCoefficient());
		registerOperation(new containsArcs());
		registerOperation(new containsEdges());
		registerOperation(new containsVertex());
		registerOperation(new containsVertices());
		registerOperation(new create());
		registerOperation(new create2());
		registerOperation(new edges());
		registerOperation(new edgesIDsRAW());
		registerOperation(new get());
		registerOperation(new get2());
		registerOperation(new getGraphInfo());
		registerOperation(new graphviz());
		registerOperation(new history());
		registerOperation(new imporADJ());
		registerOperation(new importDot());
		registerOperation(new importEdges());
		registerOperation(new listGraphs());
		registerOperation(new listProblems());
		registerOperation(new pickRandomEdge());
		registerOperation(new pickRandomVertex());
		registerOperation(new setVertexProperty());
		registerOperation(new showInVis());
		registerOperation(new size());
		registerOperation(new toDOT());
		registerOperation(new vertices());
		registerOperation(new verticesIDs());
		registerOperation(new verticesIDsRAW());
		registerOperation(new countGraphs());
	}

	public Graph getGraph(String gid) {
		var g = m.get(gid);

		if (g == null)
			throw new IllegalArgumentException("no such graph " + gid);

		return g;
	}

	public class gnm extends TypedInnerClassOperation {
		public void f(String graphID, int n, int m) {
			GNM.gnm(getGraph(graphID), n, m);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class listGraphs extends TypedInnerClassOperation {
		public Set<String> f() {
			return new TreeSet<>(m.keySet());
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class countGraphs extends TypedInnerClassOperation {
		public int f() {
			return m.size();
		}

		@Override
		public String getDescription() {
			return "tells the number of graphs in the service";
		}
	}

	public class addRandomVertex extends TypedInnerClassOperation {
		public long f(String graphID) {
			return getGraph(graphID).vertices.add();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class addVertices extends TypedInnerClassOperation {
		public void f(String graphID, LongSet s) {
			s.forEach((long u) -> getGraph(graphID).vertices.add(u));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsEdges extends TypedInnerClassOperation {
		public String getVertexProperty(String graphID, long u, String name) {
			return getGraph(graphID).vertices.get(u, "properties", () -> new HashMap<String, String>()).get(name);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class setVertexProperty extends TypedInnerClassOperation {
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

	public class removeVertex extends TypedInnerClassOperation {
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

	public class addEdge extends TypedInnerClassOperation {
		public long f(String graphID, long from, long to) {
			var g = getGraph(graphID);
			var e = g.arcs.add(from, to);
			return e;
		}

		@Override
		public String getDescription() {
			return "add an edge and returns the ID of the new edge";
		}
	}

	public class removeEdge extends TypedInnerClassOperation {
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

	public class showInVis extends TypedInnerClassOperation {
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

	public class arcs extends TypedInnerClassOperation {
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
			return "get the list of arcs";
		}
	}

	public class edges extends TypedInnerClassOperation {
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

	public class verticesIDs extends TypedInnerClassOperation {
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

	public class verticesIDsRAW extends TypedInnerClassOperation {
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

	public class edgesIDsRAW extends TypedInnerClassOperation {
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

	public class vertices extends TypedInnerClassOperation {
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

	public class get extends TypedInnerClassOperation {

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
			return "gets the content of the graph";
		}
	}

	public class get2 extends InnerClassOperation {

		@Override
		public String getDescription() {
			return "get the content, then all the changes";
		}

		@Override
		public void impl(MessageQueue in) throws Throwable {
			var tm = in.poll_sync();
			var pml = (OperationParameterList) tm.content;

			if (pml.isEmpty())
				throw new IllegalArgumentException(
						"missing graph name, available graphs: " + lookup(listGraphs.class).f());

			String gid = (String) pml.get(0);
			var g = getGraph(gid);

			// send the full graph initially
			var gi = new GraphInfo();
			gi.nbChanges = g.nbChanges();
			gi.arcs = lookup(arcs.class).f(gid);
			gi.edges = lookup(edges.class).f(gid);
			gi.vertices = lookup(vertices.class).f(gid);
			gi.properties = g.getProperties();
//			Cout.debugSuperVisible(gi.properties);
			System.err.println("sending graph");
			reply(tm, gi);
			int date = g.nbChanges();

			while (true) {
				System.err.println(g.nbChanges() + "changes");
				g.forEachChange(date, c -> reply(tm, c));
				date = g.nbChanges();
				Threads.sleep(1);
			}
		}
	}

	public static class GraphSize implements Serializable {
		long nbVertices, nbArcs;
	}

	public class size extends TypedInnerClassOperation {
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

	public class listProblems extends TypedInnerClassOperation {
		public List<String> f(String gid) {
			return getGraph(gid).listProblems();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsVertex extends TypedInnerClassOperation {
		public BooleanList f(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).vertices, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsVertices extends TypedInnerClassOperation {
		public BooleanList f(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).vertices, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class containsArcs extends TypedInnerClassOperation {
		public BooleanList containsArcs(String gid, LongList s) {
			return ElementSet.contains(getGraph(gid).arcs, s);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class toDOT extends TypedInnerClassOperation {
		public String f(String gid) {
			return og.algo.io.GraphViz.toDOT(getGraph(gid));
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class graphviz extends TypedInnerClassOperation {
		public byte[] f(String gid, String command, String outputFormat) {
			var dot = lookup(toDOT.class).f(gid);
			throw new NotYetImplementedException();
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class getGraphInfo extends TypedInnerClassOperation {
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

	public class create extends TypedInnerClassOperation {
		public void f(String gid) throws NoSuchMethodException, SecurityException {
			lookup(create2.class).f(gid, HashGraph.class.getName());
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class create2 extends TypedInnerClassOperation {
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

	public class pickRandomVertex extends TypedInnerClassOperation {
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

	public class pickRandomEdge extends TypedInnerClassOperation {
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

	public class changes extends TypedInnerClassOperation {
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

	public class history extends TypedInnerClassOperation {
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
	public class importDot extends TypedInnerClassOperation {
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

	public class importEdges extends TypedInnerClassOperation {
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

	public class imporADJ extends TypedInnerClassOperation {
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

	public class bfs extends TypedInnerClassOperation {
		public BFS.BFSResult bfs(String graphID, long source, long maxDistance, long maxNbVerticesVisited) {
			return BFS.bfs(getGraph(graphID), source, maxDistance, maxNbVerticesVisited);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class randomWalk extends TypedInnerClassOperation {
		public LongList randomWalk(String graphID, long source, long maxDistance) {
			return RandomWalk.randomWalk(getGraph(graphID), source, maxDistance);
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public class clusteringCoefficient extends TypedInnerClassOperation {
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
