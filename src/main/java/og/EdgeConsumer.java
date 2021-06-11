package og;

public interface EdgeConsumer<E> {
	void accept(long e, long src, long dest, E p);
}