package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class WentToHikeHelper implements QueryHelper{
	@Override
	public String getSelectingQuery(Map<String, String> fields, List<String> flags, StringBuilder message){
		File file = new File(SELECT_FILE);
		if(!file.exists()){
		System.out.println("NO FILE");
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
		System.out.println(query.toString());
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO WENT_TO_HIKE VALUES(");
		
		if(values.containsKey("HIKE") && values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
			String name = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			if(name != null){
				query.append("HIKE.NAME='" + name + "' AND ");
			}
			if(time != null){
				query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "' AND ");
			}
			query.delete(query.length() - " AND ".length(), query.length());
			query.append(")");
		}
		else{
			message.append("You have to enter hike data");
			return null;
		}
		query.append(",");
		if(values.containsKey("TOURIST_NAME") || values.containsKey("TOURIST_LAST_NAME")
			|| values.containsKey("TOURIST_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("TOURIST_NAME");
			String lastName = values.get("TOURIST_LAST_NAME");
			String birth = values.get("TOURIST_BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date fromat: dd.mm.yyyy");
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
			message.append("You have to enter tourists data");
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
		StringBuilder query = new StringBuilder("UPDATE WENT_TO_HIKE SET ");
		query.append("HIKE=");
		if(values.containsKey("HIKE") && values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
			String name = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				message.append(time + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			if(name != null){
				query.append("HIKE.NAME='" + name + "' AND ");
			}
			if(time != null){
				query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "' AND ");
			}
			query.delete(query.length() - " AND ".length(), query.length());
			query.append("),");
		}
		else{
			message.append("You have to enter hike data");
			return null;
		}
		query.append("TOURIST=");
		if(values.containsKey("TOURIST_NAME") || values.containsKey("TOURIST_LAST_NAME")
			|| values.containsKey("TOURIST_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("TOURIST_NAME");
			String lastName = values.get("TOURIST_LAST_NAME");
			String birth = values.get("TOURIST_BIRTH");
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
			message.append("You have to enter tourist data");
			return null;
		}
		query.append("\nWHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
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
		query.append(") AND ");
		query.append("TOURIST=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "TOURIST_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "TOURIST_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "TOURIST_BIRTH":
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
		StringBuilder query = new StringBuilder("DELETE FROM WENT_TO_HIKE WHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
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
		query.append(") AND ");
		query.append("TOURIST=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "TOURIST_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "TOURIST_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "TOURIST_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getSelectingColumns(){
		return "TOURIST_NAME;TOURIST_LAST_NAME;TOURIST_BIRTH;HIKE;TIME";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "TOURIST_NAME;TOURIST_LAST_NAME;TOURIST_BIRTH;HIKE;TIME";
	}
	
	@Override
	public String getTableColumns(){
		return "TOURIST;HIKE";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in WentToHikeHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in WentToHikeHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < TOURIST_FIELDS; ++i){
				row.append(fields[i]);
				row.append(FIELD_DELIM);
			}
			row.delete(row.length() - FIELD_DELIM.length(), row.length());
			row.append(TABLE_DELIM);
			for(; i < SELECTING_FIELDS; ++i){
				row.append(fields[i]);
				row.append(FIELD_DELIM);
			}
			row.delete(row.length() - FIELD_DELIM.length(), row.length());
			row.append(TABLE_DELIM);
			if(!tableValues.add(row.toString())){
				return false;
			}
			row.delete(0, row.length());
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in WentToHikeHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in WentToHikeHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] tourist = fields[TOURIST_INDEX].split(FIELD_DELIM);
		if(tourist.length < TOURIST_FIELDS){
			throw new RuntimeException("Problem in WentToHikeHelper.setSelectingToTable: " + tourist.length + " of value in tableValues less than " + TOURIST_FIELDS);
		}
		for(String touristField : tourist){
			selectingValues.add(touristField);
		}
		String[] hike = fields[HIKE_INDEX].split(FIELD_DELIM);
		if(hike.length < HIKE_FIELDS){
			throw new RuntimeException("Problem in WentToHikeHelper.setSelectingToTable: " + hike.length + " of value in tableValues less than " + HIKE_FIELDS);
		}
		for(String hikeField : hike){
			selectingValues.add(hikeField);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in WentToHikeHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in WentToHikeHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private int TABLE_FIELDS = 2;
	private int TOURIST_FIELDS = 3;
	private int HIKE_FIELDS = 2;
	private int TOURIST_INDEX = 0;
	private int HIKE_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_went_to_hike.txt";
}