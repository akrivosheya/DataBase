package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonSelecter implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String helperName, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(helperName == null){
			throw new NullPointerException("Problem in ButtonSelecter.doOnAction: helperName is null");
		}
		if(queryMaster == null){
			throw new NullPointerException("Problem in ButtonSelecter.doOnAction: queryMaster is null");
		}
		if(elements == null){
			throw new NullPointerException("Problem in ButtonSelecter.doOnAction: elements is null");
		}
		if(connecter == null){
			throw new NullPointerException("Problem in ButtonSelecter.doOnAction: connecter is null");
		}
		QueryHelper helper = (QueryHelper)HelperFactory.getInstance().getHelper(helperName);
		queryMaster.setHelper(helper);
		Map<String, String> fields = StringMaster.getMapFromTextsAndFields(elements.getTextsWithFields(), elements.getFields(), true);
		fields.putAll(StringMaster.getMapFormTextsAndDropdowns(elements.getTextsWithDropdowns(), elements.getDropdowns()));
		StringBuilder message = new StringBuilder("");
		String query = queryMaster.getSelectingQuery(fields, StringMaster.getFlags(elements.getFlags()), message);
		System.out.println(query);
		if(query == null){
			windowOpener.sendInformation(message.toString());
			return;
		}
		List<String> rows = new ArrayList<String>();
		String selectingColumns = queryMaster.getSelectingColumns();
		String tableColumns = queryMaster.getTableColumns();
		if(selectingColumns == null || tableColumns == null){
			windowOpener.sendInformation("Can't get columns from queryMaster");
			return;
		}
		if(!rows.add(tableColumns)){
			windowOpener.sendInformation("Can't get rows of select command");
			return;
		}
		List<String> values = connecter.executeQuery(query, StringMaster.arrayStringsToList(selectingColumns.split(DELIM)), message);
		if(values == null || (values.size() != 0 && !queryMaster.setSelectingToTable(values, rows))){
			windowOpener.sendInformation(message.toString());
			return;
		}
		windowOpener.sendSelectingResult(rows, table);
	}
	
	private String DELIM = ";";
	private WindowOpener windowOpener = WindowOpener.getInstance();
}