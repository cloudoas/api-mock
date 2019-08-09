package cloudoas.apimock.specstore.db;

import cloudoas.apimock.common.file.Configuration;

public class SQL {
	private static final Configuration sqls = Configuration.fromResource("sqls.properties");
	
	public static final String INSERT_SPEC=sqls.getString("insert.spec");
	public static final String INSERT_REQUEST_PATH=sqls.getString("insert.request.path");
	public static final String INSERT_CONTENT_TYPE=sqls.getString("insert.content.type");
	public static final String INSERT_RESP_BODY=sqls.getString("insert.resp.body");
	public static final String INSERT_RESP_INDEX=sqls.getString("insert.resp.index");
	
	public static final String FIND_SPEC_ID=sqls.getString("find.spec.id");
	public static final String FIND_REQUEST_PATH_ID=sqls.getString("find.request.path.id");
	public static final String FIND_REQUEST_PATHS=sqls.getString("find.request.paths");
	public static final String FIND_CONTENT_TYPE_ID=sqls.getString("find.content.type.id");
	public static final String FIND_RESP_BODY_ID=sqls.getString("find.resp.body.id");
	public static final String FIND_RESP_ALL=sqls.getString("find.resp.all");
	public static final String FIND_RESP_OF_CONTENTTYPE=sqls.getString("find.resp.of.contenttype");
}
