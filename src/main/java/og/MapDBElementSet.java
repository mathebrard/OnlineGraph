package og;

import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import toools.io.file.Directory;
//https://stackoverflow.com/questions/1536953/recommend-a-fast-scalable-persistent-map-java

public class MapDBElementSet {

	static MapElementSet f(Directory d) {
		DB db = DBMaker.fileDB(d.javaFile).make();
		var m = (Map<Long, Map<String, Object>>) db.hashMap("map").createOrOpen();
		return new MapElementSet(m);
	}
}
