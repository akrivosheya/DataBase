package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class RoutesHelper implements QueryHelper{
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
							case "After day":
								query.append("CONDUCTED_HIKE.TIME>='" + entry.getValue() + "' AND\n");
								break;
							case "Before day":
								query.append("CONDUCTED_HIKE.TIME<='" + entry.getValue() + "' AND\n");
								break;
							case "Instructor name":
								query.append("TOURISTS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Instructor last name":
								query.append("TOURISTS.LAST_NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Instructor birth":
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue() + "' AND\n");
								break;
							case "Groups count":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("ROUTES_GROUPS_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Contain point":
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Has length more than":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("ROUTE.LENGTH_METRE>=" + entry.getValue() + " AND\n");
								break;
							case "Min category":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("HIKE.CATEGORY<=" + entry.getValue() + " AND\n");
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
		StringBuilder query = new StringBuilder("INSERT INTO ROUTE VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append(",");
		if(values.containsKey("LENGTH_METRE")){
			String length = values.get("LENGTH_METRE");
			if(!StringMaster.isNumber(length)){
				message.append(length + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(length);
		}
		else{
			message.append("You have to enter length");
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
		StringBuilder query = new StringBuilder("UPDATE ROUTE SET ");
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append("LENGTH_METRE=");
		if(values.containsKey("LENGTH_METRE")){
			String length = values.get("LENGTH_METRE");
			if(!StringMaster.isNumber(length)){
				message.append(length + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(length);
		}
		else{
			message.append("You have to enter length");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "LENGTH_METRE":
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
		StringBuilder query = new StringBuilder("DELETE FROM ROUTE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "LENGTH_METRE":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "NAME;LENGTH_METRE";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "NAME;LENGTH_METRE";
	}
	
	@Override
	public String getTableColumns(){
		return "NAME;LENGTH_METRE";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in RoutesHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		if(!tableValues.addAll(selectingValues)){
			return false;
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in sRoutesHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		selectingValues.addAll(tableValues);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in RoutesHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in RoutesHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SELECTING_FIELDS = 2;
	private String SELECT_FILE = "SQL_select_routes.txt";
}