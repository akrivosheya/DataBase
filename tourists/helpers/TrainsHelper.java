package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TrainsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO TRAINS VALUES(");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
			message.append("You have to enter coach data");
			return null;
		}
		query.append(",");
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME='" + values.get("GROUP_NAME") + "')");
		}
		else{
			message.append("You have to enter group name");
			return null;
		}
		query.append(",");
		if(values.containsKey("TRAINING") && values.containsKey("SECTION")){
			query.append("(SELECT TRAININGS.ID FROM TRAININGS, SECTIONS WHERE TRAININGS.SECTION=SECTIONS.ID AND ");
			String training = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("SECTIONS.NAME='" + section + "' AND ");
			query.append("TRAININGS.NAME='" + training + "')");
		}
		else{
			message.append("You have to enter training and section");
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
		StringBuilder query = new StringBuilder("UPDATE TRAINS SET ");
		query.append("GROUP_ID=");
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME='" + values.get("GROUP_NAME") + "'),");
		}
		else{
			message.append("You have to enter group name");
			return null;
		}
		query.append("TRAINING=");
		if(values.containsKey("TRAINING") && values.containsKey("SECTION")){
			query.append("(SELECT TRAININGS.ID FROM TRAININGS, SECTIONS WHERE TRAININGS.SECTION=SECTIONS.ID AND ");
			String training = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("SECTIONS.NAME='" + section + "' AND ");
			query.append("TRAININGS.NAME='" + training + "'),");
		}
		else{
			message.append("You have to enter training and section");
			return null;
		}
		query.append("COACH=");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
			message.append("You have to enter coach data");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "GROUP_NAME":
					query.append("GROUP_ID=(SELECT ID FROM GROUPS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("TRAINING=(SELECT ID FROM TRAININGS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "TRAINING":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SECTION":
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
		query.append("COACH=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "COACH_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "COACH_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "COACH_BIRTH":
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
		StringBuilder query = new StringBuilder("DELETE FROM TRAINS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "GROUP_NAME":
					query.append("GROUP_ID=(SELECT ID FROM GROUPS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("TRAINING=(SELECT ID FROM TRAININGS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "TRAINING":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SECTION":
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
		query.append("COACH=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "COACH_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "COACH_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "COACH_BIRTH":
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
		return "COACH_NAME;COACH_LAST_NAME;COACH_BIRTH;GROUP_NAME;TRAINING;SECTION";
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
	private String SELECT_FILE = "SQL_select_trains.txt";
}