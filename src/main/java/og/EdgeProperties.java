package og;

import java.util.Random;
import java.util.function.Consumer;

public class EdgeProperties {

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

	public static void forEach(Consumer<Property> p) {
		ElementProperties.forEach(p);
		p.accept(style);
	}
}
