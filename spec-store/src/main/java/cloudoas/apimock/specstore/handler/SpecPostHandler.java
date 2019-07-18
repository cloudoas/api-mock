package cloudoas.apimock.specstore.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloudoas.apimock.common.http.HandlerHelper;
import cloudoas.apimock.specstore.model.SpecInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class SpecPostHandler implements HttpHandler{
	private static final Logger logger = LoggerFactory.getLogger(SpecPostHandler.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getRequestReceiver().receiveFullString((e, message) -> {
            if(StringUtils.isBlank(message)) {
            	HandlerHelper.handleEmptyPostRequest(e);
                return;
            }
            if(logger.isDebugEnabled()) {
            	logger.debug("Post method with message = " + message);
            }
            
            try {
				SpecInfo specInfo = objectMapper.readValue(message, SpecInfo.class);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				HandlerHelper.handleException(e, ex.getMessage());
			}
        });
	}
}
