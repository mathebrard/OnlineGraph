package og;

import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import toools.io.file.Directory;
import toools.io.file.RegularFile;
//https://stackoverflow.com/questions/1536953/recommend-a-fast-scalable-persistent-map-java

public class MapDBElementSet extends Hash2ElementSet {

	public MapDBElementSet(Directory d) {
		super((Map<Long, Set<String>>) createOrOpenDBMap(new RegularFile(d, "m")),
				(Map<String, Object>) createOrOpenDBMap(new RegularFile(d, "m2")));
	}

	public static Map createOrOpenDBMap(RegularFile d) {
		d.getParent().ensureExists();
		DB db = DBMaker.fileDB(d.javaFile).make();
		return db.hashMap("map").createOrOpen();
	}

	public void cleanClose() {
		// TODO Auto-generated method stub

	}
}
