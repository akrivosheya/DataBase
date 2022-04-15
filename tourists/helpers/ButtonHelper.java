package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.TableData;

public interface ButtonHelper{
	public void doOnAction(String key, String param, Map<String, Group> groups, 
	List<Text> textFields, List<TextField> fields, List<CheckBox> flags, TableView<TableData> table);
}