package og;

import java.util.Random;

import toools.math.MathsUtilities;
import toools.text.TextUtilities;

public abstract class DoubleProperty extends Property {
	private final double min, max;

	public DoubleProperty(double min, double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String toGraphviz(String value) {
		return value;
	}

	@Override
	public String random() {
		return "" + MathsUtilities.pickRandomBetween(min, max, new Random());
	}

	@Override
	public boolean accept(String value) {
		return TextUtilities.isDouble(value);
	}

};