package og.dynamics;

import java.util.HashMap;

import og.Graph;
import og.GraphDynamics;
import toools.thread.Threads;

public class GridEvolver extends GraphDynamics {
	public final int width, height;
	private double pause;

	public GridEvolver(Graph g, int width, int height, double pause) {
		super(g);
		this.width = width;
		this.height = height;
		this.pause = pause;
		new Thread(() -> apply(g)).start();
	}

	public void apply(Graph g) {
		while (true) {
			for (int i = 0; i < width; ++i) {
				for (int j = 0; j < height; ++j) {
					c(g, i, j, i + 1, j);
					c(g, i, j, i, j + 1);
				}
			}

			while (g.arcs.nbEntries() > 0) {
				g.arcs.remove(g.arcs.random());
				Threads.sleepMs((int) (pause * 1000));
			}
		}
	}

	private void c(Graph g, int i1, int j1, int i2, int j2) {
		if (i2 < width && j2 < height && j2 >= 0) {
			var u = i1 + width * j1;
			var v = i2 + width * j2;

			if (!g.vertices.contains(u)) {
				g.vertices.add(u);
				g.vertices.alter(u, "properties", () -> new HashMap<>(), m -> m.put("label", i1+ "," + j1));
			}

			if (!g.vertices.contains(v)) {
				g.vertices.add(v);
				g.vertices.alter(v, "properties", () -> new HashMap<>(), m -> m.put("label", i2+ "," + j2));
			}

			g.arcs.add(u, v);
			Threads.sleepMs((int) (pause * 1000));
		}
	}
}
