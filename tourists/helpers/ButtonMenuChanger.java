package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.util.*;

import tourists.TableData;

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
				sendInformation("Is not ready");
			}
		}
	}
	
	private void sendInformation(String information){
		Stage window = new Stage();
		Group root = new Group();
        Scene scene = new Scene(root, INFORMATION_WIDTH, INFORMATION_HEIGHT, Color.WHITE);
        window.setScene(scene);
		window.setTitle("Info");
		Text text = new Text(0, INFORMATION_HEIGHT / 2, information);
		text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		root.getChildren().add(text);
		window.setMaxWidth(INFORMATION_WIDTH + OUTLINE_WIDTH);
		window.setMinWidth(INFORMATION_WIDTH + OUTLINE_WIDTH);
		window.setMaxHeight(INFORMATION_HEIGHT + TITLE_HEIGHT);
		window.setMinHeight(INFORMATION_HEIGHT + TITLE_HEIGHT);
		window.initModality(Modality.APPLICATION_MODAL);
		window.show();
	}
	
	private double INFORMATION_HEIGHT = 100;
	private double INFORMATION_WIDTH = 300;
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
}