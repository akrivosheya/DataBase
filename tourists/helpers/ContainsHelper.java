package tourists.helpers;

import java.util.*;
import java.io.*;

public class ContainsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO CONTAINS VALUES(");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "')");
		}
		else{
			message.append("You have to enter route");
			return null;
		}
		query.append(",");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "')");
		}
		else{
			message.append("You have to enter place");
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
		StringBuilder query = new StringBuilder("UPDATE CONTAINS SET ");
		query.append("ROUTE=");
		if(values.containsKey("ROUTE")){
			query.append("(SELECT ID FROM ROUTE WHERE NAME='" + values.get("ROUTE") + "'),");
		}
		else{
			message.append("You have to enter route");
			return null;
		}
		query.append("PLACE=");
		if(values.containsKey("PLACE")){
			query.append("(SELECT ID FROM PLACE WHERE NAME='" + values.get("PLACE") + "')");
		}
		else{
			message.append("You have to enter place");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "ROUTE":
					query.append(attribute + "=(SELECT ID FROM ROUTE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM CONTAINS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "ROUTE":
					query.append(attribute + "=(SELECT ID FROM ROUTE WHERE NAME='" + value + "') AND ");
					break;
				case "PLACE":
					query.append(attribute + "=(SELECT ID FROM PLACE WHERE NAME='" + value + "') AND ");
					break;
			}
		});
		query.delete(query.length() - " AND ".length(), query.length());
		return query.toString();
	}
	
	@Override
	public String getSelectingColumns(){
		return "ROUTE;PLACE";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "ROUTE;PLACE";
	}
	
	@Override
	public String getTableColumns(){
		return "ROUTE;PLACE";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in ConatainsHelper.setSelectingToTable: null argument");
		}
		if(!tableValues.addAll(selectingValues)){
			return false;
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in ConatainsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		selectingValues.addAll(tableValues);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in ConatainsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in ConatainsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	
	private int SELECTING_FIELDS = 2;
	private String SELECT_FILE = "SQL_select_contains.txt";
}