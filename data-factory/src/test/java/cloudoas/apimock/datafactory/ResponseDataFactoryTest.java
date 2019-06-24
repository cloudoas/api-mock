package cloudoas.apimock.datafactory;


import org.junit.jupiter.api.Test;

public class ResponseDataFactoryTest {

	@Test
	public void test() throws Exception {
		ResponseDataFactory f = new ResponseDataFactory();
		
		f.load("./src/test/resources/petstore.yaml");
		
		f.makeData();
		
	}
}
