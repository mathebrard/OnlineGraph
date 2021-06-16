package og;

import java.io.Serializable;

import og.GraphStorageService.EdgeInfo;
import og.GraphStorageService.VertexInfo;
import toools.util.Date;

public class Change implements Serializable {
	public final double date = Date.time();
	public final String type;

	public Change() {
		var s = getClass().getName();
		this.type = s.substring(s.lastIndexOf("$") + 1);
	}

	public static class AddVertex extends Change {
		public final VertexInfo vertexInfo;

		public AddVertex(VertexInfo i) {
			this.vertexInfo = i;
		}
	}

	public static class AddEdge extends Change {
		public final EdgeInfo edgeInfo;

		public AddEdge(EdgeInfo i) {
			this.edgeInfo = i;
		}
	}

	public static class Remove extends Change {

		final long edgeID;

		public Remove(long id) {
			this.edgeID = id;
		}
	}

	public static class RemoveVertex extends Remove {

		public RemoveVertex(long id) {
			super(id);
		}
	}

	public static class RemoveEdge extends Remove {

		public RemoveEdge(long id) {
			super(id);
		}
	}

	public static class ChangeProperties extends Change {

		final public long id;

		public ChangeProperties(long id) {
			this.id = id;
		}
	}

	public static class ChangeVertexProperties extends ChangeProperties {

		public ChangeVertexProperties(long id) {
			super(id);
		}
	}

	public static class ChangeEdgesProperties extends ChangeProperties {

		public ChangeEdgesProperties(long id) {
			super(id);
		}
	}
}
