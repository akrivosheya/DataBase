package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class ConductedHikesHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO CONDUCTED_HIKE VALUES(1,");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM HIKE WHERE NAME='" + values.get("HIKE") + "')");
		}
		else{
			message.append("You have to enter hike");
			return null;
		}
		query.append(",");
		if(values.containsKey("INSTRUCTOR_NAME") || values.containsKey("INSTRUCTOR_LAST_NAME")
			|| values.containsKey("INSTRUCTOR_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("INSTRUCTOR_NAME");
			String lastName = values.get("INSTRUCTOR_LAST_NAME");
			String birth = values.get("INSTRUCTOR_BIRTH");
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
			message.append("You have to enter instructor data");
			return null;
		}
		query.append(",");
		if(values.containsKey("TIME")){
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + time.substring(0, DATE_LENGTH) + "'");
		}
		else{
			message.append("You have to enter time");
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
		StringBuilder query = new StringBuilder("UPDATE CONDUCTED_HIKE SET ");
		query.append("HIKE=");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM HIKE WHERE NAME='" + values.get("HIKE") + "'),");
		}
		else{
			message.append("You have to enter hike");
			return null;
		}
		query.append("TIME=");
		if(values.containsKey("TIME")){
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + time.substring(0, DATE_LENGTH) + "',");
		}
		else{
			message.append("You have to enter time");
			return null;
		}
		query.append("INSTRUCTOR=");
		if(values.containsKey("INSTRUCTOR_NAME") || values.containsKey("INSTRUCTOR_LAST_NAME")
			|| values.containsKey("INSTRUCTOR_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("INSTRUCTOR_NAME");
			String lastName = values.get("INSTRUCTOR_LAST_NAME");
			String birth = values.get("INSTRUCTOR_BIRTH");
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
			message.append("You have to enter instructor data");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM HIKE WHERE NAME='" + value + "') AND ");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		query.append("INSTRUCTOR=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
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
	public String getSelectingColumns(){
		return "HIKE;INSTRUCTOR_NAME;INSTRUCTOR_LAST_NAME;INSTRUCTOR_BIRTH;TIME";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "HIKE;INSTRUCTOR_NAME;INSTRUCTOR_LAST_NAME;INSTRUCTOR_BIRTH;TIME";
	}
	
	@Override
	public String getTableColumns(){
		return "HIKE;INSTRUCTOR;TIME";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in ConductedHikesHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in ConductedHikesHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			row.append(fields[i++]);
			row.append(TABLE_DELIM);
			for(; i < INSTRUCTOR_FIELDS + 1; ++i){
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
			throw new NullPointerException("Problem in ConductedHikesHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in ConductedHikesHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		selectingValues.add(fields[0]);
		String[] instructor = fields[INSTRUCTOR_INDEX].split(FIELD_DELIM);
		if(instructor.length < INSTRUCTOR_FIELDS){
			throw new RuntimeException("Problem in ConductedHikesHelper.setSelectingToTable: " + instructor.length + " of value in tableValues less than " + INSTRUCTOR_FIELDS);
		}
		for(String instructorField : instructor){
			selectingValues.add(instructorField);
		}
		selectingValues.add(fields[TIME_INDEX]);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in ConductedHikesHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in ConductedHikesHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SELECTING_FIELDS = 5;
	private int TABLE_FIELDS = 3;
	private int INSTRUCTOR_FIELDS = 3;
	private int INSTRUCTOR_INDEX = 1;
	private int TIME_INDEX = 2;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_conducted_hikes.txt";
}