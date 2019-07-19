package cloudoas.apimock.specstore;

import cloudoas.apimock.common.file.Configuration;
import cloudoas.apimock.specstore.db.DBManager;
import cloudoas.apimock.specstore.db.SpecDAO;

public class Launcher {

	public static void main(String[] args) {
		Configuration config = Configuration.fromResource(ConfigItems.CONFIG_NAME);
		
		DBManager.INSTANCE.init(config);
		DBManager.INSTANCE.createTables();
		
		SpecDAO.INSTANCE.init(DBManager.INSTANCE, config);
		SpecDAO.INSTANCE.loadLocalSpecFiles();
		
		SpecStoreServer server = new SpecStoreServer();
		server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				DBManager.INSTANCE.close();
				server.stop();
			}
		});
	}
}
