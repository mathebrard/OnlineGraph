package og;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public abstract class ElementSet {

	public abstract long nbEntries();

	public abstract long random();

	public long add() {
		long u = ThreadLocalRandom.current().nextLong();
		add(u);
		return u;
	}

	public abstract void add(long id);

	public abstract boolean contains(long id);

	public abstract Set<String> getKeys(long id);

	public abstract void forEach(Long2BooleanFunction c);

	public abstract void remove(long id);

	public abstract void clear();

	public abstract <E> E get(long id, String key, Supplier<E> defaultValueSupplier);

	public abstract void set(long id, String key, Object content);

	public <E> E alter(long id, String key, Supplier<E> defaultValueSupplier, Consumer<E> modificationCode) {
		var data = get(id, key, defaultValueSupplier);
		modificationCode.accept(data);
		set(id, key, data);
		return data;
	}

	public LongList find(int nbExpected, Long2BooleanFunction... conditions) {
		LongList l = new LongArrayList();

		for (var condition : conditions) {
			forEach(u -> {
				if (condition.get(u)) {
					l.add(u);

					if (l.size() == nbExpected) {
						return false;
					}
				}

				return true;
			});

			if (l.size() == nbExpected) {
				break;
			}
		}

		return l;
	}
}
