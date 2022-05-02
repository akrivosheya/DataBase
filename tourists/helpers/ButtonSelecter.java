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
		Map<String, String> fields = StringMaster.getMapFormTextsAndFields(elements.getTextsWithFields(), elements.getFields());
		fields.putAll(StringMaster.getMapFormTextsAndDropdowns(elements.getTextsWithDropdowns(), elements.getDropdowns()));
		String query = queryMaster.getSelectingQuery(fields, StringMaster.getFlags(elements.getFlags()));
		System.out.println(query);
		if(query == null){
			windowOpener.sendInformation("Can't get query for that operation");
			return;
		}
		List<String> result = new ArrayList<String>();
		String columns = queryMaster.getColumns();
		if(columns == null){
			windowOpener.sendInformation("Can't get columns from queryMaster");
			return;
		}
		if(!result.add(columns)){
			windowOpener.sendInformation("Can't get result of select command");
			return;
		}
		List<String> values = connecter.executeQuery(query, StringMaster.arrayStringsToList(columns.split(DELIM)));
		if(values == null || (!result.addAll(values) && values.size() != 0)){
			windowOpener.sendInformation("Can't get result of select command");
			return;
		}
		windowOpener.sendSelectingResult(result, table);
	}
	
	private String DELIM = ";";
	private WindowOpener windowOpener = WindowOpener.getInstance();
}