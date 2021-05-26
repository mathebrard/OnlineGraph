import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class Graph {

	final Directory d;

	public static final Directory baseDirectory = new Directory("$HOME/graph-server");

	static {
		baseDirectory.ensureExists();
	}

	public Graph(Object id) {
		d = new Directory(baseDirectory, id.toString());
	}

	void addVertex(String u) {
		RegularFile vf = new RegularFile(d, u);
		vf.create();
	}

	void removeVertex(String u) {
		RegularFile vf = new RegularFile(d, u);
		vf.delete();
	}

	void addEdge(String from, String to) {
		RegularFile f = new RegularFile(d, from + " --- " + to);
		f.create();
	}

	void removeEdge(String from, String to) {
		RegularFile f = new RegularFile(d, from + " --- " + to);
		f.delete();
	}

}
