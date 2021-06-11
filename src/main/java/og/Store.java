package og;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.LongConsumer;

public abstract class Store {

	public abstract long nbEntries();

	public abstract void add(long id);

	public abstract List<String> get(long id);

	public abstract void remove(long id);

	public abstract <E> E get(long id, String ext, Supplier<E> defaultValueSupplier);

	public abstract void set(long id, String ext, Object content);

	public abstract long random();

	public abstract void files(LongConsumer c);

	public <E> E alter(long id, String ext, Supplier<E> defaultValueSupplier, Consumer<E> c) {
		var o = get(id, ext, defaultValueSupplier);
		c.accept(o);
		set(id, ext, o);
		return o;
	}

}
