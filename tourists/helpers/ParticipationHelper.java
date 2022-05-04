package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class ParticipationHelper implements QueryHelper{
	@Override
	public String getSelectingQuery(Map<String, String> fields, List<String> flags, StringBuilder message){
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
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO PARTICIPATION VALUES(");
		if(values.containsKey("COMPETITION")){
			query.append("(SELECT ID FROM COMPETITIONS WHERE NAME='" + values.get("COMPETITION") + "')");
		}
		else{
			message.append("You have to enter competition");
			return null;
		}
		query.append(",");
		if(values.containsKey("PARTICIPANT_NAME") || values.containsKey("PARTICIPANT_LAST_NAME")
			|| values.containsKey("PARTICIPANT_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("PARTICIPANT_NAME");
			String lastName = values.get("PARTICIPANT_LAST_NAME");
			String birth = values.get("PARTICIPANT_BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
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
			message.append("You have to enter participant data");
			return null;
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE PARTICIPATION SET ");
		query.append("COMPETITION=");
		if(values.containsKey("COMPETITION")){
			query.append("(SELECT ID FROM COMPETITIONS WHERE NAME='" + values.get("COMPETITION") + "'),");
		}
		else{
			message.append("You have to enter competition");
			return null;
		}
		query.append("PARTICIPANT=");
		if(values.containsKey("PARTICIPANT_NAME") || values.containsKey("PARTICIPANT_LAST_NAME")
			|| values.containsKey("PARTICIPANT_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("PARTICIPANT_NAME");
			String lastName = values.get("PARTICIPANT_LAST_NAME");
			String birth = values.get("PARTICIPANT_BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
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
			message.append("You have to enter participant data");
			return null;
		}
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
	
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_participation.txt";
}