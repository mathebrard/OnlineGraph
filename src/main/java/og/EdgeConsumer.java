package og;

import java.util.Properties;

public interface EdgeConsumer {
	void accept(long e, long src, long dest, Properties p);
}