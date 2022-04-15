package tourists;

import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;

public class ElementsConfigurator{
	private ElementsConfigurator(){}
	
	public static ElementsConfigurator getInstance(){
		if(instance == null){
			instance = new ElementsConfigurator();
		}
		return instance;
	}
	
	public void setTextToFields(List<String> texts, List<TextField> textFields){
		if(texts == null || textFields == null){
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
	
	private ElementsConfigurator instance;
}