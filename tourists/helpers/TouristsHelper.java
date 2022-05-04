package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class TouristsHelper implements QueryHelper{
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
							case "Group":
								query.append("SPORTSMEN.GROUP_ID=" + entry.getValue() + " AND\n");
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
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM TOURISTS.BIRTH)=" + entry.getValue() + " AND\n");
								break;
							case "Birth":
								if(!StringMaster.isDate(entry.getValue())){
									System.err.println(entry.getValue() + " is not a date");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
								break;
							case "Hike":
								query.append("HIKE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Hikes count":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
									return null;
								}
								query.append("TOURISTS_HIKES_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Route":
								query.append("ROUTE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Point":
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Category":
								if(!StringMaster.isNumber(entry.getValue())){
									System.err.println(entry.getValue() + " is not a number");
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
						case "Instructor is coach":
							query.append("TRAINS.COACH = CONDUCTED_HIKE.INSTRUCTOR");
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
	public String getInsertingQuery(Map<String, String> values){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO TOURISTS VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("SEX")){
			String sex = values.get("SEX");
			if(!StringMaster.isSex(sex)){
				System.err.println(sex + " is not a sex");
				return null;
			}
			query.append("'" + sex + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				System.err.println(birth + " is not a date");
				return null;
			}
			query.append("'" + birth + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				System.err.println(category + " is not a number");
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
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE TOURISTS SET ");
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("LAST_NAME=");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("SEX=");
		if(values.containsKey("SEX")){
			String sex = values.get("SEX");
			if(!StringMaster.isSex(sex)){
				System.err.println(sex + " is not a sex");
				return null;
			}
			query.append("'" + sex + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("BIRTH=");
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				System.err.println(birth + " is not a date");
				return null;
			}
			query.append("'" + birth + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("CATEGORY=");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				System.err.println(category + " is not a number");
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
	public String getColumns(){
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY";
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
	
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_tourists.txt";
}