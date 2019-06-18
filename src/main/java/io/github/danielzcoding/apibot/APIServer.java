package io.github.danielzcoding.apibot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

public class APIServer {
	private static final Logger logger = LoggerFactory.getLogger(APIServer.class);
	private static final AtomicBoolean running = new AtomicBoolean(false);
	
	private Undertow server = null;
	private final RoutingHandler routingHandler = Handlers.routing();
	

	public APIServer() {
		// default
	}
	
	public synchronized void start() {
		if (!running.get()) {
			String host = Configuration.getString(Configuration.SERVER_HOST, Constants.DEFAULT_HOST);
			int port = Configuration.getInt(Configuration.SERVER_PORT, Constants.DEFAULT_PORT);
			
	        server = Undertow.builder()
	                .addHttpListener(port, host)
	                .setHandler(routingHandler)
	                .build();
	        server.start();	
	        running.set(true);
	        
	        logger.info("Listening at {}:{}", host, port);				
		}else {
			logger.error("The server is running.");
		}
	}
	
	public synchronized void stop() {
		if (running.get()) {
			server.stop();
			running.set(false);
		}
	}
	
	public void addRoute(final String method, final String template, HttpHandler handler) {
		routingHandler.add(method, template, handler);
	}
}
