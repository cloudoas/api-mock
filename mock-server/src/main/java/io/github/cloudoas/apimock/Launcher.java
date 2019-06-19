package io.github.cloudoas.apimock;

import io.undertow.util.Methods;

public class Launcher {

	public static void main(String[] args) {
		APIServer server = new APIServer();
		server.start();
		
		server.addRoute(Methods.GET_STRING, "/", exchange -> exchange.getResponseSender().send("Hello API BOT!!!"));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				server.stop();
			}
		});
	}
}
