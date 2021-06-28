package og;

import java.util.Random;
import java.util.function.Consumer;

public class VertexProperties {

	enum arrowTypes {
		diamond, normal
	};

	enum shapes {
		point, circle, square, triangle, rectangle
	}

	public static Property shape = new Property() {

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
			return "shape";
		}

	};

	public static Property fillColor = new ColorProperty() {
		@Override
		public String getName() {
			return "fillColor";
		}
	};
	public static Property borderColor = new ColorProperty() {
		@Override
		public String getName() {
			return "borderColor";
		}
	};
	public static Property labelColor = new ColorProperty() {
		@Override
		public String getName() {
			return "labelColor";
		}
	};
	public static Property scale = new DoubleProperty(0.1, 2) {

		@Override
		public String getName() {
			return "scale";
		}

	};
	public static Property borderWidth = new IntProperty(10) {
		@Override
		public String getName() {
			return "borderWidth";
		}
	};
	public static Property label = new LabelProperty() {

		@Override
		public String getName() {
			return "label";
		}

	};
	public static Property hidden = new BooleanProperty() {

		@Override
		public String getName() {
			return "hidden";
		}

	};

	public static void forEach(Consumer<Property> p) {
		p.accept(borderColor);
		p.accept(borderWidth);
		p.accept(fillColor);
		p.accept(hidden);
		p.accept(label);
		p.accept(labelColor);
		p.accept(shape);
		p.accept(scale);
	}
}
