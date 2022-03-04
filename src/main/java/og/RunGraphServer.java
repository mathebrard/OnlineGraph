package og;

import java.io.IOException;

import idawi.Component;
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;
import toools.thread.Threads;

public class RunGraphServer {
	public static void main(String[] args) throws IOException {
		var port = args.length == 0 ? 8082 : Integer.parseInt(args[0]);
		Component c = new Component();
		c.name = "og";
		c.lookup(ServiceManager.class).ensureStarted(GraphService.class);
		var gs = c.lookup(GraphService.class);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			gs.close();
		}));

		c.lookup(ServiceManager.class).ensureStarted(RESTService.class);

		var rest = c.lookup(RESTService.class);
		rest.startHTTPServer(port);
		System.out.println("URL: http://localhost:" + port + "/api/" + c.name);
		System.out.println("Website URL: http://localhost:" + port + "/web/og/display/ls.html");
		Threads.sleepForever();
	}
}
