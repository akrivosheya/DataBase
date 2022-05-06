package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class CoachesTimeHelper implements QueryHelper{
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
						if(StringMaster.isNull(entry.getValue())){
							message.append("You can't enter null values here");
							return null;
						}
						switch(entry.getKey()){
							case "Section":
								query.append("SECTIONS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Name":
								query.append("TOURISTS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Last name":
								query.append("TOURISTS.LAST_NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Birth":
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
								break;
							case "Training":
								query.append("TRAININGS.NAME='" + entry.getValue() + "' AND\n");
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
			query.delete(query.length() - " AND\n".length(), query.length());
		}
		query.append("GROUP BY TRAININGS.NAME, SECTIONS.NAME");
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		return null;
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		return null;
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		return null;
	}
	
	@Override
	public String getSelectingColumns(){
		return "TRAINING;SECTION;TIME";
	}
	
	@Override
	public String getUpdatingColumns(){
		return null;
	}
	
	@Override
	public String getTableColumns(){
		return "TRAINING;TIME";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in CoachesTimeHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in CoachesTimeHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < TRAINING_FIELDS; ++i){
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
			throw new NullPointerException("Problem in CoachesTimeHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in CoachesTimeHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] training = fields[TRAINING_INDEX].split(FIELD_DELIM);
		if(training.length < TRAINING_FIELDS){
			throw new RuntimeException("Problem in CoachesTimeHelper.setSelectingToTable: " + training.length + " of value in tableValues less than " + TRAINING_FIELDS);
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
			throw new NullPointerException("Problem in CoachesTimeHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in CoachesTimeHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private int TRAINING_FIELDS = 2;
	private int TRAINING_INDEX = 0;
	private int TIME_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_coaches_time.txt";
}