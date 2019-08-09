package cloudoas.apimock.specstore.handler;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloudoas.apimock.common.http.ClientHelper;
import cloudoas.apimock.common.http.HandlerHelper;
import cloudoas.apimock.datafactory.ResponseDataFactory;
import cloudoas.apimock.datafactory.model.APIData;
import cloudoas.apimock.specstore.db.SpecDAO;
import cloudoas.apimock.specstore.model.SpecInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class SpecRegistrationHandler implements HttpHandler{
	private static final Logger logger = LoggerFactory.getLogger(SpecRegistrationHandler.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getRequestReceiver().receiveFullString((request, message) -> {
            if(StringUtils.isBlank(message)) {
            	HandlerHelper.handleBadRequest(request, "Request body is required.");
                return;
            }
            if(logger.isDebugEnabled()) {
            	logger.debug("Post method with message = " + message);
            }
            
            try {
				SpecInfo specInfo = objectMapper.readValue(message, SpecInfo.class);
				
				if (StringUtils.isBlank(specInfo.getFormat())) {
					HandlerHelper.handleBadRequest(request, "Spec format is required.");
					
					return;
				}
				
				String specContent = specInfo.getContent();
				
				if (StringUtils.isBlank(specContent) && StringUtils.isNotBlank(specInfo.getUrl())) {
					specContent = ClientHelper.download(new URI(specInfo.getUrl()));
				}
				
				if (StringUtils.isNotBlank(specContent)) {
					ResponseDataFactory dataFactory = new ResponseDataFactory();
					
					dataFactory.loadSpec(specContent, specInfo.getFormat());
					
					APIData mockData = dataFactory.makeData();
					
					SpecDAO.INSTANCE.addAPIData(mockData);
					
					HandlerHelper.handleSuccess(request, "Spec added.");
				}else {
					HandlerHelper.handleBadRequest(request, "Spec content cannot be retrieved.");
				}
				
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				HandlerHelper.handleRuntimeException(request, ex.getMessage());
			}
        });
	}
}
