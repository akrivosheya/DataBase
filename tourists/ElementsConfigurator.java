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
	
	public void createFlags(List<String> strings, List<CheckBox> flags){
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
	
	public void setTextToFields(List<String> texts, List<TextField> fields){
		if(texts == null || fields == null){
			System.err.println("Null arguments for WindowOpener.setTextToFields");
			return;
		}
		Iterator<String> iteratorText = texts.iterator();
		Iterator<TextField> iteratorField = fields.iterator();
		while(iteratorText.hasNext() && iteratorField.hasNext()){
			String text = iteratorText.next();
			TextField field = iteratorField.next();
			if(text != null){
				field.setText(text);
			}
		}
	}
	
	public void createTextsWithFields(List<String> strings, List<Text> texts, List<TextField> fields){
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
	
	public boolean configureTable(TableView<TableData> table, QueryMaster queryMaster, ConnecterDataBase connecter){
		String query = queryMaster.getSelectingQuery(null, null);
		if(query == null){
			System.err.println("Can't get quiery for that operation");
			return false;
		}
		List<String> result = new ArrayList<String>();
		String columns = queryMaster.getColumns();
		if(columns == null){
			System.err.println("Can't get columns from queryMaster");
			return false;
		}
		if(!result.add(columns)){
			System.err.println("Can't get result of select command");
			return false;
		}
		List<String> values = connecter.executeQuery(query, StringMaster.arrayStringsToList(columns.split(DELIM)));
		if(values == null || !result.addAll(values)){
			System.err.println("Can't get result of select command");
			return false;
		}
		configureTable(result, table);
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
	
	public void configureScene(Group group, List<Text> texts, List<Text> textFields, List<TextField> fields, 
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
	private String DELIM = ";";
	private static ElementsConfigurator instance;
}