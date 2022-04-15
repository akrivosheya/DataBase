package tourists;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import java.util.*;


import tourists.helpers.QueryHelper;
import tourists.helpers.ButtonHelper;

class Controller{
	public Map<String, Group> getGroups(TableView<TableData> table){
		MenuLoader loader = new MenuLoader();
		Map<String, MenuData> menuesData = new HashMap<String, MenuData>();
		if(!loader.getMapMenuData(MENUES_FILE, menuesData)){
			return null;
		}
		Map<String, Group> groups = new HashMap<String, Group>();
		menuesData.forEach((String key, MenuData menuData)->{
			Group group = new Group();
			List<Text> texts = new ArrayList<Text>();
			List<Text> textFields = new ArrayList<Text>();
			List<TextField> fields = new ArrayList<TextField>();
			List<CheckBox> flags = new ArrayList<CheckBox>();
			List<Button> buttons = new ArrayList<Button>();
			createTexts(menuData.getTexts(), texts);
			createTextFields(menuData.getFields(), textFields, fields);
			createFlags(menuData.getFlags(), flags);
			Iterator<ButtonData> iteratorButtonData = menuData.getButtons().iterator();
			ButtonData buttonData;
			while(iteratorButtonData.hasNext()){
				buttonData = iteratorButtonData.next();
				String text = buttonData.getText();
				Button button = new Button(text);
				String type = buttonData.getType();
				String param = buttonData.getParam();
				if(!configureButton(type, key, param, groups, textFields, fields, flags, button, table)){
					System.err.println(text + " button can't be added");
				}
				else{
					buttons.add(button);
				}
			}
			configureScene(group, texts, textFields, fields, flags, buttons, menuData.hasTable());
			if(!menuData.isStarting()){
				group.setVisible(false);
			}
			groups.put(key, group);
		});
		return groups;
	}
	
	private void createTexts(List<String> strings, List<Text> texts){
		if(strings == null || texts == null){
			System.err.println("Null arguments for createTexts");
			return;
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			if(text == null){
				System.err.println("createTexts got one null string");
				continue;
			}
			texts.add(new Text(text));
		}
	}
	
	private void createTextFields(List<String> strings, List<Text> texts, List<TextField> fields){
		if(strings == null || texts == null || fields == null){
			System.err.println("Null arguments for createTextFields");
			return;
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			if(text == null){
				System.err.println("createTextFields got one null string");
				continue;
			}
			texts.add(new Text(text));
			fields.add(new TextField());
		}
	}
	
	private void createFlags(List<String> strings, List<CheckBox> flags){
		if(strings == null || flags == null){
			System.err.println("Null arguments for createFlags");
			return;
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			if(text == null){
				System.err.println("createFlags got one null string");
				continue;
			}
			flags.add(new CheckBox(text));
		}
	}
	
	private boolean configureButton(String type, String key, String param, Map<String, Group> groups, 
	List<Text> textFields, List<TextField> fields, List<CheckBox> flags, Button button, TableView<TableData> table){
		switch(type){
			case "next":
				button.setOnAction(e->{
					((ButtonHelper)HelperFactory.getInstance().getHelper("tourists.helpers.ButtonMenuChanger")).doOnAction(key, param, groups, null, fields, flags, table);
				});
				return true;
			case "disconnect":
				button.setOnAction(e->{
					connecter.disconnect();
					baseButtonConfiguration(key, param, fields, flags, groups, table);
				});
				return true;
			case "connect":
				if(!configureConnectingButton(key, button, param, fields, flags, groups, table)){
					return false;
				}
				return true;
			case "to_edit":
				button.setOnAction(e->{
					QueryHelper helper = (QueryHelper)HelperFactory.getInstance().getHelper(param);
					queryMaster.setHelper(helper);
					if(!configureTable(table)){
						sendInformation("Have some problems with creating table");
					}
					else{
						groups.get("to_edit").getChildren().add(table);
						baseButtonConfiguration(key, "to_edit",  null, null, groups, table);
					}
				});
				return true;
			case "create":
				button.setOnAction(e->{
					List<TableData> list = table.getSelectionModel().getSelectedItems();
					List<String> values = null;
					if(list.size() == 1){
						values = list.get(0).getListStrings();
					}
					openCreatingWindow(arrayStringsToList(queryMaster.getColumns().split(DELIM)), values, table);
				});
				return true;
			case "update":
				button.setOnAction(e->{
					List<TableData> list = table.getSelectionModel().getSelectedItems();
					List<String> values = null;
					if(list.size() == 1){
						values = list.get(0).getListStrings();
					}
					else{
						sendInformation("Please choose row that you want to update");
						return;
					}
					openUpdatingWindow(arrayStringsToList(queryMaster.getColumns().split(DELIM)), values, table);
				});
				return true;
			case "delete":
				button.setOnAction(e->{
					List<TableData> list = table.getSelectionModel().getSelectedItems();
					List<String> values = null;
					if(list.size() == 1){
						values = list.get(0).getListStrings();
					}
					else{
						sendInformation("Please choose row that you want to delete");
						return;
					}
					List<String> columns = arrayStringsToList(queryMaster.getColumns().split(DELIM));
					if(columns == null){
						sendInformation("Can't get columns for this table");
						return;
					}
					String query = queryMaster.getDeletingQuery(getFullStringsFromStrings(columns, values));
					if(query == null){
						sendInformation("Can't get query for that operation");
						return;
					}
					String result = connecter.sendQueries(List.of(query));
					sendInformation(result);
					if(!configureTable(table)){
						sendInformation("Have some problems with creating table");
					}
				});
				return true;
			case "select":
				button.setOnAction(e->{
					QueryHelper helper = (QueryHelper)HelperFactory.getInstance().getHelper(param);
					queryMaster.setHelper(helper);
					String query = queryMaster.getSelectingQuery(getFullStringsFromControls(textFields, fields), getFlags(flags));
					if(query == null){
						sendInformation("Can't get quiery for that operation");
						return;
					}
					List<String> result = new ArrayList<String>();
					String columns = queryMaster.getColumns();
					if(columns == null){
						sendInformation("Can't get columns from queryMaster");
						return;
					}
					if(!result.add(columns)){
						sendInformation("Can't get result of select command");
						return;
					}
					List<String> values = connecter.executeQuery(query, arrayStringsToList(columns.split(DELIM)));
					if(values == null || !result.addAll(values)){
						sendInformation("Can't get result of select command");
						return;
					}
					sendSelectingResult(result, table);
				});
				return true;
			case "test":
				button.setOnAction(e->{
					List<String> queries = queryMaster.getQueries(RESET_FILE);
					if(queries == null){
						sendInformation("Can't get quieries from file " + RESET_FILE);
						return;
					}
					List<String> queriesTest = queryMaster.getQueries(TEST_FILE);
					if(queriesTest == null){
						sendInformation("Can't get quieries from file " + RESET_FILE);
						return;
					}
					if(!queries.addAll(queriesTest)){
						sendInformation("Can't get quieries");
						return;
					}
					String result = connecter.sendQueries(queries);
					sendInformation(result);
				});
				return true;
			case "reset":
				button.setOnAction(e->{
					List<String> queries = queryMaster.getQueries(RESET_FILE);
					if(queries == null){
						sendInformation("Can't get quieries from file " + RESET_FILE);
					}
					else{
						String result = connecter.sendQueries(queries);
						sendInformation(result);
					}
				});
				return true;
			default:
				return false;
		}
	}
	
	private Map<String, String> getFullStringsFromControls(List<Text> texts, List<TextField> fields){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<TextField> iteratorField = fields.iterator();
		Iterator<Text> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			TextField field = iteratorField.next();
			Text text = iteratorText.next();
			if(field == null || text == null){
				return null;
			}
			if(field.getText().length() > 0){
				strings.put(text.getText(), field.getText());
			}
		}
		return strings;
	}
	
	private Map<String, String> getFullStringsFromStrings(List<String> texts, List<String> fields){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<String> iteratorField = fields.iterator();
		Iterator<String> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			String field = iteratorField.next();
			String text = iteratorText.next();
			if(field == null || text == null){
				return null;
			}
			if(field.length() > 0){
				strings.put(text, field);
			}
		}
		return strings;
	}
	
	private List<String> getFlags(List<CheckBox> flags){
		if(flags == null){
			return null;
		}
		Iterator<CheckBox> iterator = flags.iterator();
		List<String> choosenFlags = new ArrayList<String>();
		while(iterator.hasNext()){
			CheckBox flag = iterator.next();
			if(flag == null){
				return null;
			}
			if(flag.isSelected()){
				choosenFlags.add(flag.getText());
			}
		}
		return choosenFlags;
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
	
	private void sendSelectingResult(List<String> result, TableView<TableData> table){
		if(result == null || result.size() == 0){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Stage window = new Stage();
		Group root = new Group();
		configureTable(result, table);
		root.getChildren().add(table);
        Scene scene = new Scene(root, TABLE_WIDTH, TABLE_HEIGHT, Color.WHITE);
        window.setScene(scene);
		window.setTitle("Result");
		window.setMaxWidth(TABLE_WIDTH + OUTLINE_WIDTH);
		window.setMinWidth(TABLE_WIDTH + OUTLINE_WIDTH);
		window.setMaxHeight(TABLE_HEIGHT + TITLE_HEIGHT);
		window.setMinHeight(TABLE_HEIGHT + TITLE_HEIGHT);
		window.initModality(Modality.APPLICATION_MODAL);
		window.show();
	}
	
	private void openCreatingWindow(List<String> columns, List<String> fieldsText, TableView<TableData> table){
		if(columns == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Stage window = new Stage();
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		createTextFields(columns, texts, fields);
		if(fieldsText != null){
			Iterator<String> iteratorFieldText = fieldsText.iterator();
			Iterator<TextField> iteratorField = fields.iterator();
			while(iteratorFieldText.hasNext() && iteratorField.hasNext()){
				String text = iteratorFieldText.next();
				TextField field = iteratorField.next();
				if(text != null){
					field.setText(text);
				}
			}
		}
		Button button = new Button("OK");
		button.setOnAction(e->{
			String query = queryMaster.getInsertingQuery(getFullStringsFromControls(texts, fields));
			if(query == null){
				sendInformation("Can't get query for that operation");
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configureTable(table)){
				sendInformation("Have some problems with creating table");
			}
		});
		configureScene(root, null, texts, fields, null, List.of(button), false);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        window.setScene(scene);
		window.setTitle("Creating");
		window.setMaxWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		window.setMinWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		window.setMaxHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		window.setMinHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		window.initModality(Modality.APPLICATION_MODAL);
		window.show();
	}
	
	private void openUpdatingWindow(List<String> columns, List<String> fieldsText, TableView<TableData> table){
		if(columns == null || fieldsText == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Stage window = new Stage();
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		createTextFields(columns, texts, fields);
		Iterator<String> iteratorFieldText = fieldsText.iterator();
		Iterator<TextField> iteratorField = fields.iterator();
		while(iteratorFieldText.hasNext() && iteratorField.hasNext()){
			String text = iteratorFieldText.next();
			TextField field = iteratorField.next();
			if(text != null){
				field.setText(text);
			}
		}
		Button button = new Button("OK");
		button.setOnAction(e->{
			String query = queryMaster.getUpdatingQuery(getFullStringsFromControls(texts, fields), getFullStringsFromStrings(columns, fieldsText));
			if(query == null){
				sendInformation("Can't get query for that operation");
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configureTable(table)){
				sendInformation("Have some problems with creating table");
			}
			window.close();
		});
		configureScene(root, null, texts, fields, null, List.of(button), false);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        window.setScene(scene);
		window.setTitle("Updating");
		window.setMaxWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		window.setMinWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		window.setMaxHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		window.setMinHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		window.initModality(Modality.APPLICATION_MODAL);
		window.show();
	}
	
	public boolean configureTable(TableView<TableData> table){
		String query = queryMaster.getSelectingQuery(null, null);
		if(query == null){
			sendInformation("Can't get quiery for that operation");
			return false;
		}
		List<String> result = new ArrayList<String>();
		String columns = queryMaster.getColumns();
		if(columns == null){
			sendInformation("Can't get columns from queryMaster");
			return false;
		}
		if(!result.add(columns)){
			sendInformation("Can't get result of select command");
			return false;
		}
		List<String> values = connecter.executeQuery(query, arrayStringsToList(columns.split(DELIM)));
		if(values == null || !result.addAll(values)){
			sendInformation("Can't get result of select command");
			return false;
		}
		String[] columnNames = result.get(0).split(DELIM);
		result.remove(0);
		Iterator<String> iterator = result.iterator();
		List<TableData> rows = new ArrayList<TableData>();
		while(iterator.hasNext()){
			String value = iterator.next();
			if(value == null){
				throw new NullPointerException("Null arguments for sendSelectingResult");
			}
			String[] valuesArray = value.split(DELIM);
			TableData data = new TableData(valuesArray.length);
			for(int i = 0; i < valuesArray.length; ++i){
				data.set(i, valuesArray[i]);
			}
			rows.add(data);
		}
		ObservableList<TableData> obseravableRows = FXCollections.observableArrayList(rows);
		table.setItems(obseravableRows);
		List<TableColumn<TableData, String>> tableColumns = new ArrayList<TableColumn<TableData, String>>();
		for(int i = 0; i < columnNames.length; ++i){
			TableColumn<TableData, String> columnName = new TableColumn<>(columnNames[i]);
			final int j = i;
			columnName.setCellValueFactory(p -> p.getValue().property(j));
			tableColumns.add(columnName);
		}
		table.getColumns().setAll(tableColumns);
		return true;
	}
	
	public void configureTable(List<String> result, TableView<TableData> table){
		String[] columnNames = result.get(0).split(DELIM);
		result.remove(0);
		Iterator<String> iterator = result.iterator();
		List<TableData> rows = new ArrayList<TableData>();
		while(iterator.hasNext()){
			String value = iterator.next();
			if(value == null){
				throw new NullPointerException("Null arguments for sendSelectingResult");
			}
			String[] valuesArray = value.split(DELIM);
			TableData data = new TableData(valuesArray.length);
			for(int i = 0; i < valuesArray.length; ++i){
				data.set(i, valuesArray[i]);
			}
			rows.add(data);
		}
		ObservableList<TableData> obseravableRows = FXCollections.observableArrayList(rows);
		table.setItems(obseravableRows);
		List<TableColumn<TableData, String>> columns = new ArrayList<TableColumn<TableData, String>>();
		for(int i = 0; i < columnNames.length; ++i){
			TableColumn<TableData, String> columnName = new TableColumn<>(columnNames[i]);
			final int j = i;
			columnName.setCellValueFactory(p -> p.getValue().property(j));
			columns.add(columnName);
		}
		table.getColumns().setAll(columns);
	}
	
	private void baseButtonConfiguration(String key, String nextMenu, List<TextField> textFields, List<CheckBox> flags,
	Map<String, Group> groups, TableView<TableData> table){
		if(!(key == null || nextMenu == null || groups == null)){
			if(groups.containsKey(nextMenu)){
				if(textFields != null){
					Iterator<TextField> iteratorTextField = textFields.iterator();
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
	
	private boolean configureConnectingButton(String key,Button button, String param, List<TextField> textFields, 
	List<CheckBox> flags, Map<String, Group> groups, TableView<TableData> table){
		int indexUserField = 0;
		int indexPasswordField = 1;
		int indexDatabaseField = 2;
		int textFieldsSize = 3;
		if(textFields.size() < textFieldsSize){
			return false;
		}
		button.setOnAction(e->{
			String user = textFields.get(indexUserField).getText();
			String password = textFields.get(indexPasswordField).getText();
			String database = textFields.get(indexDatabaseField).getText();
			StringBuilder result = new StringBuilder("");
			if(connecter.connect(user, password, database, result)){
				baseButtonConfiguration(key, param, textFields, flags, groups, table);
			}
			else{
				sendInformation(result.toString());
			}
		});
		return true;
	}
	
	private void configureScene(Group group, List<Text> texts, List<Text> textFields, List<TextField> fields, 
	List<CheckBox> flags, List<Button> buttons, boolean hasTable){
		if(group == null ){
			System.err.println("Null arguments for configureScene");
		}
		int countElements = ((texts == null) ? 0 : texts.size()) + 
		((textFields == null) ? 0 : textFields.size()) + 
		((flags == null) ? 0 : flags.size()) + 
		((buttons == null) ? 0 : buttons.size());
		double buttonX;
		double stepY = SCENE_HEIGHT / (countElements + 1);
		double currentY = stepY;
		double shiftX = 0;
		int delim;
		int currentElement = 1;
		int maxElements;
		if(countElements > MAX_ELEMENTS_IN_COLUM){
			buttonX = BUTTON_X / 2;
			delim = 4;
			stepY = SCENE_HEIGHT / (countElements / 2 + 1);
			maxElements = countElements / 2 + 1;
		}
		else{
			buttonX = BUTTON_X;
			delim = 2;
			stepY = SCENE_HEIGHT / (countElements + 1);
			maxElements = countElements;
		}
		if(hasTable){
			shiftX = SHIFT_X;
		}
		if(texts != null){
			Iterator<Text> iterText = texts.iterator();
			while(iterText.hasNext()){
				if(currentElement > maxElements){
					currentElement = 1;
					currentY = stepY;
					shiftX = SHIFT_X;
				}
				Text text = iterText.next();
				text.setX(shiftX + LEFT_LIMIT_X);
				text.setY(currentY);
				currentY += stepY;
				text.setFont(Font.font("Tahoma", FontWeight.NORMAL, ELEMENT_SIZE));
				group.getChildren().add(text);
				++currentElement;
			}
		}
		if(textFields != null && fields != null){
			Iterator<Text> iterTextField = textFields.iterator();
			Iterator<TextField> iterField = fields.iterator();
			while(iterTextField.hasNext() && iterField.hasNext()){
				if(currentElement > maxElements){
					currentElement = 1;
					currentY = stepY;
					shiftX = SHIFT_X;
				}
				TextField textField = iterField.next();
				textField.setTranslateX(shiftX + SCENE_WIDTH / delim);
				textField.setTranslateY(currentY);
				Text text = iterTextField.next();
				text.setX(shiftX + LEFT_LIMIT_X);
				text.setY(currentY);
				currentY += stepY;
				group.getChildren().add(textField);
				group.getChildren().add(text);
				++currentElement;
			}
		}
		if(flags != null){
			Iterator<CheckBox> iterFlag = flags.iterator();
			while(iterFlag.hasNext()){
				if(currentElement > maxElements){
					currentElement = 1;
					currentY = stepY;
					shiftX = SHIFT_X;
				}
				CheckBox flag = iterFlag.next();
				flag.setTranslateX(shiftX + LEFT_LIMIT_X);
				flag.setTranslateY(currentY);
				currentY += stepY;
				group.getChildren().add(flag);
				++currentElement;
			}
		}
		if(buttons != null){
			Iterator<Button> iterButton = buttons.iterator();
			while(iterButton.hasNext()){
				if(currentElement > maxElements){
					currentElement = 1;
					currentY = stepY;
					shiftX = SHIFT_X;
				}
				Button button = iterButton.next();
				button.setTranslateX(shiftX + buttonX);
				button.setTranslateY(currentY);
				currentY += stepY;
				button.setFont(Font.font("Tahoma", FontWeight.NORMAL, ELEMENT_SIZE));
				group.getChildren().add(button);
				++currentElement;
			}
		}
	}
	
	private List<String> arrayStringsToList(String[] array){
		if(array == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for(String string : array){
			list.add(string);
		}
		return list;
	}
	
	private double SCENE_HEIGHT = 600;
	private double SCENE_WIDTH = 700;
	private double INFORMATION_HEIGHT = 100;
	private double INFORMATION_WIDTH = 300;
	private double TABLE_HEIGHT = 400;
	private double TABLE_WIDTH = 400;
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
	private double SHIFT_X = 350;
	private double SCENE_CENTER = 250;
	private double BUTTON_X = 235;
	private double LEFT_LIMIT_X = 20;
	private double ELEMENT_SIZE = 15;
	private int MAX_ELEMENTS_IN_COLUM = 20;
	private String MENUES_FILE = "tourists/menues.xml";
	private String RESET_FILE = "SQL_commands_reset.txt";
	private String TEST_FILE = "SQL_commands_test.txt";
	private String DELIM = ";";
	
	private QueryMaster queryMaster = new QueryMaster();
	private ConnecterDataBase connecter = new ConnecterDataBase();
}