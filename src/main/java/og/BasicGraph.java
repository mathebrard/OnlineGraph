package og;

import java.util.List;

import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class BasicGraph extends AbstractGraph {
	final Directory d, vertexDirectory, edgeDirectory;

	public BasicGraph(Directory d) {
		this.d = d;
		this.vertexDirectory = new Directory(d, "vertices");
		this.edgeDirectory = new Directory(d, "edges");
		d.ensureExists();
		vertexDirectory.ensureExists();
		edgeDirectory.ensureExists();
	}

	private RegularFile getVertexFile(String u) {
		return new RegularFile(d, u);
	}

	private RegularFile getEdgeFile(String from, String to) {
		return new RegularFile(d, from + " --- " + to);
	}

	@Override
	public long nbVertices() {
		return vertexDirectory.getNbFiles();
	}

	@Override
	public long nbEdges() {
		return edgeDirectory.getNbFiles();
	}

	@Override
	public void addVertex(String u) {
		getVertexFile(u).create();
	}

	@Override
	public void removeVertex(String u) {
		getVertexFile(u).delete();
	}

	@Override
	public void addEdge(String from, String to) {
		getEdgeFile(from, to).create();
	}

	@Override
	public void removeEdge(String from, String to) {
		getEdgeFile(from, to).delete();
	}

	@Override
	protected List<String> out(String v) {
		return null;
	}
}
