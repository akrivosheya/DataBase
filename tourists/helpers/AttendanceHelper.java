package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class AttendanceHelper implements QueryHelper{
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
		if(values == null || message == null){
			throw new NullPointerException("Null arguments in AttendanceHelper.getInsertingQuery");
		}
		StringBuilder query = new StringBuilder("INSERT INTO ATTENDANCE VALUES(");
		if(values.containsKey("SPORTSMAN_NAME") || 
		values.containsKey("SPORTSMAN_LAST_NAME") || values.containsKey("SPORTSMAN_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("SPORTSMAN_NAME");
			String lastName = values.get("SPORTSMAN_LAST_NAME");
			String birth = values.get("SPORTSMAN_BIRTH");
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
			message.append("You have to enter sportsman data");
			return null;
		}
		query.append(",");
		if(values.containsKey("SECTION") && values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE ");
			String name = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("NAME='" + name + "' AND ");
			query.append("SECTION=(SELECT ID FROM SECTIONS WHERE NAME = '" + section + "'))");
		}
		else{
			message.append("You have to enter section and training");
			return null;
		}
		query.append(",");
		if(values.containsKey("TIME")){
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + values.get("TIME") + "'");
		}
		else{
			message.append("You have to enter time");
			return null;
		}
		query.append(",");
		if(values.containsKey("VISITED")){
			String visited = values.get("VISITED");
			if(!StringMaster.isFlag(visited)){
				message.append(visited + " is not a flag. Flag format: 0 or 1");
				return null;
			}
			query.append(values.get("VISITED").charAt(0));
		}
		else{
			message.append("You have to enter visited");
			return null;
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null || message == null){
			throw new NullPointerException("Null arguments in AttendanceHelper.getUpdatingQuery");
		}
		StringBuilder query = new StringBuilder("UPDATE ATTENDANCE SET ");
		query.append("TRAINING=");
		if(values.containsKey("SECTION") && values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE ");
			String name = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("NAME='" + name + "' AND ");
			query.append("SECTION=(SELECT ID FROM SECTIONS WHERE NAME = '" + section + "')),");
		}
		else{
			message.append("You have to section name and training");
			return null;
		}
		String time = values.get("TIME");
		if(!StringMaster.isDate(time)){
			message.append(time + " is not a date. Date format: dd.mm.yyyy");
			return null;
		}
		query.append("TIME=");
		if(time == null){
			message.append("You have to enter time");
			return null;
		}
		else{
			query.append("'" + time.substring(0, DATE_LENGTH) + "',");
		}
		if(!StringMaster.isFlag(values.get("VISITED"))){
			message.append(values.get("VISITED") + " is not a flag. Flag format: 0 or 1");
			return null;
		}
		if(values.get("VISITED") == null){
			message.append("You have to enter visited");
			return null;
		}
		query.append("VISITED=" + values.get("VISITED") + ",");
		query.append("SPORTSMAN=");
		if(values.containsKey("SPORTSMAN_NAME") || values.containsKey("SPORTSMAN_LAST_NAME")
		|| values.containsKey("SPORTSMAN_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("SPORTSMAN_NAME");
			String lastName = values.get("SPORTSMAN_LAST_NAME");
			String birth = values.get("SPORTSMAN_BIRTH");
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
			message.append("You have to enter sportsman data");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "VISITED":
					query.append(attribute + "=" + value + " AND ");
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
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME = '" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
		query.append("SPORTSMAN=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "SPORTSMAN_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_BIRTH":
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
		StringBuilder query = new StringBuilder("DELETE FROM ATTENDANCE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "VISITED":
					query.append(attribute + "=" + value + " AND ");
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
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME = '" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
		query.append("SPORTSMAN=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SPORTSMAN_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		System.out.println(query.toString());
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "SPORTSMAN_NAME;SPORTSMAN_LAST_NAME;SPORTSMAN_BIRTH;TRAINING;SECTION;TIME;VISITED";
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
	private String SELECT_FILE = "SQL_select_attendance.txt";
}