package tourists;

import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import java.util.*;

public class ElementsConfigurator{
	private ElementsConfigurator(){}
	
	public static ElementsConfigurator getInstance(){
		if(instance == null){
			instance = new ElementsConfigurator();
		}
		return instance;
	}
	
	public void createTexts(List<String> strings, List<Text> texts){
		if(strings == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTexts: strings is null");
		}
		if(texts == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTexts: texts is null");
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			texts.add(new Text(text));
		}
	}
	
	public void createFlags(List<String> strings, List<CheckBox> flags){
		if(strings == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createFlags: strings is null");
		}
		if(flags == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createFlags: flags is null");
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			flags.add(new CheckBox(text));
		}
	}
	
	public void setTextToFields(List<String> texts, List<TextField> fields){
		if(texts == null){
			throw new NullPointerException("Problem with ElementsConfigurator.setTextToFields: texts is null");
		}
		if(texts == null){
			throw new NullPointerException("Problem with ElementsConfigurator.setTextToFields: texts is null");
		}
		Iterator<String> iteratorText = texts.iterator();
		Iterator<TextField> iteratorField = fields.iterator();
		while(iteratorText.hasNext() && iteratorField.hasNext()){
			String text = iteratorText.next();
			TextField field = iteratorField.next();
			field.setText(text);
		}
	}
	
	public void createTextsWithFields(List<String> strings, List<Text> texts, List<TextField> fields){
		if(strings == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTextsWithFields: strings is null");
		}
		if(texts == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTextsWithFields: texts is null");
		}
		if(fields == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTextsWithFields: dropdowns is null");
		}
		Iterator<String> iterator = strings.iterator();
		while(iterator.hasNext()){
			String text = iterator.next();
			texts.add(new Text(text));
			fields.add(new TextField());
		}
	}
	
	public void createTextsWithDropdowns(List<List<String>> strings, List<Text> texts, List<ComboBox<String>> dropdowns){
		if(strings == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTextsWithDropdowns: strings is null");
		}
		if(texts == null){
			throw new NullPointerException("Problem with ElementsConfigurator.createTextsWithDropdowns: texts is null");
		}
		if(dropdowns == null){
			throw new NullPointerException("Problem with ElementsConfigurato.createTextsWithDropdowns: dropdowns is null");
		}
		Iterator<List<String>> iteratorDropdownData = strings.iterator();
		List<String> dropdownData;
		while(iteratorDropdownData.hasNext()){
			dropdownData = iteratorDropdownData.next();
			if(dropdownData.size() < MIN_ELEMENTS_IN_DROPDOWN){
				throw new RuntimeException("Problem with ElementsConfigurato.createTextsWithDropdowns: dropdown doesn't have elements");
			}
			Iterator<String> iteratorText = dropdownData.iterator();
			texts.add(new Text(iteratorText.next()));
			ComboBox<String> dropdown = new ComboBox<String>();
			configureDropdown(dropdown, iteratorText);
			dropdowns.add(dropdown);
		}
	}
	
	public boolean configureTable(TableView<TableData> table, QueryMaster queryMaster, ConnecterDataBase connecter, StringBuilder result){
		if(result == null){
			throw new NullPointerException("Problem in ElementsConfigurator.configureTable: result is null");
		}
		String query = queryMaster.getSelectingQuery(null, null);
		if(query == null){
			result.append("Can't get quiery for that operation");
			return false;
		}
		List<String> rows = new ArrayList<String>();
		String columns = queryMaster.getColumns();
		if(columns == null){
			result.append("Can't get columns from queryMaster");
			return false;
		}
		if(!rows.add(columns)){
			result.append("Can't get rows of select command");
			return false;
		}
		List<String> values = connecter.executeQuery(query, StringMaster.arrayStringsToList(columns.split(DELIM)), result);
		if(values == null || (!rows.addAll(values) && values.size() != 0)){
			result.append("Can't get rows of select command");
			return false;
		}
		configureTable(rows, table);
		return true;
	}
	
	public void configureTable(List<String> result, TableView<TableData> table){
		String[] columnNames = result.get(0).split(DELIM);
		result.remove(0);
		Iterator<String> iterator = result.iterator();
		List<TableData> rows = new ArrayList<TableData>();
		while(iterator.hasNext()){
			String value = iterator.next();
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
	
	public void configureScene(Group group, MenuElements elements, double height, double width){
		if(group == null ){
			throw new NullPointerException("Null arguments for configureScene");
		}
		int countElements = elements.getCount();
		int maxElementsInColumn = (int)height / ((int)ELEMENT_SIZE * 2);
		int columns = countElements / maxElementsInColumn;
		int countElementsInColumn = countElements / (columns + 1) + 1;
		double stepY = height / (countElementsInColumn + 1);
		double stepX = width / (columns + 1 + ((elements.hasTable()) ? COLUMNS_FOR_TABLE : 0));
		double currentY = stepY;
		double currentX = 0;
		if(elements.hasTable()){
			currentX = COLUMNS_FOR_TABLE * stepX;
		}
		ConfigurationParams params = new ConfigurationParams(countElementsInColumn, currentY, currentX, stepX, stepY);
		configureTexts(elements.getTexts(), group, params);
		configureTextsWithFields(elements.getTextsWithFields(), elements.getFields(), group, params);
		configureTextsWithDropdowns(elements.getTextsWithDropdowns(), elements.getDropdowns(), group, params);
		configureFlags(elements.getFlags(), group, params);
		configureButtons(elements.getButtons(), group, params);
	}
	
	private void configureTexts(List<Text> texts, Group group, ConfigurationParams params){
		if(group == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTexts: group is null");
		}
		if(params == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTexts: params are null");
		}
		if(texts != null){
			Iterator<Text> iterText = texts.iterator();
			while(iterText.hasNext()){
				Text text = iterText.next();
				text.setX(params.getCurrentX() + LEFT_LIMIT_X);
				text.setY(params.getCurrentY());
				params.incrementCurrentY();
				text.setFont(Font.font("Tahoma", FontWeight.NORMAL, ELEMENT_SIZE));
				group.getChildren().add(text);
				params.incrementElements();
			}
		}
	}
	
	private void configureDropdown(ComboBox<String> dropdown, Iterator<String> iteratorText){
		if(dropdown == null){
			throw new NullPointerException("Problem in ElementsConfigurator.configureDropdown: dropdown is null");
		}
		if(iteratorText == null){
			throw new NullPointerException("Problem in ElementsConfigurator.configureDropdown: iteratorText is null");
		}
		while(iteratorText.hasNext()){
			dropdown.getItems().add(iteratorText.next());
		}
		dropdown.setCellFactory(p -> {
			return new ListCell<>() {
				private final Text text;
				{
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					text = new Text();
				}

				@Override protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if(item == null || empty){ 
						setGraphic(null);
					}
					else{
						text.setText(item);
						setGraphic(text);
					}
				}
			};
		});
	}
	
	private void configureTextsWithFields(List<Text> texts, List<TextField> fields, Group group, ConfigurationParams params){
		if(group == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTextsWithFields: group is null");
		}
		if(params == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTextsWithFields: params are null");
		}
		if(texts != null && fields != null){
			Iterator<Text> iterTextField = texts.iterator();
			Iterator<TextField> iterField = fields.iterator();
			while(iterTextField.hasNext() && iterField.hasNext()){
				TextField textField = iterField.next();
				textField.setTranslateX(params.getCurrentX() + params.getStepX() / 2);
				textField.setTranslateY(params.getCurrentY());
				Text text = iterTextField.next();
				text.setX(params.getCurrentX() + LEFT_LIMIT_X);
				text.setY(params.getCurrentY());
				params.incrementCurrentY();
				group.getChildren().add(textField);
				group.getChildren().add(text);
				params.incrementElements();
			}
		}
	}
	
	private void configureTextsWithDropdowns(List<Text> texts, List<ComboBox<String>> dropdowns, Group group, ConfigurationParams params){
		if(group == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTextsWithFields: group is null");
		}
		if(params == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureTextsWithFields: params are null");
		}
		if(texts != null && dropdowns != null){
			Iterator<Text> iterTextDropdown = texts.iterator();
			Iterator<ComboBox<String>> iterDropdown = dropdowns.iterator();
			while(iterTextDropdown.hasNext() && iterDropdown.hasNext()){
				ComboBox<String> dropdown = iterDropdown.next();
				dropdown.setTranslateX(params.getCurrentX() + params.getStepX() / 2);
				dropdown.setTranslateY(params.getCurrentY());
				Text text = iterTextDropdown.next();
				text.setX(params.getCurrentX() + LEFT_LIMIT_X);
				text.setY(params.getCurrentY());
				params.incrementCurrentY();
				group.getChildren().add(dropdown);
				group.getChildren().add(text);
				params.incrementElements();
			}
		}
	}
	
	private void configureFlags(List<CheckBox> flags, Group group, ConfigurationParams params){
		if(group == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureFlags: group is null");
		}
		if(params == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureFlags: params are null");
		}
		if(flags != null){
			Iterator<CheckBox> iterFlag = flags.iterator();
			while(iterFlag.hasNext()){
				CheckBox flag = iterFlag.next();
				flag.setTranslateX(params.getCurrentX() + LEFT_LIMIT_X);
				flag.setTranslateY(params.getCurrentY());
				params.incrementCurrentY();
				group.getChildren().add(flag);
				params.incrementElements();
			}
		}
	}
	
	private void configureButtons(List<Button> buttons, Group group, ConfigurationParams params){
		if(group == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureFlags: group is null");
		}
		if(params == null){
			throw new NullPointerException("Can't execute ElementsConfigurator.configureFlags: params are null");
		}
		if(buttons != null){
			Iterator<Button> iterButton = buttons.iterator();
			while(iterButton.hasNext()){
				Button button = iterButton.next();
				button.setTranslateX(params.getCurrentX() + params.getStepX() / 3);
				button.setTranslateY(params.getCurrentY());
				params.incrementCurrentY();
				button.setFont(Font.font("Tahoma", FontWeight.NORMAL, ELEMENT_SIZE));
				group.getChildren().add(button);
				params.incrementElements();
			}
		}
	}
	
	class ConfigurationParams{
		public ConfigurationParams(int maxElementsInColumn, double currentY, double currentX, double stepX, double stepY){
			if(maxElementsInColumn <= 0){
				throw new RuntimeException("Can't create ConfigurationParams: maxElementsInColumn less or equal zero");
			}
			if(currentY <= 0){
				throw new RuntimeException("Can't create ConfigurationParams: currentY less or equal zero");
			}
			if(currentX < 0){
				throw new RuntimeException("Can't create ConfigurationParams: currentX less zero");
			}
			if(stepX <= 0){
				throw new RuntimeException("Can't create ConfigurationParams: stepX less or equal zero");
			}
			if(stepY <= 0){
				throw new RuntimeException("Can't create ConfigurationParams: stepY less or equal zero");
			}
			this.maxElementsInColumn = maxElementsInColumn;
			this.currentY = currentY;
			this.currentX = currentX;
			this.stepX = stepX;
			this.stepY = stepY;
		}
		
		public int getCurrentElement(){
			return currentElement;
		}
		
		public double getCurrentY(){
			return currentY;
		}
		
		public double getStepX(){
			return stepX;
		}
		
		public double getCurrentX(){
			return currentX;
		}
		
		public void incrementElements(){
			++currentElement;
			if(currentElement > maxElementsInColumn){
				currentElement = 1;
				currentY = stepY;
				currentX += stepX;
			}
		}
		
		public void incrementCurrentY(){
			currentY += stepY;
		}
		
		private int maxElementsInColumn;
		private double stepX;
		private double stepY;
		private double currentX;
		private double currentY;
		private int currentElement = 1;
	}
	
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
	private double LEFT_LIMIT_X = 20;
	private double ELEMENT_SIZE = 15;
	private int COLUMNS_FOR_TABLE = 2;
	private int MIN_ELEMENTS_IN_DROPDOWN = 2;
	
	private String DELIM = ";";
	private static ElementsConfigurator instance;
}