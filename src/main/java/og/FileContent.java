package og;

import toools.io.ser.JavaSerializer;

public class FileContent {
	public final byte[] bytes;

	public FileContent(byte[] bytes) {
		this.bytes = bytes;
	}

	public Object toObject() {
		return JavaSerializer.instance.fromBytes(bytes);
	}
}
