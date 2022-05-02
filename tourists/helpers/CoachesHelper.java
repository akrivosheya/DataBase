package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class CoachesHelper implements QueryHelper{
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
								query.append("GROUPS.ID=" + entry.getValue() + " AND\n");
								break;
							case "Trains after hour":
								query.append("TRAININGS.ENDING_HOUR>" + entry.getValue() + " AND\n");
								break;
							case "Trains before hour":
								query.append("TRAININGS.BEGINNING_HOUR>" + entry.getValue() + " AND\n");
								break;
							case "Trains after day":
								query.append("TRAININGS.DAY>=" + entry.getValue() + " AND\n");
								break;
							case "Trains before day":
								query.append("TRAININGS.DAY<=" + entry.getValue() + " AND\n");
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
		StringBuilder query = new StringBuilder("INSERT INTO COACHES VALUES(");
		if(values.containsKey("NAME") || values.containsKey("LAST_NAME")
			|| values.containsKey("BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("NAME");
			String lastName = values.get("LAST_NAME");
			String birth = values.get("BIRTH");
			if(!StringMaster.isDate(birth)){
				System.err.println(birth + " is not a date");
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
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("SPECIALIZATION")){
			query.append("'" + values.get("SPECIALIZATION") + "'");
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
		query.append(",");
		if(values.containsKey("SALARY")){
			String salary = values.get("SALARY");
			if(!StringMaster.isNumber(salary)){
				System.err.println(salary + " is not a number");
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
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
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
			query.append("NULL,");
		}
		query.append("SALARY=");
		if(values.containsKey("SALARY")){
			String salary = values.get("SALARY");
			if(!StringMaster.isNumber(salary)){
				System.err.println(salary + " is not a number");
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
	public String getColumns(){
		return "NAME;LAST_NAME;BIRTH;SPECIALIZATION;SECTION;SALARY";
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
	private String SELECT_FILE = "SQL_select_coaches.txt";
}