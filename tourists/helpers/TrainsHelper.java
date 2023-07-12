package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TrainsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO TRAINS VALUES(");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
			message.append("You have to enter coach data");
			return null;
		}
		query.append(",");
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME='" + values.get("GROUP_NAME") + "')");
		}
		else{
			message.append("You have to enter group name");
			return null;
		}
		query.append(",");
		if(values.containsKey("TRAINING") && values.containsKey("SECTION")){
			query.append("(SELECT TRAININGS.ID FROM TRAININGS, SECTIONS WHERE TRAININGS.SECTION=SECTIONS.ID AND ");
			String training = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("SECTIONS.NAME='" + section + "' AND ");
			query.append("TRAININGS.NAME='" + training + "')");
		}
		else{
			message.append("You have to enter training and section");
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
		StringBuilder query = new StringBuilder("UPDATE TRAINS SET ");
		query.append("GROUP_ID=");
		if(values.containsKey("GROUP_NAME")){
			query.append("(SELECT ID FROM GROUPS WHERE NAME='" + values.get("GROUP_NAME") + "'),");
		}
		else{
			message.append("You have to enter group name");
			return null;
		}
		query.append("TRAINING=");
		if(values.containsKey("TRAINING") && values.containsKey("SECTION")){
			query.append("(SELECT TRAININGS.ID FROM TRAININGS, SECTIONS WHERE TRAININGS.SECTION=SECTIONS.ID AND ");
			String training = values.get("TRAINING");
			String section = values.get("SECTION");
			query.append("SECTIONS.NAME='" + section + "' AND ");
			query.append("TRAININGS.NAME='" + training + "'),");
		}
		else{
			message.append("You have to enter training and section");
			return null;
		}
		query.append("COACH=");
		if(values.containsKey("COACH_NAME") || values.containsKey("COACH_LAST_NAME")
			|| values.containsKey("COACH_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("COACH_NAME");
			String lastName = values.get("COACH_LAST_NAME");
			String birth = values.get("COACH_BIRTH");
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
			message.append("You have to enter coach data");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "GROUP_NAME":
					query.append("GROUP_ID=(SELECT ID FROM GROUPS WHERE NAME='" + value + "') AND ");
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
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
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
				case "GROUP_NAME":
					query.append("GROUP_ID=(SELECT ID FROM GROUPS WHERE NAME='" + value + "') AND ");
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
					query.append(attribute + "=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
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
	public String getSelectingColumns(){
		return "COACH_NAME;COACH_LAST_NAME;COACH_BIRTH;GROUP_NAME;TRAINING;SECTION";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "COACH_NAME;COACH_LAST_NAME;COACH_BIRTH;GROUP_NAME;TRAINING;SECTION";
	}
	
	@Override
	public String getTableColumns(){
		return "COACH;GROUP_NAME;TRAINING";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in TrainsHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in TrainsHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < COACH_FIELDS; ++i){
				row.append(fields[i]);
				row.append(FIELD_DELIM);
			}
			row.delete(row.length() - FIELD_DELIM.length(), row.length());
			row.append(TABLE_DELIM);
				row.append(fields[i++]);
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
			throw new NullPointerException("Problem in TrainsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in TrainsHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] coach = fields[COACH_INDEX].split(FIELD_DELIM);
		if(coach.length < COACH_FIELDS){
			throw new RuntimeException("Problem in TrainsHelper.setSelectingToTable: " + coach.length + " of value in tableValues less than " + COACH_FIELDS);
		}
		for(String coachField : coach){
			selectingValues.add(coachField);
		}
		selectingValues.add(fields[GROUP_INDEX]);
		String[] training = fields[TRAINING_INDEX].split(FIELD_DELIM);
		if(training.length < TRAINING_FIELDS){
			throw new RuntimeException("Problem in TrainsHelper.setSelectingToTable: " + training.length + " of value in tableValues less than " + TRAINING_FIELDS);
		}
		for(String trainingField : training){
			selectingValues.add(trainingField);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in TrainsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in TrainsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private int TABLE_FIELDS = 3;
	private int COACH_FIELDS = 3;
	private int TRAINING_FIELDS = 2;
	private int COACH_INDEX = 0;
	private int GROUP_INDEX = 1;
	private int TRAINING_INDEX = 2;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_trains.txt";
}