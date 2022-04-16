package tourists;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

import tourists.*;

public class WindowOpener{
	private WindowOpener(){}
	
	public static WindowOpener getInstance(){
		if(instance == null){
			instance = new WindowOpener();
		}
		return instance;
	}
	
	public void sendInformation(String information){
		Group root = new Group();
		Text text = new Text(0, INFORMATION_HEIGHT / 2, information);
		text.setFont(Font.font("Tahoma", FontWeight.NORMAL, TEXT_SIZE));
		root.getChildren().add(text);
		Stage window = new Stage();
		showWindow(window, root, INFORMATION_WIDTH, INFORMATION_HEIGHT, "Information");
	}
	
	public void openCreatingWindow(List<String> columns, List<String> fieldsText, TableView<TableData> table, QueryMaster queryMaster, ConnecterDataBase connecter){
		if(columns == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		configurator.createTextsWithFields(columns, texts, fields);
		if(fieldsText != null){
			configurator.setTextToFields(fieldsText, fields);
		}
		Button button = new Button("OK");
		button.setOnAction(e->{
			String query = queryMaster.getInsertingQuery(configurator.getMapFormTextsAndFields(texts, fields));
			if(query == null){
				sendInformation("Can't get query for that operation");
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configurator.configureTable(table, queryMaster, connecter)){
				sendInformation("Have some problems with creating table");
			}
		});
		configurator.configureScene(root, null, texts, fields, null, List.of(button), false);
		Stage window = new Stage();
		showWindow(window, root, DEFAULT_WIDTH, DEFAULT_HEIGHT, "Creating");
	}
	
	public void openUpdatingWindow(List<String> columns, List<String> fieldsText, TableView<TableData> table, QueryMaster queryMaster, ConnecterDataBase connecter){
		if(columns == null || fieldsText == null){
			throw new NullPointerException("Null arguments for sendSelectingResult");
		}
		Group root = new Group();
		List<Text> texts = new ArrayList<Text>();
		List<TextField> fields = new ArrayList<TextField>();
		configurator.createTextsWithFields(columns, texts, fields);
		configurator.setTextToFields(fieldsText, fields);
		Button button = new Button("OK");
		Stage window = new Stage();
		button.setOnAction(e->{
			String query = queryMaster.getUpdatingQuery(configurator.getMapFormTextsAndFields(texts, fields), configurator.getMapFromStrings(columns, fieldsText));
			if(query == null){
				sendInformation("Can't get query for that operation");
				return;
			}
			List<String> queries = List.of(query);
			String result = connecter.sendQueries(queries);
			sendInformation(result);
			if(!configurator.configureTable(table, queryMaster, connecter)){
				sendInformation("Have some problems with creating table");
			}
			window.close();
		});
		configurator.configureScene(root, null, texts, fields, null, List.of(button), false);
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
		window.initModality(Modality.APPLICATION_MODAL);
		window.show();
	}
	
	private double INFORMATION_HEIGHT = 100;
	private double INFORMATION_WIDTH = 300;
	private double DEFAULT_HEIGHT = 600;
	private double DEFAULT_WIDTH = 700;
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
	private double TEXT_SIZE = 15;
	private static WindowOpener instance;
	private ElementsConfigurator configurator = ElementsConfigurator.getInstance();
}