package og;

import java.util.List;

public abstract class AbstractGraph {
	public abstract void addVertex(String u);

	public abstract void removeVertex(String u);

	public abstract void addEdge(String from, String to);

	public abstract void removeEdge(String from, String to);

	public abstract long nbVertices();
	

	public abstract long nbEdges();

	protected abstract List<String> out(String v);
}
