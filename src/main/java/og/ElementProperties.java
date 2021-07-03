package og;

import java.util.Random;
import java.util.function.Consumer;

public class ElementProperties {


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
	
	public static Property labelColor = new ColorProperty() {
		@Override
		public String getName() {
			return "labelColor";
		}

		@Override
		public String getDefaultValue() {
			return "black";
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

	public static void forEach(Consumer<Property> p) {
		p.accept(color);
		p.accept(width);
		p.accept(label);
		p.accept(labelColor);
	}
	

}
