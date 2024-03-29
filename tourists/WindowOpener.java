package tourists;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

public class WindowOpener{
	private WindowOpener(){}
	
	public static WindowOpener getInstance(){
		if(instance == null){
			instance = new WindowOpener();
		}
		return instance;
	}
	
	public void sendInformation(String information){
		if(information == null){
			throw new NullPointerException("Problem in WindowOpener.sendInformation: information is null");
		}
		int linesCount = information.length() / MAX_INFORMATION_LENGTH;
		StringBuilder newInformation = new StringBuilder(information);
		for(int i = 1; i < linesCount; ++i){
			newInformation.insert(i * MAX_INFORMATION_LENGTH, NEW_LINE);
		}
		Group root = new Group();
		Text text = new Text(0, INFORMATION_HEIGHT / 2, newInformation.toString());
		text.setFont(Font.font("Tahoma", FontWeight.NORMAL, TEXT_SIZE));
		root.getChildren().add(text);
		Stage window = new Stage();
		showWindow(window, root, INFORMATION_WIDTH, INFORMATION_HEIGHT + TEXT_SIZE * linesCount, "Information");
	}
	
	public void sendSelectingResult(List<String> result, TableView<TableData> table){
		if(result == null || result.size() == 0){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Stage window = new Stage();
		Group root = new Group();
		configurator.configureTable(result, table);
		root.getChildren().add(table);
		showWindow(window, root, TABLE_WIDTH, TABLE_HEIGHT, "Result");
	}
	
	public void openCreatingWindow(List<String> columns, List<String> values, TableView<TableData> table, 
	QueryMaster queryMaster, ConnecterDataBase connecter){
		if(columns == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		configurator.createTextsWithFields(columns, texts, fields);
		if(values != null){
			configurator.setTextToFields(values, fields);
		}
		Button button = new Button("OK");
		button.setOnAction(e->{
			StringBuilder message = new StringBuilder("");
			String query = queryMaster.getInsertingQuery(StringMaster.getMapFromTextsAndFields(texts, fields, false), message);
			if(query == null){
				sendInformation(message.toString());
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configurator.configureTable(table, queryMaster, connecter, message)){
				sendInformation(message.toString());
			}
		});
		MenuElements elements = new MenuElements();
		elements.setTextsWithFields(texts);
		elements.setFields(fields);
		elements.setButtons(List.of(button));
		configurator.configureScene(root, elements, DEFAULT_HEIGHT, DEFAULT_WIDTH);
		Stage window = new Stage();
		showWindow(window, root, DEFAULT_WIDTH, DEFAULT_HEIGHT, "Creating");
	}
	
	public void openUpdatingWindow(List<String> updatingColumns, List<String> selectingColumns, List<String> selectingValues, TableView<TableData> table, 
	QueryMaster queryMaster, ConnecterDataBase connecter){
		if(selectingColumns == null || selectingValues == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		configurator.createTextsWithFields(updatingColumns, texts, fields);
		List<String> updatingValues = queryMaster.getUpdatingFromSelecting(selectingValues);
		configurator.setTextToFields(updatingValues, fields);
		Button button = new Button("OK");
		Stage window = new Stage();
		button.setOnAction(e->{
			StringBuilder message = new StringBuilder("");
			String query = queryMaster.getUpdatingQuery(StringMaster.getMapFromTextsAndFields(texts, fields, false), 
			StringMaster.getMapFromStrings(selectingColumns, selectingValues, true), message);
			if(query == null){
				sendInformation(message.toString());
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configurator.configureTable(table, queryMaster, connecter, message)){
				sendInformation(message.toString());
			}
			window.close();
		});
		MenuElements elements = new MenuElements();
		elements.setTextsWithFields(texts);
		elements.setFields(fields);
		elements.setButtons(List.of(button));
		configurator.configureScene(root, elements, DEFAULT_HEIGHT, DEFAULT_WIDTH);
        showWindow(window, root, DEFAULT_WIDTH, DEFAULT_HEIGHT, "Updating");
	}
	
	public void showWindow(Stage window, Group root, double width, double height, String title){
		if(window == null || root == null || title == null){
			System.err.println("Null arguments for WindowOpener.showWindow");
			return;
		}
		if(width <= 0 || height <= 0){
			System.err.println("Width or height are less or equal 0 in WindowOpener.showWindow");
			return;
		}
        Scene scene = new Scene(root, width, height, Color.WHITE);
        window.setScene(scene);
		window.setTitle(title);
		window.setMaxWidth(width + OUTLINE_WIDTH);
		window.setMinWidth(width + OUTLINE_WIDTH);
		window.setMaxHeight(height + TITLE_HEIGHT);
		window.setMinHeight(height + TITLE_HEIGHT);
		try{
			window.initModality(Modality.APPLICATION_MODAL);
		}
		catch(Exception e){
		}
		window.show();
	}
	
	private double INFORMATION_HEIGHT = 100;
	private double INFORMATION_WIDTH = 300;
	private double DEFAULT_HEIGHT = 600;
	private double DEFAULT_WIDTH = 700;
	private double TABLE_HEIGHT = 400;
	private double TABLE_WIDTH = 400;
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
	private double TEXT_SIZE = 15;
	private int MAX_INFORMATION_LENGTH = 20;
	private char NEW_LINE = '\n';
	private static WindowOpener instance;
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
}