package tourists;

import java.sql.*;
import java.util.*;
import java.io.*;

public class ConnecterDataBase{
	public ConnecterDataBase(String fileWithErrorsAndMessages){
		if(fileWithErrorsAndMessages == null){
			throw new NullPointerException("Can't create ConnecterDataBase: fileWithErrorsAndMessages is null");
		}
		File file = new File(fileWithErrorsAndMessages);
		if(!file.exists()){
			System.err.println("Can't configure properties: file " + fileWithErrorsAndMessages + " doesn't exist");
		}
		else{
			try{
				properties.load(new FileInputStream(file));
			}
			catch(IOException e){
				System.err.println("Can't configure properties: " + e.getMessage());
			}
		}
	}
	
	public boolean connect(String user, String password, String database, StringBuilder result){
		if(result == null){
			throw new NullPointerException("Problem in ConnecterDataBase: result is null");
		}
		String currentDatabase;
		if(database.length() == 0){
			currentDatabase = DEFAULT_DATABASE;
		}
		else{
			currentDatabase = database;
		}
		try {
			//connection = DriverManager.getConnection("jdbc:oracle:thin:" + 
			//	user + "/" + password + "@" + currentDatabase);
			connection = DriverManager.getConnection("jdbc:oracle:thin:" + 
				"19212_krivosheya" + "/" + "362917458boom" + "@" + currentDatabase);
			connection.setAutoCommit(false);
			roles = executeQuery("SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS", List.of("GRANTED_ROLE"), result);
			if(roles == null){
				return false;
			}
			List<String> tmp = new ArrayList<String>();
			for(String role : roles){
				tmp.add(role.substring(0, role.length() - 1));
			}
			roles = tmp;
			return true;
		}
		catch(SQLException e){
			result.append("Can't connect to database: " + getMessage(e));
			return false;
		}
	}
	
	public String sendQueries(List<String> queries){
		if(queries == null){
			return "Queries list is null";
		}
		Iterator<String> iterator = queries.iterator();
		String query = null;
		while(iterator.hasNext()){
			try(Statement stmt = connection.createStatement()){
				query = iterator.next();
				stmt.setQueryTimeout(TIMEOUT_MILLIS);
				stmt.execute(query);
			}
			catch(SQLException e){
				int errorCode = e.getErrorCode();
				if(errorCode == ERROR_CODE_ALREADY_EXIST || errorCode == ERROR_CODE_NOT_EXIST || errorCode == ERROR_TRIGGER_DOES_NOT_EXISTS){
					continue;
				}
				try{
					connection.rollback();
				}
				catch(SQLException ee){
					return "FATAL ERROR. DATABASE IS IN UNDEPENDENT STATE: " + getMessage(ee);
				}
				System.err.println("Query:\n" + query);
				return "Can't execute command: " + getMessage(e);
			}
		}
		try{
			connection.commit();
		}
		catch(SQLException e){
			try{
				connection.rollback();
			}
			catch(SQLException ee){
				return "Can't commit changes and rollback them: " + getMessage(ee);
			}
			return "Can't commit changes: " + getMessage(e);
		}
		return "Successful operation";
	}
	
	public List<String> executeQuery(String query, List<String> keys, StringBuilder result){
		if(query == null || keys == null || result == null){
			System.err.println("Null arguments for executeQuery");
			return null;
		}
		List<String> rows = new ArrayList<String>();
		StringBuilder row = new StringBuilder("");
		try(Statement stmt = connection.createStatement()){
			stmt.setQueryTimeout(TIMEOUT_MILLIS);
			ResultSet resultSet = stmt.executeQuery(query);
			while(resultSet.next()){
				Iterator<String> iteratorKey = keys.iterator();
				while(iteratorKey.hasNext()){
					String column = iteratorKey.next();
					row.append(resultSet.getString(column) + ";");
					if(row.length() > " 00:00:00;".length() && 
					row.substring(row.length() - " 00:00:00;".length(), row.length() - 1).equals(" 00:00:00")){
						row.delete(row.length() - " 00:00:00;".length(), row.length() - 1);
						String year = row.substring(row.length() - "xxxx-xx-xx;".length(), row.length() - "-xx-xx;".length());
						String month = row.substring(row.length() - "xx-xx;".length(), row.length() - "-xx;".length());
						String day = row.substring(row.length() - "xx;".length(), row.length() - ";".length());
						row.replace(row.length() - "xxxx-xx-xx;".length(), row.length() - ";".length(), day + '.' + month + '.' + year);
					}
				}
				rows.add(row.toString());
				row.delete(0, row.length());
			}
		}
		catch(SQLException e){
			result.append("Can't execute command: " + getMessage(e));
			System.err.println("Query:\n" + query);
			return null;
		}
		return rows;
	}
	
	public void disconnect(){
		if(connection != null){
			try{
				connection.close();
				roles = null;
			}
			catch(SQLException e){
				System.err.println("Can't close connection: " + e.getMessage());
			}
		}
	}
	
	public boolean hasRole(String role){
		return roles.contains(role);
	}
	
	private String getMessage(SQLException e){
		String message = properties.getProperty(String.valueOf(e.getErrorCode()));
		if(message == null){
			System.err.println(e.getErrorCode());
			return e.getMessage();
		}
		return message;
	}
	
	private String DEFAULT_DATABASE = "84.237.50.81:1521";
	private int ERROR_CODE_NOT_EXIST = 942;
	private int ERROR_CODE_ALREADY_EXIST = 955;
	private int ERROR_TRIGGER_DOES_NOT_EXISTS = 4080;
	private int TIMEOUT_MILLIS = 5000;
	
	private Connection connection = null;
	private Properties properties = new Properties();
	private List<String> roles;
}