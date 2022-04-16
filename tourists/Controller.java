package tourists;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import java.util.*;

import tourists.helpers.*;

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
			configurator.createTexts(menuData.getTexts(), texts);
			configurator.createTextsWithFields(menuData.getFields(), textFields, fields);
			configurator.createFlags(menuData.getFlags(), flags);
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
			configurator.configureScene(group, texts, textFields, fields, flags, buttons, menuData.hasTable());
			if(!menuData.isStarting()){
				group.setVisible(false);
			}
			groups.put(key, group);
		});
		return groups;
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
					if(!configurator.configureTable(table, queryMaster, connecter)){
						windowOpener.sendInformation("Have some problems with creating table");
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
					windowOpener.openCreatingWindow(StringMaster.arrayStringsToList(queryMaster.getColumns().split(DELIM)), values, table, queryMaster, connecter);
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
						windowOpener.sendInformation("Please choose row that you want to update");
						return;
					}
					windowOpener.openUpdatingWindow(StringMaster.arrayStringsToList(queryMaster.getColumns().split(DELIM)), values, table, queryMaster, connecter);
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
						windowOpener.sendInformation("Please choose row that you want to delete");
						return;
					}
					List<String> columns = StringMaster.arrayStringsToList(queryMaster.getColumns().split(DELIM));
					if(columns == null){
						windowOpener.sendInformation("Can't get columns for this table");
						return;
					}
					String query = queryMaster.getDeletingQuery(StringMaster.getMapFromStrings(columns, values));
					if(query == null){
						windowOpener.sendInformation("Can't get query for that operation");
						return;
					}
					String result = connecter.sendQueries(List.of(query));
					windowOpener.sendInformation(result);
					if(!configurator.configureTable(table, queryMaster, connecter)){
						windowOpener.sendInformation("Have some problems with creating table");
					}
				});
				return true;
			case "select":
				button.setOnAction(e->{
					QueryHelper helper = (QueryHelper)HelperFactory.getInstance().getHelper(param);
					queryMaster.setHelper(helper);
					String query = queryMaster.getSelectingQuery(StringMaster.getMapFormTextsAndFields(textFields, fields), StringMaster.getFlags(flags));
					if(query == null){
						windowOpener.sendInformation("Can't get quiery for that operation");
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
					if(values == null || !result.addAll(values)){
						windowOpener.sendInformation("Can't get result of select command");
						return;
					}
					windowOpener.sendSelectingResult(result, table);
				});
				return true;
			case "test":
				button.setOnAction(e->{
					List<String> queries = queryMaster.getQueries(RESET_FILE);
					if(queries == null){
						windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
						return;
					}
					List<String> queriesTest = queryMaster.getQueries(TEST_FILE);
					if(queriesTest == null){
						windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
						return;
					}
					if(!queries.addAll(queriesTest)){
						windowOpener.sendInformation("Can't get quieries");
						return;
					}
					String result = connecter.sendQueries(queries);
					windowOpener.sendInformation(result);
				});
				return true;
			case "reset":
				button.setOnAction(e->{
					List<String> queries = queryMaster.getQueries(RESET_FILE);
					if(queries == null){
						windowOpener.sendInformation("Can't get quieries from file " + RESET_FILE);
					}
					else{
						String result = connecter.sendQueries(queries);
						windowOpener.sendInformation(result);
					}
				});
				return true;
			default:
				return false;
		}
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
				windowOpener.sendInformation("Is not ready");
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
				windowOpener.sendInformation(result.toString());
			}
		});
		return true;
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
	private WindowOpener windowOpener = WindowOpener.getInstance();
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
}