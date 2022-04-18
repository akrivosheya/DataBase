package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonTest implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(queryMaster == null){
			throw new NullPointerException("Problem in ButtonTest.doOnAction: queryMaster is null");
		}
		List<String> queries = queryMaster.getQueries(RESET_FILE);
		if(queries == null){
			windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
			return;
		}
		List<String> queriesTest = queryMaster.getQueries(TEST_FILE);
		if(queriesTest == null){
			windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
			return;
		}
		if(!queries.addAll(queriesTest)){
			windowOpener.sendInformation("Can't get quieries");
			return;
		}
		String result = connecter.sendQueries(queries);
		windowOpener.sendInformation(result);
	}
	
	private String RESET_FILE = "SQL_commands_reset.txt";
	private String TEST_FILE = "SQL_commands_test.txt";
	private WindowOpener windowOpener = WindowOpener.getInstance();
}