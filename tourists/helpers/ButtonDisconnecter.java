package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class ButtonDisconnecter extends ButtonMenuChanger{
	@Override
	public void doOnAction(String menu, String nextMenu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table, ConnecterDataBase connecter, QueryMaster queryMaster){
		if(connecter == null){
			throw new NullPointerException("Problem in ButtonDisconnecter.doOnAction: connecter is null");
		}
		connecter.disconnect();
		super.doOnAction(menu, nextMenu, groups, elements, table, connecter, queryMaster);
	}
}