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

		final long elementID;

		public Remove(long id) {
			this.elementID = id;
		}
	}

	public static class RemoveVertex extends Remove {

		public RemoveVertex(long vertexID) {
			super(vertexID);
		}
	}

	public static class RemoveEdge extends Remove {

		public RemoveEdge(long edgeID) {
			super(edgeID);
		}
	}

	public static class PropertiesChange extends Change {

		final public long id;

		public PropertiesChange(long id) {
			this.id = id;
		}
	}

	public static class VertexPropertiesChange extends PropertiesChange {

		public VertexPropertiesChange(long id) {
			super(id);
		}
	}

	public static class EdgePropertiesChange extends PropertiesChange {

		public EdgePropertiesChange(long id) {
			super(id);
		}
	}
}
