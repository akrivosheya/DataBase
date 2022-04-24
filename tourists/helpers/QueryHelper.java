package tourists.helpers;

import java.util.*;

public interface QueryHelper{
	public String getSelectingQuery(Map<String, String> fields, List<String> flags);
	
	public String getInsertingQuery(Map<String, String> values);
	
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields);
	
	public String getDeletingQuery(Map<String, String> params);
	
	public String getColumns();
}