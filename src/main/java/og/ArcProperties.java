package og;

import java.util.Random;
import java.util.function.Consumer;

public class ArcProperties {
	enum arrowShapes {
		none, normal, diamond;
	}

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

	public static void forEach(Consumer<Property> p) {
		EdgeProperties.forEach(p);
		p.accept(arrowShape);
		p.accept(arrowSize);
	}
}
