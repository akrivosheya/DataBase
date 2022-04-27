package tourists.helpers;

import java.util.*;
import java.io.*;

public class CoachesTimeHelper implements QueryHelper{
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
							case "Name":
								query.append("TOURISTS.NAME='" + value + "' AND\n");
								break;
							case "Last name":
								query.append("TOURISTS.LAST_NAME='" + value + "' AND\n");
								break;
							case "Birth":
								query.append("TOURISTS.BIRTH='" + value + "' AND\n");
								break;
							case "Training":
								query.append("TRAININGS.NAME='" + value + "' AND\n");
								break;
							case "Trains after hour":
								query.append("TRAININGS.ENDING_HOUR>" + value + " AND\n");
								break;
							case "Trains before hour":
								query.append("TRAININGS.BEGINNING_HOUR<" + value + " AND\n");
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
			query.delete(query.length() - " AND\n".length(), query.length());
		}
		query.append("GROUP BY TRAININGS.NAME");
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values){
		return null;
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields){
		return null;
	}
	
	@Override
	public String getDeletingQuery(Map<String, String> params){
		return null;
	}
	
	@Override
	public String getColumns(){
		return "TRAINING;TIME";
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
	
	private String SELECT_FILE = "SQL_select_coaches_time.txt";
}