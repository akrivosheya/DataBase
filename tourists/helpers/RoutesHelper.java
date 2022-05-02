package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class RoutesHelper implements QueryHelper{
	@Override
	public String getSelectingQuery(Map<String, String> fields, List<String> flags){
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
									System.err.println(entry.getValue() + " is not a date");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue() + "' AND\n");
								break;
							case "Groups count":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								query.append("ROUTES_GROUPS_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Contain point":
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Has length more than":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								query.append("ROUTE.LENGTH_METRE>=" + entry.getValue() + " AND\n");
								break;
							case "Min category":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
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
	public String getInsertingQuery(Map<String, String> values){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO ROUTE VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("LENGTH_METRE")){
			String length = values.get("LENGTH_METRE");
			if(!StringMaster.isNumber(length)){
				System.out.println(length + " is not a number");
			}
			query.append(length);
		}
		else{
			query.append("NULL");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE ROUTE SET ");
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("LENGTH_METRE=");
		if(values.containsKey("LENGTH_METRE")){
			String length = values.get("LENGTH_METRE");
			if(!StringMaster.isNumber(length)){
				System.out.println(length + " is not a number");
			}
			query.append(length);
		}
		else{
			query.append("NULL");
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
	public String getColumns(){
		return "NAME;LENGTH_METRE";
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
	
	private String SELECT_FILE = "SQL_select_routes.txt";
}