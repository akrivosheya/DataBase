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
			MenuElements elements = new MenuElements();
			elements.setHasTable(menuData.hasTable());
			elements.setTexts(texts);
			elements.setTextsWithFields(textFields);
			elements.setFields(fields);
			elements.setFlags(flags);
			Iterator<ButtonData> iteratorButtonData = menuData.getButtons().iterator();
			ButtonData buttonData;
			while(iteratorButtonData.hasNext()){
				buttonData = iteratorButtonData.next();
				if(buttonData == null){
					throw new NullPointerException("Problem with Controller.getGroups: buttonData is null");
				}
				Button button = createButton(buttonData, key, groups, elements, table);
				if(button == null){
					System.err.println("Can't create button " + buttonData.getText() + " for menu " + key);
				}
				else{
					buttons.add(button);
				}
			}
			elements.setButtons(buttons);
			configurator.configureScene(group, elements, SCENE_HEIGHT, SCENE_WIDTH);
			if(!menuData.isStarting()){
				group.setVisible(false);
			}
			groups.put(key, group);
		});
		return groups;
	}
	
	private Button createButton(ButtonData buttonData, String menu, Map<String, Group> groups, 
	MenuElements elements, TableView<TableData> table){
		if(buttonData == null){
			throw new NullPointerException("Problem with Controller.configureButton: buttonData is null");
		}
		if(menu == null){
			throw new NullPointerException("Problem with Controller.configureButton: menu is null");
		}
		if(groups == null){
			throw new NullPointerException("Problem with Controller.configureButton: groups is null");
		}
		if(elements == null){
			throw new NullPointerException("Problem with Controller.configureButton: elements is null");
		}
		if(table == null){
			throw new NullPointerException("Problem with Controller.configureButton: table is null");
		}
		String text = buttonData.getText();
		if(text == null){
			throw new NullPointerException("Problem with Controller.configureButton: text in buttonData is null");
		}
		Button button = new Button(text);
		String helperName = buttonData.getHelper();
		String param = buttonData.getParam();
		if(helperName == null){
			throw new NullPointerException("Problem with Controller.configureButton: type in buttonData is null");
		}
		if(param == null){
			throw new NullPointerException("Problem with Controller.configureButton: param in buttonData is null");
		}
		ButtonHelper helper = (ButtonHelper)HelperFactory.getInstance().getHelper(helperName);
		if(helper == null){
			System.err.println("Problem int Controller.configureButton: can't get helper " + helperName);
			return null;
		}
		button.setOnAction(e->{
			try{
				helper.doOnAction(menu, param, groups, elements, table, connecter, queryMaster);
			}
			catch(RuntimeException ee){
				System.err.println(text + " button can't do action: " + ee.getMessage());
				windowOpener.sendInformation("Some proplems with action\nPlease check menues.xml");
			}
		});
		return button;
	}
	
	private double SCENE_HEIGHT = 600;
	private double SCENE_WIDTH = 700;
	private String MENUES_FILE = "tourists/menues.xml";
	
	private QueryMaster queryMaster = new QueryMaster();
	private ConnecterDataBase connecter = new ConnecterDataBase();
	private WindowOpener windowOpener = WindowOpener.getInstance();
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
}