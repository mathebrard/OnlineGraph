package og;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HashGraph extends Graph {
	private Map<String, String> props = new HashMap<>();
	private final List<Change> changes = new ArrayList<>();

	public HashGraph() {
		super(new HashElementSet(), new HashElementSet(), new HashElementSet());
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

	@Override
	public void forEachChange(int since, Consumer<Change> c) {
		changes.subList(since, changes.size()).forEach(c);
	}
}
