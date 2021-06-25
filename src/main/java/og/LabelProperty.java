package og;

import java.util.Random;

import toools.text.TextUtilities;

public abstract class LabelProperty extends Property {

	@Override
	public String toGraphviz(String value) {
		return value;
	}

	@Override
	public String random() {
		return TextUtilities.generateRandomString("abczedfgijklmnopqrstuvwxyz", 5, new Random());
	}

	@Override
	public boolean accept(String value) {
		return true;
	}

};