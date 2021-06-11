package og.demo;

import og.Client;

public class ImportEdgeList {

	public static void main(String[] args) throws Exception {
		String g = "1 2\n1 3\n3 4\n4 2";
		var r = Client.post("http://localhost:8081/api/og/og.GraphStorageService/importEdges/test", g.getBytes());
		System.out.println(r);
	}
}
