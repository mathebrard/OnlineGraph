package og.algo;

import it.unimi.dsi.fastutil.longs.LongList;
import og.Graph;

public class CC {
	public static double clusteringCoefficient(Graph g, long v) {
		var neighbors = (LongList) g.vertices.get(v, "outNeighbors", null);

		int count = 0;

		for (var n : neighbors) {
			for (var nn : (LongList) g.vertices.get(n, "outNeighbors", null)) {
				if (neighbors.contains(nn)) {
					++count;
				}
			}
		}

		double degree = neighbors.size();
		return count / (degree * (degree - 1));
	}

}
