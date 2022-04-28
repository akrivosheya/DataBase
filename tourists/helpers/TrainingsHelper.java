package tourists.helpers;

import java.util.*;
import java.io.*;

public class TrainingsHelper implements QueryHelper{
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
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO TRAININGS VALUES(");
		query.append("1,");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE NAME='" + values.get("SECTION") + "')");
		}
		query.append(",");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		query.append(",");
		if(values.containsKey("DAY")){
			query.append(values.get("DAY"));
		}
		query.append(",");
		if(values.containsKey("BEGINNING_HOUR")){
			query.append(values.get("BEGINNING_HOUR"));
		}
		query.append(",");
		if(values.containsKey("ENDING_HOUR")){
			query.append(values.get("ENDING_HOUR"));
		}
		query.append(",");
		if(values.containsKey("PLACE")){
			query.append("'" + values.get("PLACE") + "'");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE TRAININGS SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("TRAININGS.SECTION=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "'),");
					break;
				case "NAME":
				case "PLACE":
					query.append(attribute + "='" + value + "',");
					break;
				case "DAY":
				case "BEFINNING_HOUR":
				case "ENDING_HOUR":
					query.append(attribute + "=" + value + ",");
					break;
			}
		});
		query.deleteCharAt(query.length() - 1);
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("TRAININGS.SECTION=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
				case "NAME":
				case "PLACE":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "DAY":
				case "BEFINNING_HOUR":
				case "ENDING_HOUR":
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
		StringBuilder query = new StringBuilder("DELETE FROM TRAININGS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("TRAININGS.SECTION=(SELECT ID FROM SECTIONS WHERE NAME='" + value + "') AND ");
					break;
				case "NAME":
				case "PLACE":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "DAY":
				case "BEFINNING_HOUR":
				case "ENDING_HOUR":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getColumns(){
		return "SECTION;NAME;DAY;BEGINNING_HOUR;ENDING_HOUR;PLACE";
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
	
	private String SELECT_FILE = "SQL_select_trainings.txt";
}