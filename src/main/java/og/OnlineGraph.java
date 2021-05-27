package og;
import idawi.Component;
import idawi.service.ServiceManager;
import idawi.service.rest.RESTService;

public class OnlineGraph {
	public static void main(String[] args) {
		Component c = new Component();
		c.lookupService(ServiceManager.class).ensureStarted(RESTService.class);
	}
}
