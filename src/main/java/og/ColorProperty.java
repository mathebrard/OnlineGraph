package og;

import java.awt.Color;
import java.util.Random;

import toools.gui.Utilities;

public abstract class ColorProperty extends Property {

	
	@Override
	public String toGraphviz(String value) {
		var c = Color.decode(value);
		return "#" + Integer.toHexString(c.getRGB());
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