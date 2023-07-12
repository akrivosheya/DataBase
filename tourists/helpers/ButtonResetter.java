package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonResetter implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String privileges, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(queryMaster == null){
			throw new NullPointerException("Problem in ButtonResetter.doOnAction: queryMaster is null");
		}
		if(privileges == null){
			throw new NullPointerException("Problem in ButtonResetter.doOnAction: privileges is null");
		}
		if(!connecter.hasRole(privileges)){
			windowOpener.sendInformation("Need " + privileges + " privileges");
			return;
		}
		List<String> queries = queryMaster.getQueries(RESET_FILE);
		if(queries == null){
			windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
		}
		else{
			String result = connecter.sendQueries(queries);
			windowOpener.sendInformation(result);
		}
	}
	
	private String RESET_FILE = "SQL_commands_reset.txt";
	private WindowOpener windowOpener = WindowOpener.getInstance();
}