package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TrainingsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO TRAININGS VALUES(");
		query.append("1,");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE NAME='" + values.get("SECTION") + "')");
		}
		else{
			message.append("You have to enter section");
			return null;
		}
		query.append(",");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append(",");
		if(values.containsKey("DAY")){
			String day = values.get("DAY");
			if(!StringMaster.isWeekDay(day)){
				message.append(day + " is not a week day. Week days: " + StringMaster.getWeekDays());
				return null;
			}
			query.append(StringMaster.getDayFromWeekDay(day));
		}
		else{
			message.append("You have to enter day");
			return null;
		}
		query.append(",");
		if(values.containsKey("BEGINNING_HOUR")){
			String hour = values.get("BEGINNING_HOUR");
			if(!StringMaster.isHour(hour)){
				message.append(hour + " is not a hour. Hour format: hh:mm");
				return null;
			}
			query.append(StringMaster.getHour(hour));
		}
		else{
			message.append("You have to enter beginning hour");
			return null;
		}
		query.append(",");
		if(values.containsKey("ENDING_HOUR")){
			String hour = values.get("ENDING_HOUR");
			if(!StringMaster.isHour(hour)){
				message.append(hour + " is not a hour. Hour format: hh:mm");
				return null;
			}
			query.append(StringMaster.getHour(hour));
		}
		else{
			message.append("You have to enter ending hour");
			return null;
		}
		query.append(",");
		if(values.containsKey("PLACE")){
			query.append("'" + values.get("PLACE") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE TRAININGS SET ");
		query.append("SECTION=");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE NAME='" + values.get("SECTION") + "'),");
		}
		else{
			message.append("You have to enter section");
			return null;
		}
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append("PLACE=");
		if(values.containsKey("PLACE")){
			query.append("'" + values.get("PLACE") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("DAY=");
		if(values.containsKey("DAY")){
			String day = values.get("DAY");
			if(!StringMaster.isWeekDay(day)){
				message.append(day + " is not a week day. Week days: " + StringMaster.getWeekDays());
				return null;
			}
			query.append(StringMaster.getDayFromWeekDay(day) + ",");
		}
		else{
			message.append("You have to enter day");
			return null;
		}
		query.append("BEGINNING_HOUR=");
		if(values.containsKey("BEGINNING_HOUR")){
			String hour = values.get("BEGINNING_HOUR");
			if(!StringMaster.isHour(hour)){
				message.append(hour + " is not a hour. Hour format: hh:mm");
				return null;
			}
			query.append(StringMaster.getHour(hour) + ",");
		}
		else{
			message.append("You have to enter beginning hour");
			return null;
		}
		query.append("ENDING_HOUR=");
		if(values.containsKey("ENDING_HOUR")){
			String hour = values.get("ENDING_HOUR");
			if(!StringMaster.isHour(hour)){
				message.append(hour + " is not a hour. Hour format: hh:mm");
				return null;
			}
			query.append(StringMaster.getHour(hour));
		}
		else{
			message.append("You have to enter ending hour");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("TRAININGS.SECTION=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
				case "NAME":
				case "PLACE":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "DAY":
					query.append(attribute + "=" + StringMaster.getDayFromWeekDay(value) + " AND ");
					break;
				case "BEGINNING_HOUR":
				case "ENDING_HOUR":
					query.append(attribute + "=" + StringMaster.getHour(value) + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM TRAININGS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("TRAININGS.SECTION=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
				case "NAME":
				case "PLACE":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "DAY":
					query.append(attribute + "=" + StringMaster.getDayFromWeekDay(value) + " AND ");
					break;
				case "BEGINNING_HOUR":
				case "ENDING_HOUR":
					query.append(attribute + "=" + StringMaster.getHour(value) + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "SECTION;NAME;DAY;BEGINNING_HOUR;ENDING_HOUR;PLACE";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "SECTION;NAME;DAY;BEGINNING_HOUR;ENDING_HOUR;PLACE";
	}
	
	@Override
	public String getTableColumns(){
		return "SECTION;NAME;DAY;HOURS;PLACE";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in TrainingsHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in TrainingsHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < DAY_INDEX; ++i){
				row.append(fields[i]);
				row.append(TABLE_DELIM);
			}
			row.append(StringMaster.getWeekDayFromDay(fields[i++]));
			row.append(TABLE_DELIM);
			row.append(StringMaster.getHourString(fields[i++]));
			row.append(HOURS_DELIM);
			row.append(StringMaster.getHourString(fields[i++]));
			row.append(TABLE_DELIM);
			for(; i < SELECTING_FIELDS; ++i){
				row.append(fields[i]);
				row.append(TABLE_DELIM);
			}
			if(!tableValues.add(row.toString())){
				return false;
			}
			row.delete(0, row.length());
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in TrainingsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in TrainingsHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		int i = 0;
		for(; i < DAY_INDEX; ++i){
			selectingValues.add(fields[i]);
		}
		selectingValues.add(fields[i++]);
		String[] hours = fields[i++].split(HOURS_DELIM);
		if(hours.length < HOURS_FIELDS){
			throw new RuntimeException("Problem in TrainingsHelper.setSelectingToTable: " + hours.length + " of value in tableValues less than " + HOURS_FIELDS);
		}
		for(String hoursField : hours){
			selectingValues.add(hoursField);
		}
		selectingValues.add(fields[i++]);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in TrainingsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in TrainingsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SELECTING_FIELDS = 6;
	private int TABLE_FIELDS = 5;
	private int HOURS_FIELDS = 2;
	private int DAY_INDEX = 2;
	private String TABLE_DELIM = ";";
	private String HOURS_DELIM = "-";
	private String FIELD_REPLACE = "_";
	private String SELECT_FILE = "SQL_select_trainings.txt";
}