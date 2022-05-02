package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class SportsmenHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO SPORTSMEN VALUES(");
		if(values.containsKey("NAME") || values.containsKey("LAST_NAME")
			|| values.containsKey("BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("NAME");
			String lastName = values.get("LAST_NAME");
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				System.err.println(birth + " is not a date");
				return null;
			}
			if(name != null){
				query.append("NAME='" + name + "' AND ");
			}
			if(lastName != null){
				query.append("LAST_NAME='" + lastName + "' AND ");
			}
			if(birth != null){
				query.append("BIRTH='" + birth.substring(0, DATE_LENGTH) + "' AND ");
			}
			query.delete(query.length() - " AND ".length(), query.length());
			query.append(")");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("GROUP_ID")){
			query.append(values.get("GROUP_ID"));
		}
		else{
			query.append(values.get("NULL"));
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null || fields.size() <= 2){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE SPORTSMEN SET ");
		query.append("GROUP_ID=");
		if(values.containsKey("GROUP_ID")){
			System.out.println(values.get("GROUP_ID"));
			query.append(values.get("GROUP_ID"));
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() < 2){
			return null;
		}
		StringBuilder query = new StringBuilder(
		"DELETE FROM SPORTSMEN WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		if(query.length() <= ("DELETE FROM SPORTSMEN WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ").length()){
			return null;
		}
		query.setCharAt(query.length() - (" AND ").length(), ')');
		return query.substring(0, query.length() - "AND ".length());
	}
	
	@Override
	public String getColumns(){
		return "NAME;LAST_NAME;BIRTH;GROUP_ID";
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
	
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_sportsmen.txt";
}