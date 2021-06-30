package og;

import java.awt.Color;
import java.util.Random;

import toools.gui.Utilities;

public abstract class ColorProperty extends Property {

	
	@Override
	public String toGraphviz(String value) {
		return value;
	}

	@Override
	public String random() {
		return Utilities.toRGBHex(new Color(new Random().nextInt()));
	}

	@Override
	public boolean accept(String value) {
		try {
			Utilities.parseColor(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
};