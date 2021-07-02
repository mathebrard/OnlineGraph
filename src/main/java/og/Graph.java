package og;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Graph {
	public final VertexSet vertices;
	public final EdgeSet edges;

	public Graph(ElementSet vertices, ElementSet edges) {
		this.vertices = new VertexSet(vertices, this);
		this.edges = new EdgeSet(edges, this);
		
		var p = new TreeMap<String, String>();
		p.put("background color", "dark grey");

		VertexProperties.forEach(pa -> {
			p.put("default vertex " + pa.getName(), pa.getDefaultValue());
		});

		EdgeProperties.forEach(pa -> {
			p.put("default edge " + pa.getName(), pa.getDefaultValue());
		});

		setProperties(p);
	}

	// graph-related methods

	public abstract List<Change> getHistory();

	public abstract void addChange(Change c);

	public abstract Map<String, String> getProperties();

	public abstract void setProperties(Map<String, String> m);

	public void clear() {
		// clear either vertex set OR edge set
		vertices.impl.clear();
	}

	public void check() {
		edges.forEach(e -> {
			var ends = edges.ends(e);
			var from = ends[0];

			if (!vertices.contains(from))
				throw new IllegalStateException("unknown source : " + from);

			var to = ends[1];

			if (!vertices.contains(to))
				throw new IllegalStateException("unknown destination : " + to);

			return true;
		});

		vertices.forEach(v -> {
			for (var e : vertices.outEdges(v)) {
				if (!edges.contains(e))
					throw new IllegalStateException("unknown out edge : " + e);
			}

			for (var e : vertices.inEdges(v)) {
				if (!edges.contains(e))
					throw new IllegalStateException("unknown in edge : " + e);
			}

			return true;
		});
	}

}
