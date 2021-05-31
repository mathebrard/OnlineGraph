package og;

import java.util.Properties;

public interface VertexConsumer {
	void accept(long u, Properties p);
}