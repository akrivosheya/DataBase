package tourists.helpers;

import java.util.*;
import java.io.*;

public class SectionsHelper implements QueryHelper{
	@Override
	public String getSelectingQuery(Map<String, String> fields, List<String> flags){
		File file = new File(SELECT_FILE);
		if(!file.exists()){
			return null;
		}
		StringBuilder query = new StringBuilder("");
		try(Scanner scanner = new Scanner(file)){
			while(scanner.hasNextLine()){
				query.append(scanner.nextLine() + "\n");
			}
		}
		catch(IOException e){
			System.err.println("Can't get scanner for file " + SELECT_FILE + ": " + e.getMessage());
			return null;
		}
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO SECTIONS VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		query.append(",(SELECT ID FROM DIRECTORS WHERE ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "DIRECTOR_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "DIRECTOR_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - (" AND ").length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE SECTIONS SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "',");
					break;
			}
		});
		query.append("DIRECTOR=(SELECT ID FROM DIRECTORS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "DIRECTOR_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "DIRECTOR_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - (" AND ").length(), query.length());
		query.append(")\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "CATEGORY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM SECTIONS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getColumns(){
		return "NAME;DIRECTOR_NAME;DIRECTOR_LAST_NAME";
	}
	
	private String scanFile(String fileName){
		File file = new File(SELECT_FILE);
		if(!file.exists()){
			return null;
		}
		StringBuilder text = new StringBuilder("");
		try(Scanner scanner = new Scanner(file)){
			while(scanner.hasNextLine()){
				text.append(scanner.nextLine());
			}
		}
		catch(IOException e){
			System.err.println("Can't get scanner for file " + SELECT_FILE + ": " + e.getMessage());
			return null;
		}
		return text.toString();
	}
	
	private String SELECT_FILE = "SQL_select_sections.txt";
}