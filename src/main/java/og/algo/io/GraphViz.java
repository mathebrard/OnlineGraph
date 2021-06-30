package og.algo.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import og.EdgeProperties;
import og.Graph;
import og.VertexProperties;

public class GraphViz {
	public static String toDOT(Graph g) {
		var bos = new ByteArrayOutputStream();
		var out = new PrintStream(bos);
		out.println("# graph \"" + g + "\" has " + g.vertices.nbEntries() + " vertices and " + g.edges.nbEntries()
				+ " edges");
		out.println("digraph {");

		out.print("node ");
		var m = new HashMap<String, String>();
		VertexProperties.forEach(p -> m.put(p.getName(), p.getDefaultValue()));
		map2string(m, out);

		out.print("edge ");
		m.clear();
		EdgeProperties.forEach(p -> m.put(p.getName(), p.getDefaultValue()));
		map2string(m, out);

		out.println();

		g.vertices.forEach(u -> {
			out.print("\t" + u);
			var p = g.vertices.get(u, "properties", () -> new HashMap<String, String>());
			var gvp = new HashMap<String, String>();

			var shape = p.get(VertexProperties.shape.getName());

			if (shape != null) {
				gvp.put("shape", VertexProperties.shape.toGraphviz(shape));
			}

			var borderColor = p.get(VertexProperties.borderColor.getName());

			if (borderColor != null) {
				gvp.put("color", VertexProperties.borderColor.toGraphviz(borderColor));
			}

			var fillColor = p.get(VertexProperties.fillColor.getName());

			if (fillColor != null) {
				gvp.put("style", "filled");
				gvp.put("fillcolor", VertexProperties.fillColor.toGraphviz(fillColor));
			}

			var borderWidth = p.get(VertexProperties.borderWidth.getName());
			gvp.put("penwidth", borderWidth == null ? "1" : "" + borderWidth);

			var label = p.get(VertexProperties.label.getName());

			if (label != null) {
				gvp.put("label", VertexProperties.label.toGraphviz(label));
			}

			var scale = p.get(VertexProperties.scale.getName());

			if (scale != null) {
				gvp.put("width", VertexProperties.scale.toGraphviz(scale));
				gvp.put("height", VertexProperties.scale.toGraphviz(scale));
			}

//var labelColor = p.get(VertexProperties.labelColor.getName());

			map2string(gvp, out);
			out.println();
			return true;
		});

		g.edges.forEach(e -> {
			var ends = g.edges.ends(e);
			out.print("\t" + ends[0] + " -> " + ends[1]);
			var p = g.edges.get(e, "properties", () -> new HashMap<String, String>());
			var gvp = new HashMap<String, String>();

			var arrowShape = p.get(EdgeProperties.arrowShape.getName());

			if (arrowShape != null) {
				gvp.put("arrowhead", EdgeProperties.arrowShape.toGraphviz(arrowShape));
			}

			var style = p.get(EdgeProperties.style.getName());

			if (style != null) {
				gvp.put("style", EdgeProperties.style.toGraphviz(style));
			}

			map2string(gvp, out);
			out.println();
			return true;

		});

		out.println("}");
		out.flush();

		var dot = new String(bos.toByteArray());
		return dot;
	}

	private static void map2string(Map<String, String> p, PrintStream out) {
		// p.keySet().removeIf(k -> p.get(k) == null);

		if (p.isEmpty())
			return;

		out.print("\t [");
		var i = p.entrySet().iterator();

		while (i.hasNext()) {
			var e = i.next();
			out.print(e.getKey() + "=\"" + e.getValue() + "\"");

			if (i.hasNext()) {
				out.print(", ");
			}
		}

		out.print("]");
	}
}
