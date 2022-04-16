package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.util.*;

import tourists.*;

public class ButtonMenuChanger implements ButtonHelper{
	@Override
	public void doOnAction(String key, String nextMenu, Map<String, Group> groups, 
	List<Text> textFields, List<TextField> fields, List<CheckBox> flags, TableView<TableData> table){
		if(!(key == null || nextMenu == null || groups == null)){
			if(groups.containsKey(nextMenu)){
				if(fields != null){
					Iterator<TextField> iteratorTextField = fields.iterator();
					while(iteratorTextField.hasNext()){
						iteratorTextField.next().setText("");
					}
				}
				if(flags != null){
					Iterator<CheckBox> iteratorFlag = flags.iterator();
					while(iteratorFlag.hasNext()){
						iteratorFlag.next().setSelected(false);
					}
				}
				groups.get(key).setVisible(false);
				groups.get(key).getChildren().remove(table);
				groups.get(nextMenu).setVisible(true);
			}
			else{
				windowOpener.sendInformation("Is not ready");
			}
		}
	}
	
	private WindowOpener windowOpener = WindowOpener.getInstance()();
}