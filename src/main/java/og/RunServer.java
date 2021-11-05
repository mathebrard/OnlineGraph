package og;

import java.io.IOException;

import idawi.Component;
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;
import toools.thread.Threads;

public class RunServer {
	public static void main(String[] args) throws IOException {
		var port = args.length == 0 ? 8082 : Integer.parseInt(args[0]);
		Component c = new Component();
		c.friendlyName = "og";
		c.lookupService(ServiceManager.class).ensureStarted(GraphService.class);
		var gs = c.lookupService(GraphService.class);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			gs.close();
		}));

		c.lookupService(ServiceManager.class).ensureStarted(RESTService.class);

		var rest = c.lookupService(RESTService.class);
		rest.startHTTPServer(port);
		System.out.println("URL: http://localhost:" + port + "/api/" + c.friendlyName);
		System.out.println("Website URL: http://localhost:" + port + "/web/og/display/ls.html");
		Threads.sleepForever();
	}
}
