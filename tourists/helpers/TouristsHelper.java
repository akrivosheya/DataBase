package tourists.helpers;

import java.util.*;
import java.io.*;

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
				fields.forEach((String attribute, String value)->{
					if(!value.equals("")){
						switch(attribute){
							case "Section":
								query.append("SECTIONS.NAME='" + value + "' AND\n");
								break;
							case "Group":
								query.append("SPORTSMEN.GROUP_ID=" + value + " AND\n");
								break;
							case "Sex":
								query.append("TOURISTS.SEX='" + value + "' AND\n");
								break;
							case "Age":
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM TOURISTS.BIRTH)=" + value + " AND\n");
								break;
							case "Birth":
								query.append("TOURISTS.BIRTH='" + value + "' AND\n");
								break;
							case "Hike":
								query.append("HIKE.ID=" + value + " AND\n");
								break;
							case "Hike count":
								//query.append("(SELECT COUNT(*) FROM HIKE, WENT_TO_HIKE, TOURISTS WHERE HIKE.ID=");
								break;
							case "Route":
								//query.append("(SELECT COUNT(*) FROM HIKE, WENT_TO_HIKE, TOURISTS WHERE HIKE.ID=");
								break;
							case "Point":
								//query.append("(SELECT COUNT(*) FROM HIKE, WENT_TO_HIKE, TOURISTS WHERE HIKE.ID=");
								break;
							case "Category":
								//query.append("(SELECT COUNT(*) FROM HIKE, WENT_TO_HIKE, TOURISTS WHERE HIKE.ID=");
								break;
							case "Can go to hike":
								//query.append("(SELECT COUNT(*) FROM HIKE, WENT_TO_HIKE, TOURISTS WHERE HIKE.ID=");
								break;
						}
					}
				});
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
							//query.append("SECTIONS.ID=");
						case "Is sportsman":
							query.append("TOURISTS.TYPE='SPORTSMAN'");
						case "Instructor is coach":
							//query.append("TOURISTS.SEX=");
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
		query.append(",");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "'");
		}
		query.append(",");
		if(values.containsKey("SEX")){
			query.append("'" + values.get("SEX") + "'");
		}
		query.append(",");
		if(values.containsKey("BIRTH")){
			query.append("'" + values.get("BIRTH") + "'");
		}
		query.append(",");
		if(values.containsKey("CATEGORY")){
			query.append(values.get("CATEGORY"));
		}
		query.append(",");
		if(values.containsKey("TYPE")){
			query.append("'" + values.get("TYPE") + "'");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE TOURISTS SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "SEX":
				case "BIRTH":
				case "TYPE":
					query.append(attribute + "='" + value + "',");
					break;
				case "CATEGORY":
					query.append(attribute + "=" + value + ",");
					break;
			}
		});
		query.deleteCharAt(query.length() - 1);
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "SEX":
				case "BIRTH":
				case "TYPE":
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
				case "TYPE":
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
	public int getColumnsSize(){
		return columnsSize;
	}
	
	@Override
	public String getColumns(){
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY;TYPE";
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
	
	private int columnsSize = 7;
	private String SELECT_FILE = "SQL_select_tourists.txt";
}