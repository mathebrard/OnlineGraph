package og;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongList;

public interface GraphPrimitives {
	// creates a new vertex in the graph, automatically assigning an ID to it
	long addVertex(long u);
	
	public default long addVertex() {
		long u = ThreadLocalRandom.current().nextLong();
		addVertex(u);
		return u;
	}


	// removes the given vertex from the graph
	void removeVertex(long u);

	long addEdge(long from, long to);

	LongList inEdges(long v);

	LongList outEdges(long v);

	void removeEdge(long e);

	long nbVertices();

	long nbEdges();

	long source(long e);

	long destination(long
	 e);

	long pickRandomVertex();

	long pickRandomEdge();

	void traverseEdges(LongConsumer e);

	void traverseVertices(LongConsumer v);

	void clear();

	List<Change> getHistory();

	void addChange(Change c);

	Map<String, String> getProperties();

	void setProperties(Map<String, String> m);
}
