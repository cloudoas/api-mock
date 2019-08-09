package cloudoas.apimock.specstore;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudoas.apimock.common.file.Configuration;
import cloudoas.apimock.specstore.handler.ResponseQueryHandler;
import cloudoas.apimock.specstore.handler.SpecRegistrationHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Methods;

public class SpecStoreServer {
	private static final Logger logger = LoggerFactory.getLogger(SpecStoreServer.class);
	private static final AtomicBoolean running = new AtomicBoolean(false);
	private static final Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
	
	private Undertow server = null;
	private final RoutingHandler routingHandler = Handlers.routing();
	

	public SpecStoreServer() {
		// default
	}
	
	public synchronized void start() {
		String host = config.getString(ConfigItems.SERVER_HOST, Defaults.SERVER_HOST);
		int port = config.getInt(ConfigItems.SERVER_PORT, Defaults.SERVER_PORT);
		
		routingHandler.add(Methods.POST, "/specs", new SpecRegistrationHandler());
		routingHandler.add(Methods.GET, "/response", new ResponseQueryHandler());
		
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(routingHandler)
                .build();		
		
		if (!running.get()) {
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
}
