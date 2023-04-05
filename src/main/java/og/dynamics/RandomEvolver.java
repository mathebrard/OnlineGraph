package og.dynamics;

import java.util.HashMap;

import og.ArcProperties;
import og.EdgeProperties;
import og.ElementProperties;
import og.Graph;
import og.GraphDynamics;
import og.VertexProperties;
import toools.thread.Threads;

public class RandomEvolver extends GraphDynamics {

	public RandomEvolver(Graph g) {
		super(g);
		Threads.newThread_loop_periodic(1000, () -> true, () -> apply(g));
	}

	public void apply(Graph g) {
		var pb = g.listProblems();

		if (!pb.isEmpty())
			throw new IllegalStateException(pb.toString());

		double targetN = 50;
		double targetD = 3;
		double nbVertices = g.vertices.nbEntries();
		double nbEdges = g.arcs.nbEntries();
		double degree = nbEdges / nbVertices;

		if (Math.random() < -0.5 * nbVertices / targetN + 1) {
			long u = g.vertices.add();
			var to = g.vertices.find(1, condition -> !g.vertices.isIsolated(condition));

			if (to.isEmpty()) {
				to.add(g.vertices.random());
			}

			g.arcs.add(u, to.getLong(0));
		}

		if (Math.random() < 0.5 * nbVertices / targetN) {
			g.vertices.remove(g.vertices.random());
		}

		if (Math.random() < -0.5 * degree / targetD + 1) {
			var from = g.vertices.find(1, u -> g.vertices.isIsolated(u));

			if (from.isEmpty()) {
				from.add(g.vertices.random());
			}

			var to = g.vertices.find(1, u -> !g.vertices.isIsolated(u));

			if (to.isEmpty()) {
				to.add(g.vertices.random());
			}

			if (Math.random() < 0.5) {
				g.arcs.add(from.getLong(0), to.getLong(0));
			} else {
				g.edges.add(from.getLong(0), to.getLong(0));
			}
		}

		if (Math.random() < 0.5 * degree / targetD) {
			g.arcs.remove(g.arcs.random());
		}

		if (Math.random() < 0.4 && g.vertices.nbEntries() > 0) {
			var u = g.vertices.random();
			var p = g.vertices.get(u, "properties", () -> new HashMap<String, String>());
			double pp = 0.5;

			p.put(VertexProperties.location.getName(), VertexProperties.location.random());

			if (Math.random() < pp)
				p.put(VertexProperties.scale.getName(), VertexProperties.scale.random());
			if (Math.random() < pp)
				p.put(ElementProperties.width.getName(), ElementProperties.width.random());
			if (Math.random() < pp)
				p.put(ElementProperties.color.getName(), ElementProperties.color.random());
			if (Math.random() < pp)
				p.put(VertexProperties.fillColor.getName(), VertexProperties.fillColor.random());
			if (Math.random() < pp)
				p.put(ElementProperties.labelColor.getName(), ElementProperties.labelColor.random());
			if (Math.random() < pp)
				p.put(ElementProperties.label.getName(), ElementProperties.label.random());
			if (Math.random() < pp)
				p.put(VertexProperties.shape.getName(), VertexProperties.shape.random());
			if (Math.random() < pp)
				p.put("foo", "" + Math.random());
			if (Math.random() < pp)
				p.put("bar", "" + (int) ((Math.random() * 100)));

			g.vertices.set(u, "properties", p);
		}

		if (Math.random() < 0.4 && g.arcs.nbEntries() > 0) {
			var a = g.arcs.random();
			var p = g.arcs.get(a, "properties", () -> new HashMap<String, String>());
			double pp = 0.5;

			if (Math.random() < pp)
				p.put(ArcProperties.arrowLocation.getName(), ArcProperties.arrowLocation.random());
			if (Math.random() < pp)
				p.put(ArcProperties.arrowSize.getName(), ArcProperties.arrowShape.random());
			if (Math.random() < pp)
				p.put(ArcProperties.arrowShape.getName(), ArcProperties.arrowShape.random());
			if (Math.random() < pp)
				p.put(EdgeProperties.style.getName(), EdgeProperties.style.random());
			if (Math.random() < pp)
				p.put(ElementProperties.color.getName(), ElementProperties.color.random());
			if (Math.random() < pp)
				p.put(ElementProperties.label.getName(), ElementProperties.label.random());
			if (Math.random() < pp)
				p.put(ElementProperties.width.getName(), ElementProperties.width.random());

			g.arcs.set(a, "properties", p);
		}

		if (Math.random() < 0.4 && g.edges.nbEntries() > 0) {
			var e = g.edges.random();
			var p = g.edges.get(e, "properties", () -> new HashMap<String, String>());
			double pp = 0.5;

			if (Math.random() < pp)
				p.put(EdgeProperties.style.getName(), EdgeProperties.style.random());
			if (Math.random() < pp)
				p.put(ElementProperties.color.getName(), ElementProperties.color.random());
			if (Math.random() < pp)
				p.put(ElementProperties.label.getName(), ElementProperties.label.random());
			if (Math.random() < pp)
				p.put(ElementProperties.width.getName(), ElementProperties.width.random());

			g.edges.set(e, "properties", p);
		}
	}
}
