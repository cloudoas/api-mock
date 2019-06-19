package cloudoas.apimock.specstore;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class LauncherTest {

	@Test
	public void test() throws SQLException {
		Launcher launcher = new Launcher();
		
		Connection conn = launcher.initializeDB();
		conn.close();
	}
}
