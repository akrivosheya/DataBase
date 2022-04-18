package tourists;

import java.util.*;
import java.io.*;

import tourists.helpers.QueryHelper;

public class QueryMaster{
	public List<String> getQueries(String fileName){
		if(fileName == null){
			return null;
		}
		File file = new File(fileName);
		if(!file.exists()){
			return null;
		}
		List<String> queries = null;
		try(Scanner scanner = new Scanner(file)){
			StringBuilder query = new StringBuilder("");
			queries = new ArrayList<String>();
			while(scanner.hasNextLine()){
				query.append(scanner.nextLine());
				if(query.charAt(query.length() - 1) == END_LINE){
					queries.add(query.substring(0, query.length() - 1));
					query.delete(0, query.length());
				}
			}
		}
		catch(IOException e){
			System.err.println("Can't get scanner for file " + fileName + ": " + e.getMessage());
			return null;
		}
		return queries;
	}
	
	public String getSelectingQuery(Map<String, String> fields, List<String> flags){
		if(helper == null){
			return null;
		}
		String query = helper.getSelectingQuery(fields, flags);
		return query;
	}
	
	public String getDeletingQuery(Map<String, String> fields){
		if(helper == null){
			return null;
		}
		String query = helper.getDeletingQuery(fields);
		return query;
	}
	
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(helper == null){
			return null;
		}
		String query = helper.getUpdatingQuery(values, fields);
		return query;
	}
	
	public String getInsertingQuery(Map<String, String> values){
		if(helper == null){
			return null;
		}
		String query = helper.getInsertingQuery(values);
		return query;
	}
	
	public int getColumnsSize(){
		if(helper == null){
			return 0;
		}
		return helper.getColumnsSize();
	}
	
	public String getColumns(){
		if(helper == null){
			return null;
		}
		return helper.getColumns();
	}
	
	public void setHelper(QueryHelper helper){
		this.helper = helper;
	}

	private char END_LINE = '|';
	private QueryHelper helper;
}