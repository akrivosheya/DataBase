package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonDeleter implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(table == null){
			throw new NullPointerException("Problem in ButtonToCreating.doOnAction: table is null");
		}
		if(queryMaster == null){
			throw new NullPointerException("Problem in ButtonToCreating.doOnAction: queryMaster is null");
		}
		if(connecter == null){
			throw new NullPointerException("Problem in ButtonToCreating.doOnAction: connecter is null");
		}
		List<TableData> list = table.getSelectionModel().getSelectedItems();
		List<String> values = null;
		if(list.size() == 1){
			values = list.get(0).getListStrings();
		}
		else{
			windowOpener.sendInformation("Please choose row that you want to delete");
			return;
		}
		List<String> columns = StringMaster.arrayStringsToList(queryMaster.getColumns().split(DELIM));
		if(columns == null){
			windowOpener.sendInformation("Can't get columns for this table");
			return;
		}
		String query = queryMaster.getDeletingQuery(StringMaster.getMapFromStrings(columns, values));
		if(query == null){
			windowOpener.sendInformation("Can't get query for that operation");
			return;
		}
		String result = connecter.sendQueries(List.of(query));
		windowOpener.sendInformation(result);
		StringBuilder message = new StringBuilder("");
		if(!configurator.configureTable(table, queryMaster, connecter, message)){
			windowOpener.sendInformation(message.toString());
		}
	}
	
	private String DELIM = ";";
	private WindowOpener windowOpener = WindowOpener.getInstance();
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
}