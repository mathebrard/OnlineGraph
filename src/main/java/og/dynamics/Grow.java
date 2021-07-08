package og.dynamics;

import og.Graph;
import og.GraphDynamics;
import toools.thread.Threads;

public class Grow extends GraphDynamics {
	private double pause;

	public Grow(Graph g, double pause) {
		super(g);
		this.pause = pause;
		new Thread(() -> apply(g)).start();
	}

	public void apply(Graph g) {
		g.vertices.add();
		
		while (true) {
			var u = g.vertices.random();
			var v = g.vertices.add();
			g.edges.add(u, v);
			Threads.sleepMs((int) (pause * 1000));
		}
	}
}
