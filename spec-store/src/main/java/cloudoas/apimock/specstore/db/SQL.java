package cloudoas.apimock.specstore.db;

import cloudoas.apimock.common.Configuration;

public class SQL {
	private static final Configuration sqls = Configuration.fromResource("sqls.properties");
	
	public static final String INSERT_SPEC=sqls.getString("insert.spec");
	public static final String INSERT_REUEST_PATH=sqls.getString("insert.request.path");
	public static final String INSERT_CONTENT_TYPE=sqls.getString("insert.content.type");
	public static final String INSERT_RESP_BODY=sqls.getString("insert.resp.body");
	public static final String INSERT_RESP_INDEX=sqls.getString("insert.resp.index");
	
	public static final String FIND_CONTENT_TYPE_ID=sqls.getString("find.content.type.id");
	public static final String FIND_RESP_BODY_ID=sqls.getString("find.resp.body.id");
}
