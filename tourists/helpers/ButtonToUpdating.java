package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonToUpdating implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(table == null){
			throw new NullPointerException("Problem in ButtonToUpdating.doOnAction: table is null");
		}
		if(queryMaster == null){
			throw new NullPointerException("Problem in ButtonToUpdating.doOnAction: queryMaster is null");
		}
		if(connecter == null){
			throw new NullPointerException("Problem in ButtonToUpdating.doOnAction: connecter is null");
		}
		List<TableData> list = table.getSelectionModel().getSelectedItems();
		List<String> tableValues = null;
		if(list.size() == 1){
			tableValues = list.get(0).getListStrings();
		}
		else{
			windowOpener.sendInformation("Please choose row that you want to update");
			return;
		}
		List<String> selectingColumns = StringMaster.arrayStringsToList(queryMaster.getSelectingColumns().split(DELIM));
		List<String> updatingColumns = StringMaster.arrayStringsToList(queryMaster.getUpdatingColumns().split(DELIM));
		List<String> selectingValues = new ArrayList<String>();
		queryMaster.setTableToSelecting(tableValues, selectingValues);
		windowOpener.openUpdatingWindow(updatingColumns, selectingColumns, selectingValues, table, queryMaster, connecter);
	}
	
	private String DELIM = ";";
	private WindowOpener windowOpener = WindowOpener.getInstance();
}