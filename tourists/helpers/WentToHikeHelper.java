package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class WentToHikeHelper implements QueryHelper{
	@Override
	public String getSelectingQuery(Map<String, String> fields, List<String> flags){
		File file = new File(SELECT_FILE);
		if(!file.exists()){
		System.out.println("NO FILE");
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
		System.out.println(query.toString());
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO WENT_TO_HIKE VALUES(");
		
		if(values.containsKey("HIKE") || values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
			String name = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				System.err.println(time + " is not a date");
				return null;
			}
			if(name != null){
				query.append("HIKE.NAME='" + name + "' AND ");
			}
			if(time != null){
				query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "' AND ");
			}
			query.delete(query.length() - " AND ".length(), query.length());
			query.append(")");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("TOURIST_NAME") || values.containsKey("TOURIST_LAST_NAME")
			|| values.containsKey("TOURIST_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("TOURIST_NAME");
			String lastName = values.get("TOURIST_LAST_NAME");
			String birth = values.get("TOURIST_BIRTH");
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
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE WENT_TO_HIKE SET ");
		query.append("HIKE=");
		if(values.containsKey("HIKE") || values.containsKey("TIME")){
			query.append("(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
			String name = values.get("HIKE");
			String time = values.get("TIME");
			if(!StringMaster.isDate(time)){
				System.err.println(time + " is not a date");
				return null;
			}
			if(name != null){
				query.append("HIKE.NAME='" + name + "' AND ");
			}
			if(time != null){
				query.append("CONDUCTED_HIKE.TIME='" + time.substring(0, DATE_LENGTH) + "' AND ");
			}
			query.delete(query.length() - " AND ".length(), query.length());
			query.append("),");
		}
		else{
			query.append("NULL,");
		}
		query.append("TOURIST=");
		if(values.containsKey("TOURIST_NAME") || values.containsKey("TOURIST_LAST_NAME")
			|| values.containsKey("TOURIST_BIRTH")){
			query.append("(SELECT ID FROM TOURISTS WHERE ");
			String name = values.get("TOURIST_NAME");
			String lastName = values.get("TOURIST_LAST_NAME");
			String birth = values.get("TOURIST_BIRTH");
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
		query.append("\nWHERE ");
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
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
		query.append("HIKE=(SELECT CONDUCTED_HIKE.ID FROM CONDUCTED_HIKE, HIKE WHERE CONDUCTED_HIKE.HIKE = HIKE.ID AND ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "HIKE":
					query.append("HIKE.NAME='" + value + "' AND ");
					break;
				case "TIME":
					query.append("CONDUCTED_HIKE.TIME='" + value + "' AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		query.append(") AND ");
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
		return "TOURIST_NAME;TOURIST_LAST_NAME;TOURIST_BIRTH;HIKE;TIME";
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
	private String SELECT_FILE = "SQL_select_went_to_hike.txt";
}