package tourists.helpers;

import java.util.*;
import java.io.*;

public class WentToHikeHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO WENT_TO_HIKE VALUES(");
		if(values.containsKey("HIKE")){
			query.append("(SELECT ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND NAME='" + values.get("HIKE") + "')");
		}
		query.append(",");
		if(values.containsKey("TOURIST_NAME") && values.containsKey("TOURIST_LAST_NAME") && values.containsKey("TOURIST_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE NAME='" + values.get("TOURIST_NAME") + "' AND ");
			query.append("LAST_NAME='" + values.get("TOURIST_LAST_NAME") + "' AND ");
			query.append("BIRTH='" + values.get("TOURIST_BIRTH") + "')");
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE WENT_TO_HIKE SET ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND NAME='" + value + "'),");
					break;
			}
		});
		query.append("TOURIST=(SELECT ID FROM TOURISTS WHERE ");
		values.forEach((String attribute, String value)->{
			switch(attribute){
				case "TOURIST_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "TOURIST_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "TOURIST_BIRTH":
					query.append("BIRTH='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(")");
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("TOURIST=(SELECT ID FROM TOURISTS WHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "TOURIST_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "TOURIST_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "TOURIST_BIRTH":
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
		StringBuilder query = new StringBuilder("DELETE FROM WENT_TO_HIKE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append(attribute + "=(SELECT ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND NAME='" + value + "') AND ");
					break;
			}
		});
		query.append("TOURIST=(SELECT ID FROM TOURISTS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "TOURIST_NAME":
					query.append("NAME='" + value + "' AND ");
					break;
				case "TOURIST_LAST_NAME":
					query.append("LAST_NAME='" + value + "' AND ");
					break;
				case "TOURIST_BIRTH":
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
		return "TOURIST_NAME;TOURIST_LAST_NAME;TOURIST_BIRTH;HIKE";
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
	
	private String SELECT_FILE = "SQL_select_went_to_hike.txt";
}