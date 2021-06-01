package og;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Client {

	public static byte[] get(String uri) throws Exception {
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();

		HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
		System.out.println(response.statusCode());
		var body = response.body();
		System.out.println("body: " + body);
		return (byte[]) body;
	}

	public static byte[] post(String uri) throws Exception {
		return post(uri, new byte[0]);
	}

	public static byte[] post(String uri, byte[] data) throws Exception {
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).POST(BodyPublishers.ofByteArray(data))
				.build();

		HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
		System.out.println(response.statusCode());
		var body = response.body();
		System.out.println("body: " + body);
		return (byte[]) body;
	}

	public static void main(String[] args) throws Exception {
		String dot = "digraph testgraph\n" + "{\n" + " xxx [k=v]\n" + " yyy [k1=v1 k2=v2]\n" + " a -> b\n"
				+ " n1 [label=\"Node 1\"]; n2 [label=\"Node 2\"]; \n"
				+ " n1 -> n2 [style=dotted label=\"A dotted edge\"]\n" + " n1 -> n4\n" + " n2 -> n3\n" + " n3 -> n5\n"
				+ " foo -> { bar baz } [ek=ev]\n" + " foo -> bar [fbk=fbv]\n" + " n4 -> n5\n" + " bar [kk=vv]\n"
				+ " bar [kkk=vvv]\n" + "}";
		post("http://localhost:8081/api/og/og.GraphStorageService/importDot/demo_graph,dot", dot.getBytes());
	}
}
