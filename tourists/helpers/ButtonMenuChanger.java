package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonMenuChanger implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String params, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(menu == null){
			throw new NullPointerException("Problem in ButtonMenuChanger.doOnAction: menu is null");
		}
		if(params == null){
			throw new NullPointerException("Problem in ButtonMenuChanger.doOnAction: params is null");
		}
		String[] arrayParams = params.split(DELIM);
		String nextMenu = arrayParams[INDEX_NEXT_MENU];
		if(arrayParams.length >= BUTTON_PARAMS && !connecter.hasRole(arrayParams[INDEX_ROLE])){
			windowOpener.sendInformation("Need " + arrayParams[INDEX_ROLE] + " privileges");
			return;
		}
		if(groups == null){
			throw new NullPointerException("Problem in ButtonMenuChanger.doOnAction: groups is null");
		}
		if(!groups.containsKey(nextMenu)){
			throw new RuntimeException("Problem in ButtonMenuChanger.doOnAction: there is not " + nextMenu);
		}
		if(!groups.containsKey(menu)){
			throw new RuntimeException("Problem in ButtonMenuChanger.doOnAction: there is not " + menu);
		}
		ElementsCleaner.cleanElements(elements);
		groups.get(menu).setVisible(false);
		groups.get(menu).getChildren().remove(table);
		groups.get(nextMenu).setVisible(true);
	}
	
	private String DELIM = " ";
	private int INDEX_NEXT_MENU = 0;
	private int INDEX_ROLE = 1;
	private int BUTTON_PARAMS = 2;
	private WindowOpener windowOpener = WindowOpener.getInstance();
}