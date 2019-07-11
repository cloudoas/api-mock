package cloudoas.apimock.specstore.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class DataManagerTest {

	@Test
	public void test() throws SQLException {
		DataManager db = new DataManager();
		
		db.initialize();
		
		db.createTables();
		
		db.addResponse("petstore", "1.0.0", "/get", "application/json", "200", "OK");
		
		check(db, "select count(*) from respindex_tbl", 1);
		check(db, "select count(*) from respbody_tbl", 1);
		
		db.close();
	}
	
	private void check(DataManager db, String sql, Object expectedValue) {
		db.query(sql, p->{}, rs->{try {
			if (rs.next()) {
				assertEquals(expectedValue, rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}});
	}
}
