package tourists.helpers;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public interface ButtonHelper{
	public void doOnAction(String key, String param, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table);
}