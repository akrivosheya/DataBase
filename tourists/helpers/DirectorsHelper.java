package tourists.helpers;

import java.util.*;
import java.io.*;

public class DirectorsHelper implements QueryHelper{
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
							case "Salary":
								query.append("DIRECTORS.SALARY=" + value + " AND\n");
								break;
							case "Birth":
								query.append("DIRECTORS.BIRTH='" + value + "' AND\n");
								break;
							case "Age":
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM DIRECTORS.BIRTH)=" + value + " AND\n");
								break;
							case "Admission":
								query.append("DIRECTORS.ADMISSION='" + value + "' AND\n");
								break;
						}
					}
				});
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
		StringBuilder query = new StringBuilder("INSERT INTO DIRECTORS VALUES(");
		query.append("1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		query.append(",");
		if(values.containsKey("LAST_NAME")){
			query.append("'" + values.get("LAST_NAME") + "'");
		}
		query.append(",");
		if(values.containsKey("BIRTH")){
			query.append("'" + values.get("BIRTH") + "'");
		}
		query.append(",");
		if(values.containsKey("ADMISSION")){
			query.append("'" + values.get("ADMISSION") + "'");
		}
		query.append(",");
		if(values.containsKey("SALARY")){
			query.append(values.get("SALARY"));
		}
		query.append(",");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE DIRECTORS SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append(attribute + "=(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + value + "'),");
					break;
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
				case "ADMISSION":
					query.append(attribute + "='" + value + "',");
					break;
				case "SALARY":
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
				case "BIRTH":
				case "ADMISSION":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "SALARY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder(
		"DELETE FROM DIRECTORS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append(attribute + "=(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + value + "') AND ");
					break;
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
				case "ADMISSION":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "SALARY":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getColumns(){
		return "NAME;LAST_NAME;BIRTH;ADMISSION;SALARY;SECTION";
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
	
	private String SELECT_FILE = "SQL_select_directors.txt";
}