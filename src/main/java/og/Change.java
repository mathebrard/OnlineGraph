package og;

import java.io.Serializable;

import og.GraphService.ArcInfo;
import og.GraphService.EdgeInfo;
import og.GraphService.VertexInfo;
import toools.util.Date;

public class Change implements Serializable {
	public static class Clear extends Change {

	}

	public final double date = Date.time();
	public long index;
	public final String type;

	public Change() {
		var s = getClass().getName();
		this.type = s.substring(s.lastIndexOf("$") + 1);
	}

	@Override
	public String toString() {
		return type;
	}

	public static class AddVertex extends Change {
		public final VertexInfo vertexInfo;

		public AddVertex(VertexInfo i) {
			this.vertexInfo = i;
		}

		@Override
		public String toString() {
			return super.toString() + ", vertex=" + vertexInfo.id;
		}
	}

	public static class AddArc extends Change {
		public final ArcInfo edgeInfo;

		public AddArc(ArcInfo i) {
			this.edgeInfo = i;
		}

		@Override
		public String toString() {
			return super.toString() + ", arc=" + edgeInfo.id;
		}
	}

	public static class AddEdge extends Change {
		public final EdgeInfo edgeInfo;

		public AddEdge(EdgeInfo i) {
			this.edgeInfo = i;
		}

		@Override
		public String toString() {
			return super.toString() + ", edge=" + edgeInfo.id;
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

		@Override
		public String toString() {
			return super.toString() + ", vertex=" + elementID;
		}
	}

	public static class RemoveArc extends Remove {

		public RemoveArc(long edgeID) {
			super(edgeID);
		}

		@Override
		public String toString() {
			return super.toString() + ", arc=" + elementID;
		}
	}

	public static class RemoveEdge extends Remove {

		public RemoveEdge(long edgeID) {
			super(edgeID);
		}

		@Override
		public String toString() {
			return super.toString() + ", edge=" + elementID;
		}
	}

	public static class DataChange extends Change {

		final public long id;
		final public String name;

		public DataChange(long id, String key) {
			this.id = id;
			this.name = key;
		}

	}

	public static class VertexDataChange extends DataChange {

		public VertexDataChange(long u, String key) {
			super(u, key);
		}

		@Override
		public String toString() {
			return super.toString() + ", vertex=" + id;
		}
	}

	public static class ArcDataChange extends DataChange {

		public ArcDataChange(long id, String key) {
			super(id, key);
		}

		@Override
		public String toString() {
			return super.toString() + ", arc=" + id;
		}
	}

	public static class EdgeDataChange extends DataChange {

		public EdgeDataChange(long id, String key) {
			super(id, key);
		}

		@Override
		public String toString() {
			return super.toString() + ", edge=" + id;
		}
	}

	public static class GraphPropertiesChange extends Change {

	}

}
