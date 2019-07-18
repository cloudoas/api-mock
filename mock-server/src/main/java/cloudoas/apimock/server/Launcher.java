package cloudoas.apimock.server;

import io.undertow.util.Methods;

public class Launcher {

	public static void main(String[] args) {
		MockServer server = new MockServer();
		server.start();
		
		server.addRoute(Methods.GET_STRING, "/", exchange -> exchange.getResponseSender().send("Hello API BOT!!!"));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				server.stop();
			}
		});
	}
}
