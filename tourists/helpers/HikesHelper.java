package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class HikesHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO HIKE VALUES(1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("REQUIREMENT")){
			query.append("'" + values.get("REQUIREMENT") + "'");
		}
		else{
			query.append("NULL");
		}
		query.append(",");
		if(values.containsKey("DAYS")){
			String days = values.get("DAYS");
			if(!StringMaster.isNumber(days)){
				System.err.println(days + " is not a number");
				return null;
			}
			query.append(days);
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
		query.append(",");
		if(values.containsKey("HAS_PLAN")){
			String hasPlan = values.get("HAS_PLAN");
			if(!StringMaster.isNumber(hasPlan)){
				System.err.println(hasPlan + " is not a number");
				return null;
			}
			query.append(hasPlan);
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
		StringBuilder query = new StringBuilder("UPDATE HIKE SET ");
		query.append("HAS_PLAN=");
		if(values.containsKey("HAS_PLAN")){
			String hasPlan = values.get("HAS_PLAN");
			if(!StringMaster.isFlag(hasPlan)){
				System.err.println(hasPlan + " is not a flag");
				return null;
			}
			query.append(hasPlan + ",");
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
			query.append(category + ",");
		}
		else{
			query.append("NULL,");
		}
		query.append("DAYS=");
		if(values.containsKey("DAYS")){
			String days = values.get("DAYS");
			if(!StringMaster.isNumber(days)){
				System.err.println(days + " is not a number");
				return null;
			}
			query.append(days + ",");
		}
		else{
			query.append("NULL,");
		}
		query.append("REQUIREMENT=");
		if(values.containsKey("REQUIREMENT")){
			query.append("'" + values.get("REQUIREMENT") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "',");
		}
		else{
			query.append("NULL,");
		}
		query.append("ROUTE=");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			query.append("NULL");
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "'");
					break;
			}
		});
		return query.toString();
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		if(params == null || params.size() == 0){
			return null;
		}
		StringBuilder query = new StringBuilder("DELETE FROM HIKE WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "'");
					break;
			}
		});
		return query.toString();
	}
	
	@Override
	public String getColumns(){
		return "NAME;ROUTE;REQUIREMENT;DAYS;CATEGORY;HAS_PLAN";
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
	
	private String SELECT_FILE = "SQL_select_hikes.txt";
}