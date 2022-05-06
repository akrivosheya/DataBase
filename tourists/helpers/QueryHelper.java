package tourists.helpers;

import java.util.*;

public interface QueryHelper{
	public String getSelectingQuery(Map<String, String> fields, List<String> flags, StringBuilder message);
	
	public String getInsertingQuery(Map<String, String> values, StringBuilder message);
	
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message);
	
	public String getDeletingQuery(Map<String, String> params);
	
	public String getSelectingColumns();
	
	public String getUpdatingColumns();
	
	public String getTableColumns();
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues);
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues);
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues);
}