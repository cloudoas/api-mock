package cloudoas.apimock.specstore;

import org.apache.commons.lang3.StringUtils;

public interface Defaults {
	String SERVER_HOST="localhost";
	int SERVER_PORT = 8081;	
	
	String DB_NAME="specstore";
	String DB_DRIVER="org.hsqldb.jdbc.JDBCDriver";
	String DB_USERNAME="SA";
	String DB_PASSWORD=StringUtils.EMPTY;
	String SQL_SCRIPTS_CREATE="./sql/create";
}
