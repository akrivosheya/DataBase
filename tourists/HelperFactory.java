package tourists;

import java.util.*;

public class HelperFactory{
	private HelperFactory(){}
	
	public static HelperFactory getInstance(){
		if(instance == null){
			instance = new HelperFactory();
		}
		return instance;
	}
	
	public Object getHelper(String className){
		if(className == null){
			return null;
		}
		if(helpers.containsKey(className)){
			return helpers.get(className);
		}
		else{
			Object helper;
			try{
				Class<?> type = Class.forName(className);
				helper = type.getDeclaredConstructor().newInstance();
				helpers.put(className, helper);
			}
			catch(Exception e){
				System.err.println("Can't find " + className + ": " + e.getMessage());
				return null;
			}
			return helper;
		}
	}
	
	private static HelperFactory instance = null;
	private Map<String, Object> helpers = new HashMap<String, Object>();
}