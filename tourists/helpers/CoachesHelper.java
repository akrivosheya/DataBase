package tourists.helpers;

import java.util.*;
import java.io.*;

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
				fields.forEach((String attribute, String value)->{
					if(!value.equals("")){
						switch(attribute){
							case "Section":
								query.append("SECTIONS.NAME='" + value + "' AND\n");
								break;
							case "Sex":
								if(value.isBlank()){
									query.append("(TOURISTS.SEX='M' OR TOURISTS.SEX='W') AND\n");
								}
								else{
									query.append("TOURISTS.SEX='" + value + "' AND\n");
								}
								break;
							case "Age":
								int year = Calendar.getInstance().get(Calendar.YEAR);
								query.append(year + "-EXTRACT(YEAR FROM TOURISTS.BIRTH)=" + value + " AND\n");
								break;
							case "Salary":
								query.append("COACHES.SALARY=" + value + " AND\n");
								break;
							case "Specialization":
								query.append("COACHES.SPECIALIZATION='" + value + "' AND\n");
								break;
							case "Group":
								query.append("GROUPS.ID=" + value + " AND\n");
								break;
							case "Trains after hour":
								query.append("TRAININGS.ENDING_HOUR>" + value + " AND\n");
								break;
							case "Trains before hour":
								query.append("TRAININGS.BEGINNING_HOUR>" + value + " AND\n");
								break;
							case "Trains after day":
								query.append("TRAININGS.DAY>=" + value + " AND\n");
								break;
							case "Trains before day":
								query.append("TRAININGS.DAY<=" + value + " AND\n");
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
		StringBuilder query = new StringBuilder("INSERT INTO COACHES VALUES(");
		query.append("(SELECT TOURISTS.ID FROM TOURISTS WHERE ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
				case "LAST_NAME":
				case "BIRTH":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append("),");
		if(values.containsKey("SPECIALIZATION")){
			query.append("'" + values.get("SPECIALIZATION") + "'");
		}
		query.append(",");
		if(values.containsKey("SECTION")){
			query.append("(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		query.append(",");
		if(values.containsKey("SALARY")){
			query.append(values.get("SALARY"));
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
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append(attribute + "=(SELECT SECTIONS.ID FROM SECTIONS WHERE SECTIONS.NAME='" + value + "'),");
					break;
				case "SPECIALIZATION":
					query.append(attribute + "='" + value + "',");
					break;
				case "SALARY":
					query.append(attribute + "=" + value + ",");
					break;
			}
		});
		query.deleteCharAt(query.length() - 1);
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
	
	private String SELECT_FILE = "SQL_select_coaches.txt";
}