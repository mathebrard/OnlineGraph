package og;

import java.util.Random;
import java.util.function.Consumer;

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

		@Override
		public String getDefaultValue() {
			return "20";
		}
	};
	public static Property width = new IntProperty(10) {

		@Override
		public String getName() {
			return "width";
		}

		@Override
		public String getDefaultValue() {
			return "1";
		}
	};
	public static Property label = new LabelProperty() {

		@Override
		public String getName() {
			return "label";
		}

		@Override
		public String getDefaultValue() {
			return "";
		}
	};
	public static Property color = new ColorProperty() {

		@Override
		public String getName() {
			return "color";
		}

		@Override
		public String getDefaultValue() {
			return "black";
		}
	};

	public static Property directed = new BooleanProperty() {

		@Override
		public String getName() {
			return "directed";
		}

		@Override
		public String getDefaultValue() {
			return "true";
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
			return styles.values()[new Random().nextInt(styles.values().length)].name();
		}

		@Override
		public boolean accept(String value) {
			return styles.valueOf(value) != null;
		}

		@Override
		public String getName() {
			return "style";
		}

		@Override
		public String getDefaultValue() {
			return styles.solid.name();
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

		@Override
		public String getDefaultValue() {
			return arrowShapes.normal.name();
		}
	};

	public static void forEach(Consumer<Property> p) {
		p.accept(arrowShape);
		p.accept(arrowSize);
		p.accept(color);
		p.accept(directed);
		p.accept(label);
		p.accept(style);
		p.accept(width);
	}
}
