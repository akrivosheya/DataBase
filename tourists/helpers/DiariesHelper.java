package tourists.helpers;

import java.util.*;
import java.io.*;

public class DiariesHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO DIARY VALUES(1,");
		if(values.containsKey("HIKE") && values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND HIKE.NAME='" + values.get("HIKE") + "' AND ");
			query.append("CONDUCTED_HIKE.TIME='" + values.get("TIME") + "')");
		}
		query.append(",");
		if(values.containsKey("TEXT")){
			query.append("'" + values.get("TEXT") + "'");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE DIARY SET ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append("),");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "TEXT":
					query.append("TEXT='" + value + "'");
					break;
			}
		});
		query.append("\nWHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM DIARY WHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "HIKE;TIME;TEXT";
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
	
	private String SELECT_FILE = "SQL_select_diaries.txt";
}