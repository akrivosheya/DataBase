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
		String currentDatabase;
		if(database.length() == 0){
			currentDatabase = DEFAULT_DATABASE;
		}
		else{
			currentDatabase = database;
		}
		try{
			//connection = DriverManager.getConnection("jdbc:oracle:thin:" + 
			//	user + "/" + password + "@" + currentDatabase);
			connection = DriverManager.getConnection("jdbc:oracle:thin:" + 
				"19212_krivosheya" + "/" + "362917458boom" + "@" + currentDatabase);
			connection.setAutoCommit(false);
			return true;
		}
		catch(SQLException e){
			result.append("Can't connect to database: \n" + e.getMessage());
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
				stmt.execute(query);
			}
			catch(SQLException e){
				int errorCode = e.getErrorCode();
				if(errorCode == ERROR_CODE_ALREADY_EXIST || errorCode == ERROR_CODE_NOT_EXIST){
					continue;
				}
				try{
					connection.rollback();
				}
				catch(SQLException ee){
					return "FATAL ERROR. DATABASE IS IN UNDEPENDENT STATE:\n" + getMessageForError(ee.getErrorCode());
				}
				return "Can't execute command:\n" + getMessageForError(e.getErrorCode()) + "\n" + e.getMessage();
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
				return "Can't commit changes and rollback them:\n" + getMessageForError(ee.getErrorCode());
			}
			return "Can't commit changes:\n" + getMessageForError(e.getErrorCode());
		}
		return "Successful operation";
	}
	
	public List<String> executeQuery(String query, List<String> keys){
		if(query == null || keys == null){
			System.err.println("Null arguments for executeQuery");
			return null;
		}
		List<String> rows = new ArrayList<String>();
		StringBuilder row = new StringBuilder("");
		try(Statement stmt = connection.createStatement()){
			ResultSet resultSet = stmt.executeQuery(query);
			while(resultSet.next()){
				Iterator<String> iteratorKey = keys.iterator();
				while(iteratorKey.hasNext()){
					String column = iteratorKey.next();
					if(column == null){
						System.err.println("Null key in keys in executeQuery");
						return null;
					}
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
			System.err.println("Can't execute command:\n" + getMessageForError(e.getErrorCode()));
			return null;
		}
		return rows;
	}
	
	public void disconnect(){
		if(connection != null){
			try{
				connection.close();
			}
			catch(SQLException e){
				System.err.println("Can't close connection: " + e.getMessage());
			}
		}
	}
	
	private String getMessageForError(int error){
		return properties.getProperty(String.valueOf(error), "Unknow error: " + error);
	}
	
	private String DEFAULT_DATABASE = "84.237.50.81:1521";
	private int ERROR_CODE_NOT_EXIST = 942;
	private int ERROR_CODE_ALREADY_EXIST = 955;
	
	private Connection connection = null;
	private Properties properties = new Properties();
}