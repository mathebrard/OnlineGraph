package og;

import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import toools.io.file.Directory;
//https://stackoverflow.com/questions/1536953/recommend-a-fast-scalable-persistent-map-java

public class MapDBElementSet extends Hash2ElementSet {

	MapDBElementSet(Directory d) {
		super((Map<Long, Set<String>>) createMapDBMap(new Directory(d, "m")),
				(Map<String, Object>) createMapDBMap(new Directory(d, "m2")));
	}

	public static Map createMapDBMap(Directory d) {
		d.ensureExists();
		DB db = DBMaker.fileDB(d.javaFile).make();
		return db.hashMap("map").createOrOpen();
	}
}
