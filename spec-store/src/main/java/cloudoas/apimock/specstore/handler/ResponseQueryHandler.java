package cloudoas.apimock.specstore.handler;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloudoas.apimock.common.http.HandlerHelper;
import cloudoas.apimock.common.http.Status;
import cloudoas.apimock.specstore.Defaults;
import cloudoas.apimock.specstore.db.SpecDAO;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class ResponseQueryHandler implements HttpHandler{
	private static final Logger logger = LoggerFactory.getLogger(ResponseQueryHandler.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Pattern NUMBERS = Pattern.compile("\\d+");

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		logger.info("ResponseQueryHandler ...");
		
        exchange.getRequestReceiver().receiveFullString((request, message) -> {
            if(StringUtils.isBlank(message)) {
            	HandlerHelper.handleBadRequest(request, "Request body is required.");
                return;
            }
            if(logger.isDebugEnabled()) {
            	logger.debug("Post method with message = " + message);
            }
            
            try {
            	MockRequest mockRequest = objectMapper.readValue(message, MockRequest.class);
				
        		if (null==mockRequest) {
        			HandlerHelper.handleBadRequest(request, "Invalid mock request.");
        			return;
        		}
        		
        		long specId = SpecDAO.INSTANCE.getSpecId(mockRequest.getSpecName(), mockRequest.getVersion());
        		
        		if (specId<0) {
        			HandlerHelper.handleBadRequest(request, "Unregistered spec.");
        			return;			
        		}
        		
                if(logger.isDebugEnabled()) {
                	logger.debug("specId = " + specId);
                }
        		
        		Collection<String> paths = SpecDAO.INSTANCE.findRequestPaths(specId);
        		
        		if (paths.isEmpty()) {
        			HandlerHelper.handleBadRequest(request, "No request path defined.");
        			return;			
        		}
        		
                if(logger.isDebugEnabled()) {
                	logger.debug("paths = " + paths);
                }
        		
        		String path = PathMatcher.match(paths, mockRequest.getRequestPath());
        		
        		if (null==path) {
        			HandlerHelper.handleBadRequest(request, "Undefined request path.");
        			return;				
        		}
        		
                if(logger.isDebugEnabled()) {
                	logger.debug("path = " + path);
                }
        		
        		long pathId = SpecDAO.INSTANCE.getRequestPathId(specId, path);
        		
                if(logger.isDebugEnabled()) {
                	logger.debug("pathId = " + pathId);
                }
        		
        		Map<String, String> responses = SpecDAO.INSTANCE.getResponses(specId, pathId, mockRequest.getRequestMethod());
        		
        		if (responses.isEmpty()) {
        			HandlerHelper.handleNotFound(request, "No responses defined.");
        			return;
        		}
        		
  
                if(logger.isDebugEnabled()) {
                	logger.debug("responses = " + responses);
                }        		
        		
        		MockResponse mockResponse = findResponse(responses, mockRequest);
        		
        		if (null!=mockResponse) {
        			sendMockResponse(request, mockResponse);
        		}else {
        			HandlerHelper.handleNotFound(request, "Cannot find mock response.");
        		}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				HandlerHelper.handleRuntimeException(request, ex.getMessage());
			}
        });
	}
	
	private MockResponse buildResponse(String key, String value) {
		String[] parts = key.split(Defaults.KEY_DELIMIETER);
		
		return new MockResponse(parts[1], parts[0], value);		
	}
	
	private MockResponse findResponse(Map<String, String> responses, MockRequest mockRequest) {
		if (responses.size()==1) {
			String key = responses.keySet().iterator().next();
			return buildResponse(key, responses.get(key));
		}
		
		String key = String.format("%s%s%s", 
				StringUtils.defaultIfBlank(mockRequest.getContentType(), Defaults.CONTENT_TYPE), 
				Defaults.KEY_DELIMIETER, 
				StringUtils.defaultIfBlank(mockRequest.getStatusCode(), Defaults.STATUS_CODE));
		
		
		String body = responses.get(key);
		
		
		return null==body?null:buildResponse(key, body);
	}
	
	private int getStatusCode(String code) {
		if (NUMBERS.matcher(code).matches()) {
			return Integer.parseInt(code);
		}
		
		return 200;
	}
	
	private void sendMockResponse(HttpServerExchange exchange, MockResponse response) {
    	Status status = new Status(getStatusCode(response.getStatusCode()), response.getResponseBody());
    	
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), StringUtils.defaultString(response.getContentType(), Defaults.CONTENT_TYPE));
        exchange.setStatusCode(status.getCode());
		exchange.getResponseSender().send(status.toString());
		exchange.endExchange();
	}
}
