package cloudoas.apimock.specstore.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class DataManagerTest {

	@Test
	public void test() throws SQLException {
		DataManager launcher = new DataManager();
		
		Connection conn = launcher.initializeDB();
		
		launcher.createTables(conn);
		
		checkTables(conn, "select count(*) from specname_tbl");
		checkTables(conn, "select count(*) from reqpath_tbl");
		
		conn.close();
	}
	
	private void checkTables(Connection conn, String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery(sql);
		
		while (rs.next()) {
			assertEquals(0, rs.getInt(1));
		}		
	}
}
