package og.dynamics;

import og.Graph;
import og.GraphDynamics;
import toools.io.Cout;
import toools.thread.Threads;

public class Grow extends GraphDynamics {
	private double pause;
	private double growFactor;

	public Grow(Graph g, double growFactor, double pause) {
		super(g);
		this.pause = pause;
		this.growFactor = growFactor;
		new Thread(() -> apply(g)).start();
	}

	public void apply(Graph g) {
		for (int i = 0; i < 10; ++i) {
			g.vertices.add();
		}
		
		while (true) {
			var n = g.vertices.nbEntries();
			int nbVerticesToAdd = (int) (n * (growFactor-1));
			Cout.debug(n+ " vertices, adding " + nbVerticesToAdd + " new ones");
			for (int i = 0; i < nbVerticesToAdd; ++i) {
				var u = g.vertices.random();
				var v = g.vertices.add();
				g.edges.add(u, v);
			}
			Cout.debug("done");
			Threads.sleepMs((int) (pause * 1000));
		}
	}
}
