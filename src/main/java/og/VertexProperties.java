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

		@Override
		public String getDefaultValue() {
			return shapes.circle.name();
		}

	};

	public static Property fillColor = new ColorProperty() {
		@Override
		public String getName() {
			return "fillColor";
		}

		@Override
		public String getDefaultValue() {
			return "white";
		}

	};

	public static Property scale = new DoubleProperty(0.1, 2) {

		@Override
		public String getName() {
			return "scale";
		}

		@Override
		public String getDefaultValue() {
			return "1";
		}

	};

	public static Property location = new Property() {
		double x, y, z;

		@Override
		public String toString() {
			return x + "," + y + "," + z;
		}

		@Override
		public String getName() {
			return "location";
		}

		@Override
		public String getDefaultValue() {
			return "0,0,0";
		}

		@Override
		public boolean accept(String value) {
			return value.matches(".*+,.*+,.*+");
		}

		@Override
		public String toGraphviz(String value) {
			return value;
		}

		@Override
		public String random() {
			return Math.random() + "," + Math.random() + "," + Math.random();
		}
	};

	public static void forEach(Consumer<Property> p) {
		ElementProperties.forEach(p);
		p.accept(fillColor);
		p.accept(shape);
		p.accept(scale);
		p.accept(location);
	}

}
