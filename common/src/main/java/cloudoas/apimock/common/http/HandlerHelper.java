package cloudoas.apimock.common.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class HandlerHelper {
    public static void handleSuccess(HttpServerExchange exchange, String message) {
    	Status status = new Status(200, message);
    	
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.setStatusCode(status.getCode());
		exchange.getResponseSender().send(status.toString());
    }
    
    public static void handleBadRequest(HttpServerExchange exchange, String message) {
    	Status status = new Status(400, message);
    	
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.setStatusCode(status.getCode());
		exchange.getResponseSender().send(status.toString());
    }
    
    public static void handleRuntimeException(HttpServerExchange exchange, String message) {
    	Status status = new Status(500, message);
    	
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.setStatusCode(status.getCode());
		exchange.getResponseSender().send(status.toString());
    }
    

}
