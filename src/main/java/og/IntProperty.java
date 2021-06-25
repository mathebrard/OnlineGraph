package og;

import java.util.Random;

import toools.text.TextUtilities;

public abstract class IntProperty extends Property {
	private final int max;

	public IntProperty(int max) {
		this.max = max;
	}

	@Override
	public String toGraphviz(String value) {
		return value;
	}

	@Override
	public String random() {
		return "" + new Random().nextInt(max);
	}

	@Override
	public boolean accept(String value) {
		return TextUtilities.isInt(value);
	}

};