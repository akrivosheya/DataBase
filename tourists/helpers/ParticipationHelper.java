package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class ParticipationHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO PARTICIPATION VALUES(");
		if(values.containsKey("COMPETITION")){
			query.append("(SELECT ID FROM COMPETITIONS WHERE NAME='" + values.get("COMPETITION") + "')");
		}
		else{
			message.append("You have to enter competition");
			return null;
		}
		query.append(",");
		if(values.containsKey("PARTICIPANT_NAME") || values.containsKey("PARTICIPANT_LAST_NAME")
			|| values.containsKey("PARTICIPANT_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("PARTICIPANT_NAME");
			String lastName = values.get("PARTICIPANT_LAST_NAME");
			String birth = values.get("PARTICIPANT_BIRTH");
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
			message.append("You have to enter participant data");
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
		StringBuilder query = new StringBuilder("UPDATE PARTICIPATION SET ");
		query.append("COMPETITION=");
		if(values.containsKey("COMPETITION")){
			query.append("(SELECT ID FROM COMPETITIONS WHERE NAME='" + values.get("COMPETITION") + "'),");
		}
		else{
			message.append("You have to enter competition");
			return null;
		}
		query.append("PARTICIPANT=");
		if(values.containsKey("PARTICIPANT_NAME") || values.containsKey("PARTICIPANT_LAST_NAME")
			|| values.containsKey("PARTICIPANT_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("PARTICIPANT_NAME");
			String lastName = values.get("PARTICIPANT_LAST_NAME");
			String birth = values.get("PARTICIPANT_BIRTH");
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
			message.append("You have to enter participant data");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "COMPETITION":
					query.append(attribute + "=(SELECT ID FROM COMPETITIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("PARTICIPANT=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "PARTICIPANT_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_BIRTH":
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
		StringBuilder query = new StringBuilder("DELETE FROM PARTICIPATION WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "COMPETITION":
					query.append(attribute + "=(SELECT ID FROM COMPETITIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("PARTICIPANT=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "PARTICIPANT_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "PARTICIPANT_BIRTH":
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
		return "PARTICIPANT_NAME;PARTICIPANT_LAST_NAME;PARTICIPANT_BIRTH;COMPETITION";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "PARTICIPANT_NAME;PARTICIPANT_LAST_NAME;PARTICIPANT_BIRTH;COMPETITION";
	}
	
	@Override
	public String getTableColumns(){
		return "PARTICIPANT;COMPETITION";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in ParticipationHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in ParticipationHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < PARTICIPANT_FIELDS; ++i){
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
			throw new NullPointerException("Problem in ParticipationHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in ParticipationHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] participant = fields[PARTICIPANT_INDEX].split(FIELD_DELIM);
		if(participant.length < PARTICIPANT_FIELDS){
			throw new RuntimeException("Problem in ParticipationHelper.setSelectingToTable: " + participant.length + " of value in tableValues less than " + PARTICIPANT_FIELDS);
		}
		for(String participantField : participant){
			selectingValues.add(participantField);
		}
		selectingValues.add(fields[OTHER_INDEX]);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in ParticipationHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in ParticipationHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SELECTING_FIELDS = 4;
	private int TABLE_FIELDS = 2;
	private int PARTICIPANT_FIELDS = 3;
	private int PARTICIPANT_INDEX = 0;
	private int OTHER_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_participation.txt";
}