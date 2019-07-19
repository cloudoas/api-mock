package cloudoas.apimock.specstore.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cloudoas.apimock.common.file.Configuration;
import cloudoas.apimock.specstore.ConfigItems;

public class DBTest {
	
	@BeforeAll
	public static void setup() {
		Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
		DBManager.INSTANCE.init(config);
		DBManager.INSTANCE.createTables();
		SpecDAO.INSTANCE.init(DBManager.INSTANCE, config);
	}
	
	@AfterAll
	public static void cleanup() {
		DBManager.INSTANCE.close();
	}

	@Test
	public void test() throws SQLException {
		SpecDAO.INSTANCE.loadLocalSpecFiles();
		
		check("select count(*) from spec_tbl", 1);
		
		//check("select count(*) from respindex_tbl", 1);
		//check("select count(*) from respbody_tbl", 1);
	}
	
	private void check(String sql, Object expectedValue) {
		DBManager.INSTANCE.query(sql, p->{}, rs->{try {
			if (rs.next()) {
				assertEquals(expectedValue, rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}});
	}
}
