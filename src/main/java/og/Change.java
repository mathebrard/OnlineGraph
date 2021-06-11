package og;

import java.io.Serializable;

import og.GraphStorageService.EdgeInfo;
import og.GraphStorageService.VertexInfo;

public class Change implements Serializable {
	public final long index;

	public Change(long index) {
		this.index = index;
	}

	public static class AddVertex extends Change {
		public final VertexInfo i;

		public AddVertex(long index, VertexInfo i) {
			super(index);
			this.i = i;
		}
	}

	public static class AddEdge extends Change {
		public final EdgeInfo i;

		public AddEdge(long index, EdgeInfo i) {
			super(index);
			this.i = i;
		}
	}

	public static class Remove extends Change {

		final long id;

		public Remove(long index, long id) {
			super(index);
			this.id = id;
		}
	}

	public static class RemoveVertex extends Remove {

		public RemoveVertex(long index, long id) {
			super(index, id);
		}
	}

	public static class RemoveEdge extends Remove {

		public RemoveEdge(long index, long id) {
			super(index, id);
		}
	}

	public static class ChangeProperties extends Change {

		final long id;

		public ChangeProperties(long index, long id) {
			super(index);
			this.id = id;
		}
	}

	public static class ChangeVertexProperties extends ChangeProperties {

		public ChangeVertexProperties(long index, long id) {
			super(index, id);
		}
	}

	public static class ChangeEdgesProperties extends ChangeProperties {

		public ChangeEdgesProperties(long index, long id) {
			super(index, id);
		}
	}
}
