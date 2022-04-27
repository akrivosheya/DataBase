package tourists.helpers;

import java.util.*;
import java.io.*;

public class ConductedHikesHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO CONDUCTED_HIKE VALUES(1,");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM HIKE WHERE NAME='" + values.get("HIKE") + "')");
		}
		query.append(",");
		if(values.containsKey("INSTRUCTOR_NAME") && values.containsKey("INSTRUCTOR_LAST_NAME") && values.containsKey("INSTRUCTOR_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE NAME='" + values.get("INSTRUCTOR_NAME") + "' AND ");
			query.append("LAST_NAME='" + values.get("INSTRUCTOR_LAST_NAME") + "' AND ");
			query.append("BIRTH='" + values.get("INSTRUCTOR_BIRTH") + "')");
		}
		query.append(",");
		if(values.containsKey("TIME")){
			query.append("'" + values.get("TIME") + "'");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE CONDUCTED_HIKE SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "'),");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "',");
					break;
			}
		});
		query.append("INSTRUCTOR=(SELECT ID FROM TOURISTS WHERE ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "INSTRUCTOR_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "INSTRUCTOR_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "INSTRUCTOR_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "'),");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "',");
					break;
			}
		});
		query.append("PARTICIPANT=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "PARTICIPANT_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM CONDUCTED_HIKE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "') AND ");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "HIKE;INSTRUCTOR_NAME;INSTRUCTOR_LAST_NAME;INSTRUCTOR_BIRTH;TIME";
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
	
	private String SELECT_FILE = "SQL_select_conducted_hikes.txt";
}