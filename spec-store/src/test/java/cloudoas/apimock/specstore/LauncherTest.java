package cloudoas.apimock.specstore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class LauncherTest {

	@Test
	public void test() throws SQLException {
		Launcher launcher = new Launcher();
		
		Connection conn = launcher.initializeDB();
		
		launcher.createTables(conn);
		
		Statement stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("select count(*) from test_tbl");
		
		while (rs.next()) {
			assertEquals(0, rs.getInt(1));
		}
		
		conn.close();
	}
	
	@Test
	public void testPath() throws IOException {
		File cwd = new File(".");
		
		System.out.println(cwd.getCanonicalFile().getAbsolutePath());
	}
}
