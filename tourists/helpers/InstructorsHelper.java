package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class InstructorsHelper implements QueryHelper{
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
							case "Category":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("TOURISTS.CATEGORY=" + entry.getValue() + " AND\n");
								break;
							case "Hike":
								query.append("HIKE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Hikes count":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("INSTRUCTORS_HIKES_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Route":
								query.append("ROUTE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Point":
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
						}
					}
				}
			}
			if(flags != null){
				Iterator<String> iteratorFlag = flags.iterator();
				while(iteratorFlag.hasNext()){
					String flag = iteratorFlag.next();
					if(flag == null){
						return null;
					}
					switch(flag){
						case "Is sportsman":
							query.append("TOURISTS.TYPE='SPORTSMAN'");
							break;
						case "Is coach":
							query.append("TOURISTS.TYPE='COACH'");
							break;
					}
					query.append(" AND\n");
				}
			}
			return query.substring(0, query.length() - " AND\n".length());
		}
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
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY";
	}
	
	@Override
	public String getUpdatingColumns(){
		return null;
	}
	
	@Override
	public String getTableColumns(){
		return "INSTRUCTOR;CATEGORY";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in InstructorHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in InstructorHelper.setSelectingToTable: not enough parametres in values");
			}
			int i = 0;
			for(; i < INSTRUCTOR_FIELDS; ++i){
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
			throw new NullPointerException("Problem in InstructorHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in InstructorHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] instructor = fields[INSTRUCTOR_INDEX].split(FIELD_DELIM);
		if(instructor.length < INSTRUCTOR_FIELDS){
			throw new RuntimeException("Problem in InstructorHelper.setSelectingToTable: " + instructor.length + " of value in tableValues less than " + INSTRUCTOR_FIELDS);
		}
		for(String instructorField : instructor){
			selectingValues.add(instructorField);
		}
		selectingValues.add(fields[CATEGORY_INDEX]);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in InstructorHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in InstructorHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private int INSTRUCTOR_FIELDS = 4;
	private int INSTRUCTOR_INDEX = 0;
	private int CATEGORY_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private String SELECT_FILE = "SQL_select_instructors.txt";
}