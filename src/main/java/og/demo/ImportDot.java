package og.demo;

import og.Client;

public class ImportDot {
	public static void main(String[] args) throws Exception {
		String dot = "digraph testgraph\n" + "{\n" + " xxx [k=v]\n" + " yyy [k1=v1 k2=v2]\n" + " a -> b\n"
				+ " n1 [label=\"Node 1\"]; n2 [label=\"Node 2\"]; \n"
				+ " n1 -> n2 [style=dotted label=\"A dotted edge\"]\n" + " n1 -> n4\n" + " n2 -> n3\n" + " n3 -> n5\n"
				+ " foo -> { bar baz } [ek=ev]\n" + " foo -> bar [fbk=fbv]\n" + " n4 -> n5\n" + " bar [kk=vv]\n"
				+ " bar [kkk=vvv]\n" + "}";
		var r = Client.post("http://localhost:8081/api/og/og.GraphStorageService/importDot/demo_graph,dot",
				dot.getBytes());
		System.out.println(r);
	}
}
