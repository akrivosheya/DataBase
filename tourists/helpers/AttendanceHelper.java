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
	public String getSelectingColumns(){
		return "SPORTSMAN_NAME;SPORTSMAN_LAST_NAME;SPORTSMAN_BIRTH;TRAINING;SECTION;TIME;VISITED";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "SPORTSMAN_NAME;SPORTSMAN_LAST_NAME;SPORTSMAN_BIRTH;TRAINING;SECTION;TIME;VISITED";
	}
	
	@Override
	public String getTableColumns(){
		return "SPORTSMAN;TRAINING;TIME;VISITED";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in AttendanceHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in AttendanceHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < SPORTSMAN_FIELDS; ++i){
				row.append(fields[i]);
				row.append(FIELD_DELIM);
			}
			row.delete(row.length() - FIELD_DELIM.length(), row.length());
			row.append(TABLE_DELIM);
			for(; i < SPORTSMAN_FIELDS + TRAINING_FIELDS; ++i){
				row.append(fields[i]);
				row.append(FIELD_DELIM);
			}
			row.delete(row.length() - FIELD_DELIM.length(), row.length());
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
			throw new NullPointerException("Problem in AttendanceHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in AttendanceHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] sportsman = fields[SPORTSMAN_INDEX].split(FIELD_DELIM);
		if(sportsman.length < SPORTSMAN_FIELDS){
			throw new RuntimeException("Problem in AttendanceHelper.setSelectingToTable: " + sportsman.length + " of value in tableValues less than " + SPORTSMAN_FIELDS);
		}
		for(String sportsmanField : sportsman){
			selectingValues.add(sportsmanField);
		}
		String[] training = fields[TRAINING_INDEX].split(FIELD_DELIM);
		if(training.length < TRAINING_FIELDS){
			throw new RuntimeException("Problem in AttendanceHelper.setSelectingToTable: " + training.length + " of value in tableValues less than " + TRAINING_FIELDS);
		}
		for(String trainingField : training){
			selectingValues.add(trainingField);
		}
		for(int i = TIME_INDEX; i < TABLE_FIELDS; ++i){
			selectingValues.add(fields[i]);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in AttendanceHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in AttendanceHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SPORTSMAN_INDEX = 0;
	private int TRAINING_INDEX = 1;
	private int TIME_INDEX = 2;
	private int VISITED_INDEX = 3;
	private int SELECTING_FIELDS = 7;
	private int TABLE_FIELDS = 4;
	private int SPORTSMAN_FIELDS = 3;
	private int TRAINING_FIELDS = 2;
	private int DATE_LENGTH = 10;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private String SELECT_FILE = "SQL_select_attendance.txt";
}