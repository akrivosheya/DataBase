package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TouristsHelper implements QueryHelper{
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
							case "Group":
								if(StringMaster.isNull(entry.getValue())){
									query.append("GROUPS.ID IS NULL AND\n");
									break;
								}
								query.append("GROUPS.NAME=" + entry.getValue() + " AND\n");
								break;
							case "Sex":
								if(!entry.getValue().isBlank()){
									query.append("TOURISTS.SEX='" + entry.getValue() + "' AND\n");
								}
								else{
									query.append("(TOURISTS.SEX='M' OR TOURISTS.SEX='W') AND\n");
								}
								break;
							case "Age":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM TOURISTS.BIRTH)=" + entry.getValue() + " AND\n");
								break;
							case "Birth":
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
								break;
							case "Hike":
								if(StringMaster.isNull(entry.getValue())){
									query.append("HIKE.ID IS NULL AND\n");
									break;
								}
								query.append("HIKE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Hikes count":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("TOURISTS_HIKES_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Route":
								if(StringMaster.isNull(entry.getValue())){
									query.append("ROUTE.ID IS NULL AND\n");
									break;
								}
								query.append("ROUTE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Point":
								if(StringMaster.isNull(entry.getValue())){
									query.append("PLACE.ID IS NULL AND\n");
									break;
								}
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Category":
								if(StringMaster.isNull(entry.getValue())){
									query.append("TOURISTS.CATEGORY IS NULL AND\n");
									break;
								}
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("TOURISTS.CATEGORY=" + entry.getValue() + " AND\n");
								break;
							case "Can go to hike":
								getConditionToHikeRequirement(query, entry.getValue());
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
						case "All routes":
							query.append("TOURISTS_ROUTES_COUNT.COUNT = (SELECT COUNT(*) FROM ROUTE)");
							break;
						case "Is sportsman":
							query.append("TOURISTS.TYPE='SPORTSMAN'");
							break;
						case "Instructor is tourist":
							query.append("TRAINS.tourist = CONDUCTED_HIKE.INSTRUCTOR");
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
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO TOURISTS VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append(",");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "'");
		}
		else{
			message.append("You have to enter last name");
			return null;
		}
		query.append(",");
		if(values.containsKey("SEX")){
			String sex = values.get("SEX");
			if(!StringMaster.isSex(sex)){
				message.append(sex + " is not a sex. Sex is M or W");
				return null;
			}
			query.append("'" + sex + "'");
		}
		else{
			message.append("You have to enter sex");
			return null;
		}
		query.append(",");
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + birth + "'");
		}
		else{
			message.append("You have to enter birth");
			return null;
		}
		query.append(",");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				message.append(category + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(category);
		}
		else{
			query.append("NULL");
		}
		query.append(",'AMATEUR')");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE TOURISTS SET ");
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append("LAST_NAME=");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "',");
		}
		else{
			message.append("You have to enter last name");
			return null;
		}
		query.append("SEX=");
		if(values.containsKey("SEX")){
			String sex = values.get("SEX");
			if(!StringMaster.isSex(sex)){
				message.append(sex + " is not a sex. Sex is M or W");
				return null;
			}
			query.append("'" + sex + "',");
		}
		else{
			message.append("You have to enter sex");
			return null;
		}
		query.append("BIRTH=");
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + birth + "',");
		}
		else{
			message.append("You have to enter birth");
			return null;
		}
		query.append("CATEGORY=");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				message.append(category + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(category);
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "SEX":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "CATEGORY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "SEX":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "CATEGORY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY";
	}
	
	@Override
	public String getTableColumns(){
		return "TOURIST;SEX;BIRTH;CATEGORY";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in TouristsHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		for(String value : selectingValues){
			String[] fields = value.split(TABLE_DELIM);
			if(fields.length < SELECTING_FIELDS){
				throw new RuntimeException("Problem in TouristsHelper.setSelectingToTable: not enough parametres in values");
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
			throw new NullPointerException("Problem in TouristsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		String[] fields = new String[0];
		fields = tableValues.toArray(fields);
		if(fields.length < TABLE_FIELDS){
			throw new RuntimeException("Problem in TouristsHelper.setSelectingToTable: " + fields.length + " of value in tableValues less than " + TABLE_FIELDS);
		}
		String[] tourist = fields[TOURIST_INDEX].split(FIELD_DELIM);
		if(tourist.length < TOURIST_FIELDS){
			throw new RuntimeException("Problem in TouristsHelper.setSelectingToTable: " + tourist.length + " of value in tableValues less than " + TOURIST_FIELDS);
		}
		for(String touristField : tourist){
			selectingValues.add(touristField);
		}
		for(int i = OTHER_INDEX; i < TABLE_FIELDS; ++i){
			selectingValues.add(fields[i]);
		}
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in TouristsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in TouristsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private String getConditionToHikeRequirement(StringBuilder query, String value){
		if(value == null){
			throw new NullPointerException("Problem in TouristsHelper.getConditionToHikeRequirement: value is null");
		}
		if(query == null){
			throw new NullPointerException("Problem in TouristsHelper.getConditionToHikeRequirement: query is null");
		}
		query.append("((SECTIONS.NAME=(SELECT HIKE.REQUIREMENT FROM HIKE WHERE HIKE.NAME='");
		query.append(value);
		query.append("')AND(SELECT COUNT(HIKE.ID) FROM HIKE WHERE HIKE.NAME='");
		query.append(value);
		query.append("')=1)OR(((SELECT HIKE.REQUIREMENT FROM HIKE WHERE HIKE.NAME='");
		query.append(value);
		query.append("') IS NULL)AND(SELECT COUNT(HIKE.ID) FROM HIKE WHERE HIKE.NAME='");
		query.append(value);
		query.append("')=1)) AND\n");
		return query.toString();
	}
	
	private int SELECTING_FIELDS = 5;
	private int TABLE_FIELDS = 4;
	private int TOURIST_FIELDS = 2;
	private int TOURIST_INDEX = 0;
	private int OTHER_INDEX = 1;
	private String TABLE_DELIM = ";";
	private String FIELD_DELIM = ", ";
	private String FIELD_REPLACE = "_";
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_tourists.txt";
}