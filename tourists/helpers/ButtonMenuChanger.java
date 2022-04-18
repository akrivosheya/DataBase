package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonMenuChanger implements ButtonHelper{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(menu == null){
			throw new NullPointerException("Problem in ButtonMenuChanger.doOnAction: menu is null");
		}
		if(nextMenu == null){
			throw new NullPointerException("Problem in ButtonMenuChanger.doOnAction: nextMenu is null");
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
}