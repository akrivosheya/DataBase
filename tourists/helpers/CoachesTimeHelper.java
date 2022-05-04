package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class CoachesTimeHelper implements QueryHelper{
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
		if((fields != null && fields.size() > 0) || (flags != null && flags.size() > 0)){
			query.append(" WHERE ");
			if(fields != null){
				for(Map.Entry<String, String> entry : fields.entrySet()){
					if(!entry.getValue().equals("")){
						if(StringMaster.isNull(entry.getValue())){
							message.append("You can't enter null values here");
							return null;
						}
						switch(entry.getKey()){
							case "Section":
								query.append("SECTIONS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Name":
								query.append("TOURISTS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Last name":
								query.append("TOURISTS.LAST_NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Birth":
								if(!StringMaster.isDate(entry.getValue())){
									message.append(entry.getValue() + " is not a date. Date format: dd.mm.yyyy");
									return null;
								}
								query.append("TOURISTS.BIRTH='" + entry.getValue().substring(0, DATE_LENGTH) + "' AND\n");
								break;
							case "Training":
								query.append("TRAININGS.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Trains after hour":
								query.append("TRAININGS.ENDING_HOUR>" + entry.getValue() + " AND\n");
								break;
							case "Trains before hour":
								query.append("TRAININGS.BEGINNING_HOUR<" + entry.getValue() + " AND\n");
								break;
							case "Trains after day":
								query.append("TRAININGS.DAY>=" + entry.getValue() + " AND\n");
								break;
							case "Trains before day":
								query.append("TRAININGS.DAY<=" + entry.getValue() + " AND\n");
								break;
						}
					}
				}
			}
			query.delete(query.length() - " AND\n".length(), query.length());
		}
		query.append("GROUP BY TRAININGS.NAME");
		return query.toString();
	}
	
	@Override
	public String getInsertingQuery(Map<String, String> values, StringBuilder message){
		return null;
	}
	
	@Override
	public String getUpdatingQuery(Map<String, String> values, Map<String, String> fields, StringBuilder message){
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
	
	private int DATE_LENGTH = 10;
	private String SELECT_FILE = "SQL_select_coaches_time.txt";
}