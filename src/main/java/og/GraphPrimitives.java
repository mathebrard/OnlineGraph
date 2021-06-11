package og;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongList;

public interface GraphPrimitives<V, E> {
	// creates a new vertex in the graph, automatically assigning an ID to it
	long addVertex();

	// removes the given vertex from the graph
	void removeVertex(long u);

	long addEdge(long from, long to);

	LongList inEdges(long v);

	LongList outEdges(long v);

	void removeEdge(long e);

	long nbVertices();

	long nbEdges();

	long source(long e);

	long destination(long e);

	long pickRandomVertex();

	long pickRandomEdge();

	void traverseEdges(LongConsumer e);

	void traverseVertices(LongConsumer v);

	void create();

	void clear();

	boolean exists();
}
