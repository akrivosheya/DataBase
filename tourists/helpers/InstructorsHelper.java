package tourists.helpers;

import java.util.*;
import java.io.*;

import tourists.StringMaster;

public class InstructorsHelper implements QueryHelper{
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
							case "Category":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("TOURISTS.CATEGORY=" + entry.getValue() + " AND\n");
								break;
							case "Hike":
								query.append("HIKE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Hikes count":
								if(!StringMaster.isNumber(entry.getValue())){
									message.append(entry.getValue() + " is not a number. You have to enter positive integer or zero");
									return null;
								}
								query.append("INSTRUCTORS_HIKES_COUNT.COUNT=" + entry.getValue() + " AND\n");
								break;
							case "Route":
								query.append("ROUTE.NAME='" + entry.getValue() + "' AND\n");
								break;
							case "Point":
								query.append("PLACE.NAME='" + entry.getValue() + "' AND\n");
								break;
						}
					}
				}
			}
			if(flags != null){
				Iterator<String> iteratorFlag = flags.iterator();
				while(iteratorFlag.hasNext()){
					String flag = iteratorFlag.next();
					if(flag == null){
						return null;
					}
					switch(flag){
						case "Is sportsman":
							query.append("TOURISTS.TYPE='SPORTSMAN'");
							break;
						case "Is coach":
							query.append("TOURISTS.TYPE='COACH'");
							break;
					}
					query.append(" AND\n");
				}
			}
			return query.substring(0, query.length() - " AND\n".length());
		}
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
		return "NAME;LAST_NAME;SEX;BIRTH;CATEGORY";
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
	
	private String SELECT_FILE = "SQL_select_instructors.txt";
}