import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

import og.ElementSet;
import og.FlatOnDiskDiskGraph;
import og.FlatOnDiskElementSet;
import og.HashElementSet;
import og.OnDiskElementSet;
import toools.io.file.Directory;

public class AllTest {
	@org.junit.jupiter.api.Test
	public void main() {
		test(new FlatOnDiskElementSet(new Directory(Directory.getSystemTempDirectory(), "elementSet")));
		test(new HashElementSet());

		FlatOnDiskDiskGraph g = new FlatOnDiskDiskGraph(new Directory(Directory.getSystemTempDirectory(), "g"));
		System.out.println("graph is in " + g);

		if (g.exists()) {
			g.clear();
		} else {
			g.create();
		}

		for (int i = 0; i < 10; ++i) {
			g.vertices.add();
			System.out.println("add vertex " + i);
		}

		for (int i = 0; i < 10; ++i) {
			var from = g.vertices.random();
			var to = g.vertices.random();
			g.arcs.add(from, to);
			System.out.println("add edge " + i);
		}

		while (g.arcs.nbEntries() > 0) {
			System.out.println("remove edge from " + g.arcs.nbEntries());
			var e = g.arcs.random();
			g.arcs.remove(e);
		}
	}

	public void test(ElementSet s) {
		if (s instanceof OnDiskElementSet) {
			var sd = (OnDiskElementSet) s;
			sd.ensureExists();
		}

		s.clear();
		assertEquals(s.nbEntries(), 0);
		s.add(ThreadLocalRandom.current().nextLong());
		assertEquals(s.nbEntries(), 1);
		s.add(ThreadLocalRandom.current().nextLong());
		s.add(ThreadLocalRandom.current().nextLong());
		s.add(ThreadLocalRandom.current().nextLong());
		assertEquals(s.nbEntries(), 4);
		var e = s.random();
		s.remove(e);
		assertEquals(s.nbEntries(), 3);

		while (s.nbEntries() > 0) {
			s.remove(s.random());
		}

		assertEquals(s.nbEntries(), 0);

		for (int i = 0; i < 10; ++i) {
			s.add(i);
		}

		for (int i = 0; i < s.nbEntries(); ++i) {
			var r = s.random();
			assertEquals(s.contains(r), true);
		}

	}
}
