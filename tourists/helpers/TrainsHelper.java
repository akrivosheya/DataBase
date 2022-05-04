package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TrainsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO TRAINS VALUES(");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE NAME='" + values.get("TRAINING") + "')");
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
		StringBuilder query = new StringBuilder("UPDATE TRAINS SET ");
		query.append("GROUP_ID=");
		if(values.containsKey("GROUP_ID")){
			query.append(values.get("GROUP_ID") + ",");
		}
		else{
			query.append("NULL,");
		}
		query.append("TRAINING=");
		if(values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE NAME='" + values.get("TRAINING") + "'),");
		}
		else{
			query.append("NULL,");
		}
		query.append("COACH=");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "GROUP_ID":
					query.append(attribute + "=" + value + " AND ");
					break;
				case "TRAINING":
					query.append(attribute + "=(SELECT ID FROM TRAININGS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
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
				case "GROUP_ID":
					query.append(attribute + "=" + value + " AND ");
					break;
				case "TRAINING":
					query.append(attribute + "=(SELECT ID FROM TRAININGS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
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
		return "COACH_NAME;COACH_LAST_NAME;COACH_BIRTH;GROUP_ID;TRAINING";
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