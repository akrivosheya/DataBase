package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class CheckpointsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO CHECK_POINT VALUES(");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM HIKE WHERE NAME='" + values.get("HIKE") + "')");
		}
		else{
			message.append("You have to enter hike");
			return null;
		}
		query.append(",");
		if(values.containsKey("DAY")){
			String day = values.get("DAY");
			if(!StringMaster.isNumber(day)){
				message.append(day + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(day);
		}
		else{
			message.append("You have to enter day");
			return null;
		}
		query.append(",");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "')");
		}
		else{
			message.append("You have to enter place");
			return null;
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null || message == null){
			throw new NullPointerException("Null arguments in CheckpointsHelper.getUpdatingQuery");
		}
		StringBuilder query = new StringBuilder("UPDATE CHECK_POINT SET ");
		query.append("HIKE=");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM HIKE WHERE NAME='" + values.get("HIKE") + "'),");
		}
		else{
			message.append("You have to enter hike");
			return null;
		}
		query.append("PLACE=");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "'),");
		}
		else{
			message.append("You have to enter place");
			return null;
		}
		query.append("DAY=");
		if(values.containsKey("DAY")){
			String day = values.get("DAY");
			if(!StringMaster.isNumber(day)){
				message.append(day + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(day);
		}
		else{
			message.append("You have to enter day");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
					break;
				case "DAY":
					query.append(attribute + "=" + value + " AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM CHECK_POINT WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
					break;
				case "DAY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "HIKE;DAY;PLACE";
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
	private String SELECT_FILE = "SQL_select_checkpoints.txt";
}