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
	public String getSelectingColumns(){
		return "HIKE;DAY;PLACE";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "HIKE;DAY;PLACE";
	}
	
	@Override
	public String getTableColumns(){
		return "HIKE;DAY;PLACE";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in CheckpointsHelper.setSelectingToTable: null argument");
		}
		if(!tableValues.addAll(selectingValues)){
			return false;
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in CheckpointsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		selectingValues.addAll(tableValues);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in CheckpointsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in CheckpointsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
		}
		return selectingValues;
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
	
	private int SELECTING_FIELDS = 3;
	private String SELECT_FILE = "SQL_select_checkpoints.txt";
}