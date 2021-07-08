package og;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public abstract class Graph implements Serializable {
	public final VertexSet vertices;
	public final ArcSet arcs;
	public final EdgeSet edges;

	public Graph(ElementSet vertices, ElementSet edges, ElementSet arcs) {
		this.vertices = new VertexSet(vertices, this);
		this.arcs = new ArcSet(arcs, this);
		this.edges = new EdgeSet(edges, this);
	}

	public Map<String, String> defaultProperties() {
		var p = new TreeMap<String, String>();
		p.put("background color", "dark grey");
		p.put("brake", "0");

		VertexProperties.forEach(pa -> {
			p.put("default vertex " + pa.getName(), pa.getDefaultValue());
		});

		EdgeProperties.forEach(pa -> {
			p.put("default edge " + pa.getName(), pa.getDefaultValue());
		});

		ArcProperties.forEach(pa -> {
			p.put("default arc " + pa.getName(), pa.getDefaultValue());
		});
		return p;
	}

	// graph-related methods
	public abstract int nbChanges();

	public  abstract void forEachChange(int startIndex, Consumer<Change> c);

	public abstract void commitNewChange(Change c);

	public abstract Map<String, String> getProperties();

	public abstract void setProperties(Map<String, String> m);

	public void clear() {
		// clear either vertex set OR edge set
		vertices.impl.clear();
	}


	public List<String> listProblems() {
		List<String> r = new ArrayList<>();
		
		arcs.forEach(a -> {
			var ends = arcs.ends(a);
			var from = ends[0];

			if (!vertices.contains(from))
				r.add("arc source is not known: " + from);

			if (!vertices.outArcs(from).contains(a))
				r.add("OUT arc not known " + a);

			var to = ends[1];

			if (!vertices.contains(to))
				r.add("arc destination is not known: " + to);

			if (!vertices.inArcs(to).contains(a))
				r.add("IN arc not known " + a);

			return true;
		});

		edges.forEach(e -> {
			var ends = edges.ends(e);

			for (var u : ends) {
				if (!vertices.contains(u))
					r.add("edge incident vertex is not known: " + u);

				if (!vertices.edges(u).contains(e))
					r.add("edge not known " + e);
			}

			return true;
		});

		vertices.forEach(v -> {
			for (var a : vertices.outArcs(v)) {
				if (!arcs.contains(a))
					r.add("unknown OUT arc : " + a);

				if (arcs.source(a) != v)
					r.add("arc source is incorrect. Should be  " + v + " but found " + arcs.source(a));
			}

			for (var a : vertices.inArcs(v)) {
				if (!arcs.contains(a))
					r.add("unknown IN arc : " + a);

				if (arcs.destination(a) != v)
					r.add("arc destination is incorrect. Should be  " + v + " but found " + arcs.destination(a));
			}

			for (var e : vertices.edges(v)) {
				if (!edges.contains(e))
					r.add("unknown edge " + e + " incident to vertex " + v);

				if (!edges.ends(e).contains(v))
					r.add("vertex " + v + " not founds in ends of edge " + e);
			}

			return true;
		});
		
		return r;
	}


}
