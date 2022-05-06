package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class SportsmenHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO SPORTSMEN VALUES(");
		if(values.containsKey("NAME") || values.containsKey("LAST_NAME")
			|| values.containsKey("BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("NAME");
			String lastName = values.get("LAST_NAME");
			String birth = values.get("BIRTH");
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
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME ='" + values.get("GROUP_NAME") + "')");
		}
		else{
			message.append("You have to enter group");
			return null;
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null || fields.size() <= 2){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE SPORTSMEN SET ");
		query.append("GROUP_ID=");
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME ='" + values.get("GROUP_NAME") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() < 2){
			return null;
		}
		StringBuilder query = new StringBuilder(
		"DELETE FROM SPORTSMEN WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		if(query.length() <= ("DELETE FROM SPORTSMEN WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ").length()){
			return null;
		}
		query.setCharAt(query.length() - (" AND ").length(), ')');
		return query.substring(0, query.length() - "AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "NAME;LAST_NAME;BIRTH;GROUP_NAME";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "GROUP_NAME";
	}
	
	@Override
	public String getTableColumns(){
		return "SPORTSMAN;GROUP_NAME";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in SportsmanHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in SportsmanHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < SPORTSMAN_FIELDS; ++i){
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
			throw new NullPointerException("Problem in SportsmanHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in SportsmanHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] sportsman = fields[SPORTSMAN_INDEX].split(FIELD_DELIM);
		if(sportsman.length < SPORTSMAN_FIELDS){
			throw new RuntimeException("Problem in SportsmanHelper.setSelectingToTable: " + sportsman.length + " of value in tableValues less than " + SPORTSMAN_FIELDS);
		}
		for(String sportsmanField : sportsman){
			selectingValues.add(sportsmanField);
		}
		for(int i = OTHER_INDEX; i < TABLE_FIELDS; ++i){
			selectingValues.add(fields[i]);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in SportsmanHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in SportsmanHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
		}
		return selectingValues.subList(SPORTSMAN_FIELDS, selectingValues.size());
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
	
	private int SELECTING_FIELDS = 4;
	private int TABLE_FIELDS = 2;
	private int SPORTSMAN_FIELDS = 3;
	private int SPORTSMAN_INDEX = 0;
	private int OTHER_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_sportsmen.txt";
}