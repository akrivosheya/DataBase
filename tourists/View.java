package tourists;

import javafx.scene.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.util.*;

public class View extends Application {
    @Override
    public void start(Stage primaryStage) {
		Group root = new Group();
		Controller controller = new Controller();
		Map<String, Group> groups = new HashMap<String, Group>();
		groups = controller.getGroups(table);
		if(groups == null){
			System.err.println("Can't get menu data");
			return;
		}
		groups.forEach((String k, Group m)->{
			root.getChildren().add(m);
		});
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        primaryStage.setScene(scene);
		primaryStage.setTitle("Tourist club");
		primaryStage.setMaxHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		primaryStage.setMinHeight(SCENE_HEIGHT + TITLE_HEIGHT);
		primaryStage.setMaxWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		primaryStage.setMinWidth(SCENE_WIDTH + OUTLINE_WIDTH);
		primaryStage.show();
    }

	private double SCENE_HEIGHT = 600;
	private double SCENE_WIDTH = 700;
	private double TITLE_HEIGHT = 35;
	private double OUTLINE_WIDTH = 15;
	
	private ConnecterDataBase connecter = new ConnecterDataBase();
	private TableView<TableData> table = new TableView<>();
}