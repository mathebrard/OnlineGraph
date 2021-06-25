package og;

public abstract class BooleanProperty extends Property {

	@Override
	public String toGraphviz(String value) {
		return value;
	}

	@Override
	public String random() {
		return "" + (Math.random() < 0.5);
	}

	@Override
	public boolean accept(String value) {
		return value.matches("true|false");
	}
};