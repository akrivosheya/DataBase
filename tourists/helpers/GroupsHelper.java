package tourists.helpers;

import java.util.*;
import java.io.*;

public class GroupsHelper implements QueryHelper{
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
		StringBuilder query = new StringBuilder("INSERT INTO GROUPS VALUES(");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "')");
		}
		else{
			message.append("You have to enter section");
			return null;
		}
		query.append(",1,");
		if(values.containsKey("NAME")){
			query.append(values.get("NAME"));
		}
		else{
			message.append("You have to enter name");
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
		StringBuilder query = new StringBuilder("UPDATE GROUPS SET ");
		query.append("SECTION=");
		if(values.containsKey("SECTION")){
			query.append("(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + values.get("SECTION") + "'),");
		}
		else{
			message.append("You have to enter section");
			return null;
		}
		query.append("NAME=");
		if(values.containsKey("NAME")){
			query.append("'" + values.get("NAME") + "'");
		}
		else{
			message.append("You have to enter name");
			return null;
		}
		query.append("\nWHERE ");
		fields.forEach((String attribute, String value)->{
			switch(attribute){
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
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
		StringBuilder query = new StringBuilder("DELETE FROM GROUPS WHERE ");
		params.forEach((String attribute, String value)->{
			switch(attribute){
				case "SECTION":
					query.append("GROUPS.SECTION=(SELECT ID FROM SECTIONS WHERE SECTIONS.NAME='" + value + "') AND ");
					break;
				case "NAME":
					query.append(attribute + "='" + value + "' AND ");
					break;
			}
		});
		return query.substring(0, query.length() - " AND ".length());
	}
	
	@Override
	public String getSelectingColumns(){
		return "SECTION;NAME";
	}
	
	@Override
	public String getUpdatingColumns(){
		return "SECTION;NAME";
	}
	
	@Override
	public String getTableColumns(){
		return "SECTION;NAME";
	}
	
	public boolean setSelectingToTable(List<String> selectingValues, List<String> tableValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in GroupsHelper.setSelectingToTable: null argument");
		}
		StringBuilder row = new StringBuilder("");
		if(!tableValues.addAll(selectingValues)){
			return false;
		}
		return true;
	}
	
	public void setTableToSelecting(List<String> tableValues, List<String> selectingValues){
		if(selectingValues == null || tableValues == null){
			throw new NullPointerException("Problem in GroupsHelper.setSelectingToTable: null argument");
		}
		selectingValues.clear();
		selectingValues.addAll(tableValues);
	}
	
	public List<String> getUpdatingFromSelecting(List<String> selectingValues){
		if(selectingValues == null){
			throw new NullPointerException("Problem in GroupsHelper.getUpdatingFromSelecting: null argument");
		}
		if(selectingValues.size() < SELECTING_FIELDS){
			throw new RuntimeException("Problem in GroupsHelper.getUpdatingFromSelecting: length " + selectingValues.size() + " of argument less than " + SELECTING_FIELDS);
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
	private String SELECT_FILE = "SQL_select_groups.txt";
}