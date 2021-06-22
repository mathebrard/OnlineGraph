package og;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.LongConsumer;

public abstract class ElementSet {

	public abstract long nbEntries();

	public abstract void add(long id);

	public abstract Set<String> get(long id);

	public abstract void remove(long id);
	
	public abstract void clear();

	public abstract <E> E get(long id, String ext, Supplier<E> defaultValueSupplier);

	public abstract void set(long id, String ext, Object content);

	public abstract long random();

	public abstract void forEach(LongConsumer c);

	public <E> E alter(long id, String ext, Supplier<E> defaultValueSupplier, Consumer<E> c) {
		var o = get(id, ext, defaultValueSupplier);
		c.accept(o);
		set(id, ext, o);
		return o;
	}

	public abstract boolean contains(long r);
}
