package tourists.helpers;

import java.util.*;
import java.io.*;

public class ContainsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO CONTAINS VALUES(");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "')");
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
		StringBuilder query = new StringBuilder("UPDATE CONTAINS SET ");
		query.append("ROUTE=");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "'),");
		}
		else{
			query.append("NULL,");
		}
		query.append("PLACE=");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "ROUTE":
					query.append(attribute + "=(SELECT ID FROM ROUTE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM CONTAINS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "ROUTE":
					query.append(attribute + "=(SELECT ID FROM ROUTE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "ROUTE;PLACE";
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
	
	private String SELECT_FILE = "SQL_select_contains.txt";
}