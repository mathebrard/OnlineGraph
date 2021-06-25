package og;

public abstract class Property {
	public abstract String getName();

	public abstract boolean accept(String value);

	public abstract String toGraphviz(String value);

	protected abstract String random();
}
