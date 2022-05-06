package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class CoachesHelper implements QueryHelper{
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
		if((fields != null && fields.size() > 0) || (flags != null && flags.size() > 0)){
			query.append(" WHERE ");
			if(fields != null){
				for(Map.Entry<String, String> entry : fields.entrySet()){
					if(!entry.getValue().equals("")){
						switch(entry.getKey()){
							case "Section":
								if(StringMaster.isNull(entry.getValue())){
									query.append("SECTIONS.ID IS NULL AND\n");
									break;
								}
								query.append("SECTIONS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Sex":
								if(entry.getValue().isBlank()){
									query.append("(TOURISTS.SEX='M' OR TOURISTS.SEX='W') AND\n");
								}
								else{
									query.append("TOURISTS.SEX='" + entry.getValue() + "' AND\n");
								}
								break;
							case "Age":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM TOURISTS.BIRTH)=" + entry.getValue().substring(0, DATE_LENGTH) + " AND\n");
								break;
							case "Salary":
								if(StringMaster.isNull(entry.getValue())){
									query.append("COACHES.SALARY IS NULL AND\n");
									break;
								}
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								query.append("COACHES.SALARY=" + entry.getValue() + " AND\n");
								break;
							case "Specialization":
								query.append("COACHES.SPECIALIZATION='" + entry.getValue() + "' AND\n");
								break;
							case "Group":
								if(StringMaster.isNull(entry.getValue())){
									query.append("GROUPS.ID IS NULL AND\n");
									break;
								}
								query.append("GROUPS.ID=" + entry.getValue() + " AND\n");
								break;
							case "Trains after hour":
								if(!StringMaster.isHour(entry.getValue())){
									message.append(entry.getValue() + " is not a hour: Hour format: hh:mm");
									return null;
								}
								query.append("TRAININGS.ENDING_HOUR>" + StringMaster.getHour(entry.getValue()) + " AND\n");
								break;
							case "Trains before hour":
								if(!StringMaster.isHour(entry.getValue())){
									message.append(entry.getValue() + " is not a hour: Hour format: hh:mm");
									return null;
								}
								query.append("TRAININGS.BEGINNING_HOUR<" + StringMaster.getHour(entry.getValue()) + " AND\n");
								break;
							case "Trains after day":
								if(entry.getValue().isBlank()){
									query.append("TRAININGS.DAY>=1 AND\n");
									continue;
								}
								if(!StringMaster.isWeekDay(entry.getValue())){
									message.append(entry.getValue() + " is not a week day: Week day is: " + StringMaster.getWeekDays());
									return null;
								}
								query.append("TRAININGS.DAY>=" + StringMaster.getDayFromWeekDay(entry.getValue()) + " AND\n");
								break;
							case "Trains before day":
								if(entry.getValue().isBlank()){
									query.append("TRAININGS.DAY<=7 AND\n");
									continue;
								}
								if(!StringMaster.isWeekDay(entry.getValue())){
									message.append(entry.getValue() + " is not a week day: Week day is: " + StringMaster.getWeekDays());
									return null;
								}
								query.append("TRAININGS.DAY<=" + StringMaster.getDayFromWeekDay(entry.getValue()) + " AND\n");
								break;
						}
					}
				}
			}
			return query.substring(0, query.length() - " AND\n".length());
		}
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO COACHES VALUES(");
		if(values.containsKey("NAME") || values.containsKey("LAST_NAME")
			|| values.containsKey("BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("NAME");
			String lastName = values.get("LAST_NAME");
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format dd.mm.yyyy");
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
		if(values.containsKey("SPECIALIZATION")){
			query.append("'" + values.get("SPECIALIZATION") + "'");
		}
		else{
			message.append("You have to enter specialization");
			return null;
		}
		query.append(",");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("SALARY")){
			String salary = values.get("SALARY");
			if(!StringMaster.isNumber(salary)){
				message.append(salary + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(salary);
		}
		else{
			query.append("NULL");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null || fields.size() <= 2){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE COACHES SET ");
		query.append("SECTION=");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "'),");
		}
		else{
			query.append("NULL,");
		}
		query.append("SPECIALIZATION=");
		if(values.containsKey("SPECIALIZATION")){
			query.append("'" + values.get("SPECIALIZATION") + "',");
		}
		else{
			message.append("You have to enter specialization");
			return null;
		}
		query.append("SALARY=");
		if(values.containsKey("SALARY")){
			String salary = values.get("SALARY");
			if(!StringMaster.isNumber(salary)){
				message.append(salary + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(salary);
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
		"DELETE FROM COACHES WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		if(query.length() <= ("DELETE FROM COACHES WHERE ID=(SELECT TOURISTS.ID FROM TOURISTS WHERE ").length()){
			return null;
		}
		query.setCharAt(query.length() - (" AND ").length(), ')');
		return query.substring(0, query.length() - "AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "NAME;LAST_NAME;BIRTH;SPECIALIZATION;SECTION;SALARY";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "SPECIALIZATION;SECTION;SALARY";
	}
	
	@Override
	public String getTableColumns(){
		return "COACH;SPECIALIZATION;SECTION;SALARY";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in CoachesHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in CoachesHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < COACH_FIELDS; ++i){
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
			throw new NullPointerException("Problem in CoachesHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in CoachesHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] coach = fields[COACH_INDEX].split(FIELD_DELIM);
		if(coach.length < COACH_FIELDS){
			throw new RuntimeException("Problem in CoachesHelper.setSelectingToTable: " + coach.length + " of value in tableValues less than " + COACH_FIELDS);
		}
		for(String coachField : coach){
			selectingValues.add(coachField);
		}
		for(int i = OTHER_INDEX; i < TABLE_FIELDS; ++i){
			selectingValues.add(fields[i]);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in CoachesHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in CoachesHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
		}
		return selectingValues.subList(COACH_FIELDS, selectingValues.size());
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
	private int TABLE_FIELDS = 4;
	private int COACH_FIELDS = 3;
	private int COACH_INDEX = 0;
	private int OTHER_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_coaches.txt";
}