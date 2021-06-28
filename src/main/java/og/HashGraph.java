package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import toools.io.Cout;

public class HashGraph extends Graph {
	private Map<String, String> props = new HashMap<>();
	private List<Change> changes = new ArrayList<>();

	public HashGraph() {
		super(new HashElementSet(), new HashElementSet());
	}

	@Override
	public List<Change> getHistory() {
		return changes;
	}

	@Override
	public void addChange(Change c) {
//		Cout.debug(c);
		changes.add(c);
	}

	@Override
	public Map<String, String> getProperties() {
		return props;
	}

	@Override
	public void setProperties(Map<String, String> m) {
		this.props = m;
	}


}
