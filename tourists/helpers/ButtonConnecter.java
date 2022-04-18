package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonConnecter extends ButtonMenuChanger{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(elements == null){
			throw new NullPointerException("Problem in ButtonConnecter.doOnAction: elements is null");
		}
		List<TextField> fields = elements.getFields();
		if(fields == null){
			throw new NullPointerException("Problem in ButtonConnecter.doOnAction: fields in elements is null");
		}
		if(fields.size() < COUNT_FIELDS){
			throw new RuntimeException("Problem in ButtonConnecter.doOnAction: count of fields is " + 
			fields.size() + " that is less than " + COUNT_FIELDS);
		}
		String user = elements.getFields().get(INDEX_USER_FIELD).getText();
		String password = elements.getFields().get(INDEX_PASSWORD_FIELD).getText();
		String database = elements.getFields().get(INDEX_DATABASE_FIELD).getText();
		StringBuilder result = new StringBuilder("");
		if(connecter.connect(user, password, database, result)){
			super.doOnAction(menu, nextMenu, groups, elements, table, connecter, queryMaster);
		}
		else{
			throw new RuntimeException("Problem in ButtonConnecter.doOnAction: " + result);
		}
	}
	
	
	private int INDEX_USER_FIELD = 0;
	private int INDEX_PASSWORD_FIELD = 1;
	private int INDEX_DATABASE_FIELD = 2;
	private int COUNT_FIELDS = 3;
}