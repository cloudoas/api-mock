package cloudoas.apimock.datafactory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import cloudoas.apimock.common.file.FileInfo;
import cloudoas.apimock.datafactory.model.APIData;

public class ResponseDataFactoryTest {
	private static final Logger logger = Logger.getLogger(ResponseDataFactoryTest.class);

	@Test
	public void test() throws Exception {
		ResponseDataFactory f = new ResponseDataFactory();
		
		f.loadSpec(new File("./src/test/resources/petstore.yaml"));
		
		APIData apiData = f.makeData();
		
		String dataStr = apiData.toString();
		
		if (logger.isDebugEnabled()) {
			logger.debug(dataStr);
		}
		
		assertTrue(StringUtils.isNotBlank(dataStr));
	}
	
	@Test
	public void testFile() {
		File f = new File("./src/test/resources/petstore.yaml");
		
		assertEquals("petstore", FileInfo.getName(f));
	}
}
