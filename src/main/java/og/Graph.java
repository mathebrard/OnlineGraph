package og;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public abstract List<Change> allChanges();

	public abstract void commitNewChange(Change c);

	public abstract Map<String, String> getProperties();

	public abstract void setProperties(Map<String, String> m);

	public void clear() {
		// clear either vertex set OR edge set
		vertices.impl.clear();
	}

	public void check() {
		arcs.forEach(e -> {
			var ends = arcs.ends(e);
			var from = ends[0];

			if (!vertices.contains(from))
				throw new IllegalStateException("unknown source : " + from);

			if (!vertices.outArcs(from).contains(e))
				throw new IllegalStateException();

			var to = ends[1];

			if (!vertices.contains(to))
				throw new IllegalStateException("unknown destination : " + to);

			if (!vertices.inArcs(to).contains(e))
				throw new IllegalStateException();

			return true;
		});

		edges.forEach(e -> {
			var ends = edges.ends(e);

			for (var u : ends) {
				if (!vertices.contains(u))
					throw new IllegalStateException("unknown edge end : " + u);

				if (!vertices.edges(u).contains(e))
					throw new IllegalStateException();
			}

			return true;
		});

		vertices.forEach(v -> {
			for (var e : vertices.outArcs(v)) {
				if (!arcs.contains(e))
					throw new IllegalStateException("unknown out edge : " + e);
			}

			for (var e : vertices.inArcs(v)) {
				if (!arcs.contains(e))
					throw new IllegalStateException("unknown in edge : " + e);
			}

			return true;
		});
	}

}
