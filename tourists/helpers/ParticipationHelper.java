package tourists.helpers;

import java.util.*;
import java.io.*;

public class ParticipationHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO PARTICIPATION VALUES(");
		if(values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE NAME='" + values.get("TRAINING") + "')");
		}
		query.append(",");
		if(values.containsKey("PARTICIPANT_NAME") && values.containsKey("PARTICIPANT_LAST_NAME") && values.containsKey("PARTICIPANT_BIRTH")){
			query.append("(SELECT ID FROM SPORTSMEN WHERE NAME='" + values.get("PARTICIPANT_NAME") + "' AND ");
			query.append("LAST_NAME='" + values.get("PARTICIPANT_LAST_NAME") + "' AND ");
			query.append("BIRTH='" + values.get("PARTICIPANT_BIRTH") + "')");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE PARTICIPATION SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "COMPETITION":
					query.append(attribute + "=(SELECT ID FROM COMPETITIONS WHERE NAME='" + value + "'),");
					break;
			}
		});
		query.append("PARTICIPANT=(SELECT ID FROM TOURISTS WHERE ");
		values.forEach((String attribute, String value)->{
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
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "COMPETITION":
					query.append(attribute + "=(SELECT ID FROM COMPETITIONS WHERE NAME='" + value + "') AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM PARTICIPATION WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "COMPETITION":
					query.append(attribute + "=(SELECT ID FROM COMPETITIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("PARTICIPANT=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
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
	public String getColumns(){
		return "PARTICIPANT_NAME;PARTICIPANT_LAST_NAME;PARTICIPANT_BIRTH;COMPETITION";
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
	
	private String SELECT_FILE = "SQL_select_participation.txt";
}