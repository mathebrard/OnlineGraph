package og;

public abstract class Property {
	public abstract String getName();

	public abstract boolean accept(String value);

	public abstract String toGraphviz(String value);

	public abstract String random();

	public abstract String getDefaultValue();
}
