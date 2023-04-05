package og;

import java.io.IOException;

import idawi.Component;
import idawi.service.DemoService;
import idawi.service.ServiceManager;
import idawi.service.web.WebService;
import toools.thread.Threads;

public class RunGraphServer {
	public static void main(String[] args) throws IOException {
		var port = args.length == 0 ? 8081 : Integer.parseInt(args[0]);
		Component c = new Component("gw");
		c.lookup(ServiceManager.class).ensureStarted(GraphService.class);
		c.lookup(ServiceManager.class).ensureStarted(DemoService.class);
		var gs = c.lookup(GraphService.class);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			gs.close();
		}));

		c.lookup(ServiceManager.class).ensureStarted(WebService.class);

		var rest = c.lookup(WebService.class);
		rest.startHTTPServer(port);
		System.out.println("URL: http://localhost:" + port + "/api/" + c);
		System.out.println("Website URL: http://localhost:" + port + "/frontend/og/display/ls.html");
		Threads.sleepForever();
	}
}
