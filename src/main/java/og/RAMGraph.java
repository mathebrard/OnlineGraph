package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RAMGraph extends Graph {
	private Map<String, String> props = new HashMap<>();
	private List<Change> changes = new ArrayList<>();

	public RAMGraph() {
		super(new RAMElementSet(), new RAMElementSet());
	}

	@Override
	public List<Change> getHistory() {
		return changes;
	}

	@Override
	public void addChange(Change c) {
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
