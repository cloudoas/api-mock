package cloudoas.apimock.specstore.db;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class DataManagerTest {

	@Test
	public void test() throws SQLException {
		DataManager db = new DataManager();
		
		db.initialize();
		
		db.createTables();
		
		checkTables(db, "select count(*) from spec_tbl");
		checkTables(db, "select count(*) from reqpath_tbl");
		
		db.addSpec("test", "1.0.0");
		
		checkTables(db, "select count(*) from spec_tbl");
		
		db.close();
	}
	
	private void checkTables(DataManager db, String sql) {
		db.query(sql, p->{}, rs->{try {
			if (rs.next()) {
				System.out.println("row count: "+rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}});
	}
}
