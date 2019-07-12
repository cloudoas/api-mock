package cloudoas.apimock.datafactory;


import java.io.File;

import org.junit.jupiter.api.Test;

import cloudoas.apimock.common.FileInfo;
import cloudoas.apimock.datafactory.model.APIData;

public class ResponseDataFactoryTest {

	@Test
	public void test() throws Exception {
		ResponseDataFactory f = new ResponseDataFactory();
		
		f.loadFile("./src/test/resources/petstore.yaml");
		
		APIData apiData = f.makeData();
		
		System.out.println(apiData.toString());
	}
	
	@Test
	public void testFile() {
		File f = new File("./src/test/resources/petstore.yaml");
		
		System.out.println(FileInfo.getName(f));
	}
}
