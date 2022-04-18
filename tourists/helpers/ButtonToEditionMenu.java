package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonToEditionMenu extends ButtonMenuChanger{
	@Override
	public void doOnAction(String menu, String helperName, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(menu == null){
			throw new NullPointerException("Problem in ButtonToEditionMenu.doOnAction: menu is null");
		}
		if(helperName == null){
			throw new NullPointerException("Problem in ButtonToEditionMenu.doOnAction: helperName is null");
		}
		if(groups == null){
			throw new NullPointerException("Problem in ButtonToEditionMenu.doOnAction: groups is null");
		}
		if(table == null){
			throw new NullPointerException("Problem in ButtonToEditionMenu.doOnAction: table is null");
		}
		if(!groups.containsKey("to_edit")){
			throw new RuntimeException("Problem in ButtonToEditionMenu.doOnAction: application doesn't have menu to_edit");
		}
		QueryHelper helper = (QueryHelper)HelperFactory.getInstance().getHelper(helperName);
		if(helper == null){
			throw new RuntimeException("Problem in ButtonToEditionMenu.doOnAction: can't get helper " + helperName);
		}
		queryMaster.setHelper(helper);
		if(!configurator.configureTable(table, queryMaster, connecter)){
			windowOpener.sendInformation("Have some problems with creating table");
		}
		else{
			groups.get("to_edit").getChildren().add(table);
			super.doOnAction(menu, "to_edit", groups, elements, table, connecter, queryMaster);
		}
	}
	
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
	private WindowOpener windowOpener = WindowOpener.getInstance();
}