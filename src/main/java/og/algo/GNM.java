package og.algo;

import og.Graph;

public class GNM {
	public static void gnm(Graph g, int n, int m) {
		for (int i = 0; i < n; ++i) {
			g.vertices.add();
		}

		for (int i = 0; i < m; ++i) {
			var u = g.vertices.random();
			var v = g.vertices.random();
			g.edges.add(u, v);
		}
	}

}
