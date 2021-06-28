package og;

import java.io.IOException;

import idawi.Component;
import idawi.ComponentDescriptor;
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;
import toools.thread.Threads;

public class RunServer {
	public static void main(String[] args) throws IOException {
		var descriptor = new ComponentDescriptor();
		descriptor.friendlyName = "og";
		Component c = new Component(descriptor);
		c.lookupService(ServiceManager.class).ensureStarted(GraphService.class);
		c.lookupService(ServiceManager.class).ensureStarted(RESTService.class);
		var rest = c.lookupService(RESTService.class);
		int port = RESTService.DEFAULT_PORT;
		rest.startHTTPServer(port);
		System.out.println("URL: http://localhost:" + port + "/api/" + c.friendlyName);
		Threads.sleepForever();
	}
}
