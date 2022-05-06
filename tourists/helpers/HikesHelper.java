package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class HikesHelper implements QueryHelper{
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
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		if(values == null){
			return null;
		}
		StringBuilder query = new StringBuilder("INSERT INTO HIKE VALUES(1,");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			message.append("You have ot enter name");
			return null;
		}
		query.append(",");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			message.append("You have ot enter route");
			return null;
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
				message.append(days + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(days);
		}
		else{
			message.append("You have ot enter days");
			return null;
		}
		query.append(",");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				message.append(category + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(category);
		}
		else{
			message.append("You have ot enter category");
			return null;
		}
		query.append(",");
		if(values.containsKey("HAS_PLAN")){
			String hasPlan = values.get("HAS_PLAN");
			if(!StringMaster.isFlag(hasPlan)){
				message.append(hasPlan + " is not a flag. Flag is 0 or 1");
				return null;
			}
			query.append(hasPlan);
		}
		else{
			message.append("You have ot enter has plan");
			return null;
		}
		query.append(")");
		return query.toString();
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
		if(values == null || fields == null){
			return null;
		}
		StringBuilder query = new StringBuilder("UPDATE HIKE SET ");
		query.append("HAS_PLAN=");
		if(values.containsKey("HAS_PLAN")){
			String hasPlan = values.get("HAS_PLAN");
			if(!StringMaster.isFlag(hasPlan)){
				message.append(hasPlan + " is not a flag. Flag is 0 or 1");
				return null;
			}
			query.append(hasPlan + ",");
		}
		else{
			message.append("You have ot enter has plan");
			return null;
		}
		query.append("CATEGORY=");
		if(values.containsKey("CATEGORY")){
			String category = values.get("CATEGORY");
			if(!StringMaster.isNumber(category)){
				message.append(category + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(category + ",");
		}
		else{
			message.append("You have ot enter category");
			return null;
		}
		query.append("DAYS=");
		if(values.containsKey("DAYS")){
			String days = values.get("DAYS");
			if(!StringMaster.isNumber(days)){
				message.append(days + " is not a number. You have to enter positive integer or zero");
				return null;
			}
			query.append(days + ",");
		}
		else{
			message.append("You have ot enter days");
			return null;
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
			message.append("You have ot enter name");
			return null;
		}
		query.append("ROUTE=");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			message.append("You have ot enter route");
			return null;
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
	public String getSelectingColumns(){
		return "NAME;ROUTE;REQUIREMENT;DAYS;CATEGORY;HAS_PLAN";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "NAME;ROUTE;REQUIREMENT;DAYS;CATEGORY;HAS_PLAN";
	}
	
	@Override
	public String getTableColumns(){
		return "NAME;ROUTE;REQUIREMENT;DAYS;CATEGORY;HAS_PLAN";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in HikesHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		if(!tableValues.addAll(selectingValues)){
			return false;
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in HikesHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		selectingValues.addAll(tableValues);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in HikesHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in HikesHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
		}
		return selectingValues;
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
	
	private int SELECTING_FIELDS = 6;
	private String SELECT_FILE = "SQL_select_hikes.txt";
}