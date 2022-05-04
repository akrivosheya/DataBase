package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class DirectorsHelper implements QueryHelper{
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
							case "Salary":
								if(StringMaster.isNull(entry.getValue())){
									query.append("DIRECTORS.SALARY IS NULL AND\n");
									break;
								}
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("DIRECTORS.SALARY=" + entry.getValue() + " AND\n");
								break;
							case "Birth":
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("DIRECTORS.BIRTH='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
								break;
							case "Age":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM DIRECTORS.BIRTH)=" + entry.getValue() + " AND\n");
								break;
							case "Admission":
								if(StringMaster.isNull(entry.getValue())){
									query.append("DIRECTORS.ADMISSION IS NULL AND\n");
									break;
								}
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("DIRECTORS.ADMISSION='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
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
		StringBuilder query = new StringBuilder("INSERT INTO DIRECTORS VALUES(");
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
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + birth.substring(0, DATE_LENGTH) + "'");
		}
		else{
			message.append("You have to enter birth");
			return null;
		}
		query.append(",");
		if(values.containsKey("ADMISSION")){
			String admission = values.get("ADMISSION");
			if(!StringMaster.isDate(admission)){
				message.append(admission + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + admission.substring(0, DATE_LENGTH) + "'");
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
		query.append(",");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE DIRECTORS SET ");
		query.append("SECTION=");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "'),");
		}
		else{
			query.append("NULL,");
		}
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
		query.append("BIRTH=");
		if(values.containsKey("BIRTH")){
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				message.append(birth + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + birth.substring(0, DATE_LENGTH) + "',");
		}
		else{
			message.append("You have to enter birth");
			return null;
		}
		query.append("ADMISSION=");
		if(values.containsKey("ADMISSION")){
			String admission = values.get("ADMISSION");
			if(!StringMaster.isDate(admission)){
				message.append(admission + " is not a date. Date format: dd.mm.yyyy");
				return null;
			}
			query.append("'" + admission.substring(0, DATE_LENGTH) + "',");
		}
		else{
			query.append("NULL,");
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
	
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_directors.txt";
}