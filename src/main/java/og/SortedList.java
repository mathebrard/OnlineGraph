package og;

import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;

public class SortedList extends AbstractLongSet {

	private LongArrayList l = new LongArrayList();

	@Override
	public LongIterator iterator() {
		return l.iterator();
	}

	@Override
	public int size() {
		return l.size();
	}

	public int indexOf(long l) {
		return -1;
	}

	@Override
	public boolean remove(long k) {
		int i = indexOf(k);

		// if not in
		if (i == -1) {
			return false;
		} else {
			l.removeLong(i);
			return true;
		}
	}

	@Override
	public boolean add(long k) {
		int i = indexOf(k);

		// if already in
		if (l.getLong(i) == k) {
			return false;
		} else {
			l.add(i, k);
			return true;
		}
	}

	@Override
	public boolean contains(long k) {
		return indexOf(k) != -1;
	}

	@Override
	public void clear() {
		l.clear();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		SortedList clone = new SortedList();
		clone.l = l.clone();
		return clone;
	}

}
