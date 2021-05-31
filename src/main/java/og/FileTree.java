package og;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import toools.io.file.Directory;
import toools.io.file.RegularFile;

public class FileTree {
	public final Directory d;

	public FileTree(Directory d) {
		this.d = d;
	}

	private RegularFile file(String name) {
		return new RegularFile(d, path(name));
	}

	private String path(String name) {
		return name;
	}

	long size() {
		return d.getNbFiles();
	}

	public RegularFile add(String name) {
		var f = file(name);
		f.create();
		return f;
	}

	public void remove(String name) {
		file(name).delete();
	}

	public byte[] getContent(String e) {
		return file(e).getContent();
	}

	public long random() {
		var files = d.javaFile.list();
		var f = files[ThreadLocalRandom.current().nextInt(files.length)];
		return Long.valueOf(f);
	}

	public void files(Consumer<RegularFile> fileConsumer) {
		d.listRegularFiles().forEach(f -> fileConsumer.accept(f));
	}
}
