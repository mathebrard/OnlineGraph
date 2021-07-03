package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toools.io.Cout;

public class HashGraph extends Graph {
	private Map<String, String> props = new HashMap<>();
	private List<Change> changes = new ArrayList<>();

	public HashGraph() {
		super(new HashElementSet(), new HashElementSet(), new HashElementSet());
	}

	@Override
	public List<Change> allChanges() {
		return changes;
	}

	@Override
	public void commitNewChange(Change c) {
		changes.add(c);
	}

	@Override
	public Map<String, String> getProperties() {
		return this.props;
	}

	@Override
	public void setProperties(Map<String, String> m) {
		this.props = m;
	}

}
