package cloudoas.apimock.common.http;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.ChannelListeners;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.ssl.XnioSsl;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.UndertowClient;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StringReadChannelListener;
import io.undertow.util.StringWriteChannelListener;

public class ClientHelper {
	private static final Logger logger = LoggerFactory.getLogger(ClientHelper.class);
    private static final OptionMap DEFAULT_OPTIONS;

    private static final AttachmentKey<String> RESPONSE_BODY = AttachmentKey.create(String.class);
    private static final ByteBufferPool BUFFER_POOL = new DefaultByteBufferPool(true, 24 * 1024);
    private static final String HTTPS="https";

    static {
        OptionMap.Builder builder = OptionMap.builder()
                .set(Options.WORKER_IO_THREADS, 8)
                .set(Options.TCP_NODELAY, true)
                .set(Options.KEEP_ALIVE, true)
                .set(Options.WORKER_NAME, "Client");

        DEFAULT_OPTIONS = builder.getMap();
    }
    
    public static boolean isSecure(URI uri) {
    	return HTTPS.equalsIgnoreCase(uri.getScheme());
    }
    
    public static String request(URI uri, HttpString method, String requestBody) {
    	SSLContext defaultSSLContext = null;
    	boolean secure = isSecure(uri);
    	
    	try {
			defaultSSLContext= secure?SSLContext.getDefault():null;
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
    	
    	return request(uri, secure, method, defaultSSLContext, requestBody);   	
    }
    
    public static String download(URI uri) {
    	SSLContext defaultSSLContext = null;
    	boolean secure = isSecure(uri);
    	
    	try {
			defaultSSLContext= secure?SSLContext.getDefault():null;
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
    	
    	return request(uri, secure, Methods.GET, defaultSSLContext, null);
    }
    
    public static String request(URI uri, boolean secure, HttpString method, SSLContext sslContext, String requestBody) {
        final UndertowClient client = UndertowClient.getInstance();
        
        Xnio xnio = Xnio.getInstance();
        XnioWorker xnioWorker = null;
		try {
			xnioWorker = xnio.createWorker(null, DEFAULT_OPTIONS);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        
		if (null==xnioWorker) {
			return null;
		}
        
        XnioSsl ssl = secure?new UndertowXnioSsl(xnioWorker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, sslContext):null;
        
        AtomicReference<ClientResponse> reference = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        URI targetURI = null;
        
        try {
        	targetURI = new URI(String.format("%s://%s:%d", uri.getScheme(), uri.getHost(), uri.getPort()>0?uri.getPort():8080));
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
        
        try (ClientConnection connection = client.connect(targetURI, xnioWorker, ssl, BUFFER_POOL, OptionMap.EMPTY).get()){
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                        ClientRequest request = new ClientRequest().setMethod(method).setPath(uri.getPath());
                        request.getRequestHeaders().put(Headers.HOST, uri.getHost());
                        
                        if (StringUtils.isNotBlank(requestBody)) {
                            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");
                        }
                        
                        connection.sendRequest(request, createClientCallback(reference, latch, requestBody));
                    }

            });

            latch.await(30, TimeUnit.SECONDS);

            final ClientResponse response = reference.get();
            
            if (null==response) {
            	logger.error("Failed to receive response.");
            	
            	return null;
            }
            
            if (response.getResponseCode()>=400) {
            	logger.error("Failed to retrieve contents." + response.getStatus());
            	
            	return null;         	
            }
            
            return response.getAttachment(RESPONSE_BODY);

        } catch(Exception e){
        	logger.error(e.getMessage(), e);
        } 	
        
        return null;
    }
    
    private static ClientCallback<ClientExchange> createClientCallback(final AtomicReference<ClientResponse> responseRef, final CountDownLatch latch, final String requestBody) {
        return new ClientCallback<ClientExchange>() {
            @Override
            public void completed(ClientExchange result) {
            	if (StringUtils.isNotBlank(requestBody)) {
            		new StringWriteChannelListener(requestBody).setup(result.getRequestChannel());
            	}
            	
                result.setResponseListener(new ClientCallback<ClientExchange>() {
                    @Override
                    public void completed(final ClientExchange result) {
                    	responseRef.set(result.getResponse());
                        new StringReadChannelListener(result.getConnection().getBufferPool()) {

                            @Override
                            protected void stringDone(String string) {
                                result.getResponse().putAttachment(RESPONSE_BODY, string);
                                latch.countDown();
                            }

                            @Override
                            protected void error(IOException e) {
                            	logger.error(e.getMessage(), e);

                                latch.countDown();
                            }
                        }.setup(result.getResponseChannel());
                    }

                    @Override
                    public void failed(IOException e) {
                    	logger.error(e.getMessage(), e);

                        latch.countDown();
                    }
                });
                try {
                    result.getRequestChannel().shutdownWrites();
                    if(!result.getRequestChannel().flush()) {
                        result.getRequestChannel().getWriteSetter().set(ChannelListeners.<StreamSinkChannel>flushingChannelListener(null, null));
                        result.getRequestChannel().resumeWrites();
                    }
                } catch (IOException e) {
                	logger.error(e.getMessage(), e);
                    latch.countDown();
                }
            }

            @Override
            public void failed(IOException e) {
            	logger.error(e.getMessage(), e);
                latch.countDown();
            }
        };
    }
}
