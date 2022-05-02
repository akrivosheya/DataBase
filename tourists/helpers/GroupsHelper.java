package tourists.helpers;

import java.util.*;
import java.io.*;

public class GroupsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO GROUPS VALUES(");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("ID")){
			query.append(values.get("ID"));
		}
		else{
			query.append("NULL");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE GROUPS SET ");
		query.append("SECTION=");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "'),");
		}
		else{
			query.append("NULL,");
		}
		query.append("ID=");
		if(values.containsKey("ID")){
			query.append(values.get("ID"));
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "ID":
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
		StringBuilder query = new StringBuilder("DELETE FROM GROUPS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("GROUPS.SECTION=(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + value + "') AND ");
					break;
				case "ID":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getColumns(){
		return "SECTION;ID";
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
	
	private String SELECT_FILE = "SQL_select_groups.txt";
}