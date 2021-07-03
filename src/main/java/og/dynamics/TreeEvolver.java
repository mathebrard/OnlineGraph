package og.dynamics;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import og.Graph;
import og.GraphDynamics;
import toools.io.Cout;
import toools.thread.Threads;

public class TreeEvolver extends GraphDynamics {

	public TreeEvolver(Graph g) {
		super(g);
		new Thread(() -> apply(g)).start();
	}

	public void apply(Graph g) {
		var l = new LongArrayList();
		l.add(g.vertices.add());

		while (true) {
			while (g.vertices.nbEntries() < 100) {	

				var u = g.vertices.random();
				var v = g.vertices.add();
				l.add(v);
				g.arcs.add(u, v);
				Threads.sleepMs(500);
			}

			while (l.size() > 1) {
				g.vertices.remove(l.removeLong(l.size() - 1));
				Threads.sleepMs(500);
			}
		}
	}
}
