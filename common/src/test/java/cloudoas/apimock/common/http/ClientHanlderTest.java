package cloudoas.apimock.common.http;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class ClientHanlderTest {
	@Test
	public void test_download() throws Exception {
		String content = ClientHelper.download(new URI("https://cloudoas.github.io/index.html"));
		
		assertTrue(StringUtils.isNotBlank(content));
	}
	
	@Test
	public void test_url() throws Exception {
        URL u = new URL("https://cloudoas.github.io/");
        
       try (InputStream in = u.openStream()){
           String result = new BufferedReader(new InputStreamReader(in)).lines()
        		   .parallel().collect(Collectors.joining("\n"));
           
           assertTrue(StringUtils.isNotBlank(result));
    	   
       }catch (Exception e) {
    	   e.printStackTrace();
       }
	}
}
