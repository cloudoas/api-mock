package cloudoas.apimock.specstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cloudoas.apimock.common.http.ClientHelper;
import cloudoas.apimock.specstore.db.DBManager;
import io.undertow.util.Methods;

public class SpecStoreTest {
	
	@BeforeAll
	public static void setup() {
		Launcher.main(null);
	}
	
	@Test
	public void test_db_ready() throws SQLException {
		//check("select name from spec_tbl", null);
		//check("select path from reqpath_tbl", null);
		//check("select content_type from contenttype_tbl", null);
		check("select count(*) from spec_tbl", 1);
	}
	
	@Test
	public void test_response_handler() throws Exception {
		String mockRequest = "{\n" + 
				"\"specName\": \"swagger petstore\",\n" + 
				"\"version\": \"1.0.0\",\n" + 
				"\"requestMethod\": \"get\",\n" + 
				"\"requestPath\": \"/pets\"\n" + 
				"}";
		String s = ClientHelper.request(new URI("http://localhost:8081/response"), Methods.POST, mockRequest);
		
		assertTrue(StringUtils.isNotBlank(s));
	}
	
	private void check(String sql, Object expectedValue) {
		DBManager.INSTANCE.query(sql, p->{}, rs->{try {
			while (rs.next()) {
				if (null==expectedValue) {
					System.out.println(rs.getString(1));
				}else {
					assertEquals(expectedValue, rs.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}});
	}
}
