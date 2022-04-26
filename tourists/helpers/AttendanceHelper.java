package tourists.helpers;

import java.util.*;
import java.io.*;

public class AttendanceHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO ATTENDANCE VALUES(");
		if(values.containsKey("SPORTSMAN_NAME") && values.containsKey("SPORTSMAN_LAST_NAME") && values.containsKey("SPORTSMAN_BIRTH")){
			query.append("(SELECT ID FROM SPORTSMEN WHERE NAME='" + values.get("SPORTSMAN_NAME") + "' AND ");
			query.append("LAST_NAME='" + values.get("SPORTSMAN_LAST_NAME") + "' AND ");
			query.append("BIRTH='" + values.get("SPORTSMAN_BIRTH") + "')");
		}
		query.append(",");
		if(values.containsKey("TRAINING")){
			query.append("(SELECT ID FROM TRAININGS WHERE NAME='" + values.get("TRAINING") + "')");
		}
		query.append(",");
		if(values.containsKey("TIME")){
			query.append("'" + values.get("TIME") + "'");
		}
		query.append(",");
		if(values.containsKey("VISITED")){
			query.append(values.get("VISITED"));
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE ATTENDANCE SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "TRAINING":
					query.append(attribute + "=(SELECT ID FROM TRAININGS WHERE NAME='" + value + "'),");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "',");
					break;
				case "VISITED":
					query.append(attribute + "=" + value + ",");
					break;
			}
		});
		query.append("SPORTSMAN=(SELECT ID FROM TOURISTS WHERE ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "SPORTSMAN_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "TRAINING":
					query.append(attribute + "=(SELECT ID FROM TRAININGS WHERE NAME='" + value + "') AND ");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "VISITED":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		query.append("SPORTSMAN=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "SPORTSMAN_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM ATTENDANCE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "TRAINING":
					query.append(attribute + "=(SELECT ID FROM TRAININGS WHERE NAME='" + value + "') AND ");
					break;
				case "TIME":
					query.append(attribute + "='" + value + "' AND ");
					break;
				case "VISITED":
					query.append(attribute + "=" + value + " AND ");
					break;
			}
		});
		query.append("SPORTSMAN=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SPORTSMAN_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "SPORTSMAN_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "SPORTSMAN_NAME;SPORTSMAN_LAST_NAME;SPORTSMAN_BIRTH;TRAINING;TIME;VISITED";
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
	
	private String SELECT_FILE = "SQL_select_attendance.txt";
}