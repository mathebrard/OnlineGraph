package og;

import java.util.Random;

import og.VertexProperties.shapes;

public class EdgeProperties {
	enum arrowShapes {
		none, normal, diamond;
	}
	
	public static Property arrowSize = new IntProperty(40) {

		@Override
		public String getName() {
			return "arrowSize";
		}

	};
	public static Property width = new IntProperty(10) {

		@Override
		public String getName() {
			return "width";
		}

	};
	public static Property label = new LabelProperty() {

		@Override
		public String getName() {
			return "label";
		}

	};
	public static Property color = new ColorProperty() {

		@Override
		public String getName() {
			return "color";
		}
	};

	public static Property directed = new BooleanProperty() {

		@Override
		public String getName() {
			return "directed";
		}
	};

	enum styles {
		solid, dashed
	}

	public static Property style = new Property() {

		@Override
		public String toGraphviz(String value) {
			return value;
		}

		@Override
		public String random() {
			return shapes.values()[new Random().nextInt(shapes.values().length)].name();
		}

		@Override
		public boolean accept(String value) {
			return shapes.valueOf(value) != null;
		}

		@Override
		public String getName() {
			return "style";
		}
	};



	public static Property arrowShape = new Property() {

		@Override
		public String toGraphviz(String value) {
			return value;
		}

		@Override
		public String random() {
			return arrowShapes.values()[new Random().nextInt(arrowShapes.values().length)].name();
		}

		@Override
		public boolean accept(String value) {
			return arrowShapes.valueOf(value) != null;
		}

		@Override
		public String getName() {
			return "arrowShape";
		}
	};
}
