package og;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongList;
import toools.io.file.Directory;

public class BasicGraph extends AbstractGraph {
	final Directory d;
	final FileTree vertices, edges;
	final Long2ObjectMap<LongList> outEdgesCache = new Long2ObjectOpenHashMap<>();

	final LongArrayList emptyList = new LongArrayList();

	public BasicGraph(Directory d) {
		this.d = d;
		this.vertices = new FileTree(new Directory(d, "vertices"));
		this.edges = new FileTree(new Directory(d, "edges"));
	}

	@Override
	public void create() {
		d.ensureExists();
		vertices.d.ensureExists();
		edges.d.ensureExists();
	}

	@Override
	public long nbVertices() {
		return vertices.size();
	}

	@Override
	public long nbEdges() {
		return edges.size();
	}

	@Override
	public long addVertex() {
		long id = ThreadLocalRandom.current().nextLong();
		vertices.add("" + id);
		return id;
	}

	@Override
	public void removeVertex(long u) {
		vertices.remove("" + u);
	}

	@Override
	public long addEdge(long from, long to) {
		long id = ThreadLocalRandom.current().nextLong();
		var edgeFile = edges.add("" + id);
		Properties p = new Properties();
		p.put("from", "" + from);
		p.put("to", "" + to);
		var os = edgeFile.createWritingStream();

		try {
			p.store(os, null);
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var set = outEdgesCache.get(from);

		if (set == null) {
			outEdgesCache.put(from, set = new LongArrayList());
		}

		set.add(id);

		return id;
	}

	@Override
	public void removeEdge(long e) {
		var from = source(e);
		var l = outEdgesCache.get(from);
		int i = l.indexOf(e);
		l.removeLong(i);
		edges.remove("" + e);
	}

	@Override
	public long source(long e) {
		String[] a = new String(edges.getContent("" + e)).split(" ");
		return Long.valueOf(a[0]);
	}

	@Override
	public long destination(long e) {
		String[] a = new String(edges.getContent("" + e)).split(" ");
		return Long.valueOf(a[1]);
	}

	@Override
	public LongList inEdges(long v) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public LongList outEdges(long v) {
		return outEdgesCache.getOrDefault(v, emptyList);
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
	public void traverseEdges(EdgeConsumer e) {
		edges.files(f -> {
			var p = new Properties();
			var is = f.createReadingStream();

			try {
				p.load(is);
				is.close();
			} catch (IOException err) {
				throw new RuntimeException(err);
			}
//			Cout.debugSuperVisible(f.getName() + "   " + p);
			long from = Long.valueOf(p.remove("from").toString());
			long to = Long.valueOf(p.remove("to").toString());
			e.accept(Long.valueOf(f.getName()), from, to, p);
		});
	}

	@Override
	public void traverseVertices(LongConsumer e) {
		vertices.files(f -> e.accept((long) Long.valueOf(f.getName())));
	}

	@Override
	public void traverseVertices(VertexConsumer v) {
		vertices.files(f -> {
			var p = new Properties();
			var is = f.createReadingStream();

			try {
				p.load(is);
				is.close();
			} catch (IOException err) {
				throw new RuntimeException(err);
			}

			v.accept(Long.valueOf(f.getName()), p);
		});
	}

	@Override
	public void clear() {
		d.deleteRecursively();
		d.create();
		outEdgesCache.clear();
	}

}
