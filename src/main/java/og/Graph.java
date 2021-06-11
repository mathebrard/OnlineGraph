package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongList;
import og.GraphStorageService.EdgeInfo;
import og.GraphStorageService.VertexInfo;
import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class Graph<V, E> extends AbstractGraph<V, E> {
	final Directory d;
	final DiskStore vertices, edges;

	final LongArrayList emptyList = new LongArrayList();
	private long nbChange = 0;

	public Graph(Directory d) {
		this.d = d;
		this.vertices = new FlatFileStore(new Directory(d, "vertices"));
		this.edges = new FlatFileStore(new Directory(d, "edges"));
	}

	@Override
	public String toString() {
		return d.getPath();
	}

	@Override
	public void create() {
		d.ensureExists();
		vertices.d.ensureExists();
		edges.d.ensureExists();
		writeProperties(new HashMap<>());
	}

	@Override
	public boolean exists() {
		return d.exists();
	}

	@Override
	public long nbVertices() {
		return vertices.nbEntries();
	}

	@Override
	public long nbEdges() {
		return edges.nbEntries();
	}

	@Override
	public long addVertex() {
		long id = ThreadLocalRandom.current().nextLong();
		vertices.add(id);
		var i = new VertexInfo();
		i.id = id;
		addChange(new Change.AddVertex(nbChange++, i));
		return id;
	}

	@Override
	public void removeVertex(long u) {
		vertices.remove(u);
		addChange(new Change.RemoveVertex(nbChange++, u));
	}

	@Override
	public long addEdge(long from, long to) {
		long e = ThreadLocalRandom.current().nextLong();
		edges.add(e);
		edges.set(e, "ends", new long[] { from, to });
		vertices.alter(from, "outVertices", () -> new LongArrayList(), (LongList outs) -> outs.add(to));
		var i = new EdgeInfo();
		i.id = e;
		i.from = from;
		i.to = to;
		addChange(new Change.AddEdge(nbChange++, i));
		return e;
	}

	@Override
	public void removeEdge(long e) {
		vertices.alter(source(e), "outVertices", null, (LongList set) -> set.removeLong(set.indexOf(e)));
		edges.remove(e);
		addChange(new Change.RemoveEdge(nbChange++, e));
	}

	public V readVertex(long u, String name, Supplier<V> defaultValueSupplier) {
		return vertices.get(u, name, defaultValueSupplier);
	}

	public void writeVertex(long u, String name, V p) {
		vertices.set(u, name, p);
	}

	public E readEdge(long e, String name, Supplier<E> defaultValueSupplier) {
		return (E) edges.get(e, name, defaultValueSupplier);
	}

	public void writeEdge(long e, String name, E p) {
		edges.set(e, name, p);
	}

	public long[] ends(long e) {
		return (long[]) edges.get(e, "ends", null);
	}

	@Override
	public long source(long e) {
		return ends(e)[0];
	}

	@Override
	public long destination(long e) {
		return ends(e)[1];
	}

	@Override
	public LongList inEdges(long v) {
		return vertices.get(v, "inEdges", null);
	}

	@Override
	public LongList outEdges(long v) {
		return vertices.get(v, "outs", null);
	}

	@Override
	public long pickRandomVertex() {
		return vertices.random();
	}

	@Override
	public long pickRandomEdge() {
		return edges.random();
	}

	@Override
	public void traverseVertices(LongConsumer c) {
		vertices.files(c);
	}

	@Override
	public void traverseEdges(LongConsumer c) {
		edges.files(c);
	}

	@Override
	public void clear() {
		d.deleteRecursively();
		d.create();
	}

	public Map<String, String> readProperties() {
		return (Map<String, String>) new RegularFile(d, "properties.ser").getContentAsJavaObject();
	}

	public void writeProperties(Map<String, String> m) {
		new RegularFile(d, "properties.ser").setContentAsJavaObject(m);
	}

	public synchronized List<Change> getHistory() {
		var f = new RegularFile(d, "history.ser");

		if (f.exists()) {
			return (List<Change>) f.getContentAsJavaObject();
		} else {
			return new ArrayList<>();
		}
	}

	public synchronized void addChange(Change c) {
		var f = new RegularFile(d, "history.ser");
		List<Change> l = null;

		if (f.exists()) {
			l = (List<Change>) f.getContentAsJavaObject();
		} else {
			l = new ArrayList<>();
		}

		l.add(c);
		f.setContentAsJavaObject(l);
	}

}
