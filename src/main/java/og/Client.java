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
		System.out.println("response body: " + body);
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
		System.out.println("response body: " + body);
		return (byte[]) body;
	}


}
