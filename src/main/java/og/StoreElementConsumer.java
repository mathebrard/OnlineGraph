package og;

import java.util.Collection;

public interface StoreElementConsumer {
	void accept(long u, Collection<String> props);
}