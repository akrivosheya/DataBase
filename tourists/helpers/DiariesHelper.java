package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class DiariesHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO DIARY VALUES(1,");
		if(values.containsKey("HIKE") && values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
			String hike = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("HIKE.NAME='" + hike + "' AND ");
			query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "')");
		}
		else{
			message.append("You have to enter hike and time");
			return null;
		}
		query.append(",");
		if(values.containsKey("TEXT")){
			query.append("'" + values.get("TEXT") + "'");
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
		StringBuilder query = new StringBuilder("UPDATE DIARY SET ");
		query.append("HIKE=");
		if(values.containsKey("HIKE") && values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
			String hike = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("HIKE.NAME='" + hike + "' AND ");
			query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "'),");
		}
		else{
			message.append("You have to enter hike and time");
			return null;
		}
		query.append("TEXT=");
		if(values.containsKey("TEXT")){
			query.append("'" + values.get("TEXT") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM DIARY WHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE=HIKE.ID AND ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getSelectingColumns(){
		return "HIKE;TIME;TEXT";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "HIKE;TIME;TEXT";
	}
	
	@Override
	public String getTableColumns(){
		return "HIKE;TEXT";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in DiariesHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in DiariesHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < HIKE_FIELDS; ++i){
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
			throw new NullPointerException("Problem in DiariesHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in DiariesHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] hike = fields[HIKE_INDEX].split(FIELD_DELIM);
		if(hike.length < HIKE_FIELDS){
			throw new RuntimeException("Problem in DiariesHelper.setSelectingToTable: " + hike.length + " of value in tableValues less than " + HIKE_FIELDS);
		}
		for(String hikeField : hike){
			selectingValues.add(hikeField);
		}
		selectingValues.add(fields[TEXT_INDEX]);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in DiariesHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in DiariesHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private int TABLE_FIELDS = 2;
	private int HIKE_FIELDS = 2;
	private int HIKE_INDEX = 0;
	private int TEXT_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_diaries.txt";
}