package og;

import java.util.HashMap;

public class RandomEvolver {

	public static void apply(HashGraph g) {
		g.check();
		double targetN = 50;
		double targetD = 3;
		double nbVertices = g.vertices.nbEntries();
		double nbEdges = g.edges.nbEntries();
		double degree = nbEdges / nbVertices;

		if (Math.random() < -0.5 * nbVertices / targetN + 1) {
			long u = g.vertices.add();
			var to = g.vertices.find(1, condition -> !g.vertices.isIsolated(condition), otherwise -> true);
			g.edges.add(u, to.getLong(0));
		}

		if (Math.random() < 0.5 * nbVertices / targetN) {
			g.vertices.remove(g.vertices.random());
		}

		if (Math.random() < -0.5 * degree / targetD + 1) {
			var from = g.vertices.find(1, u -> g.vertices.isIsolated(u));

			while (from.isEmpty()) {
				from.add(g.vertices.random());
			}

			var to = g.vertices.find(1, u -> !g.vertices.isIsolated(u));

			while (to.isEmpty()) {
				to.add(g.vertices.random());
			}

			g.edges.add(from.getLong(0), to.getLong(0));
		}

		if (Math.random() < 0.5 * degree / targetD) {
			g.edges.remove(g.edges.random());
		}

		if (Math.random() < 0.4 && g.vertices.nbEntries() > 0) {
			var u = g.vertices.random();
			var p = g.vertices.get(u, "properties", () -> new HashMap<String, String>());
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

			g.vertices.set(u, "properties", p);
		}

		if (Math.random() < 0.4 && g.edges.nbEntries() > 0) {
			var e = g.edges.random();
			var p = g.edges.get(e, "properties", () -> new HashMap<String, String>());
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

			g.edges.set(e, "properties", p);
		}
	}

}
