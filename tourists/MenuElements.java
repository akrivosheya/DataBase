package tourists;

import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

public class MenuElements{
	public void setHasTable(boolean hasTable){
		this.hasTable = hasTable;
	}
	
	public void setTexts(List<Text> texts){
		this.texts = texts;
	}
	
	public void setTextsWithFields(List<Text> textsWithFields){
		this.textsWithFields = textsWithFields;
	}
	
	public void setFields(List<TextField> fields){
		this.fields = fields;
	}
	
	public void setFlags(List<CheckBox> flags){
		this.flags = flags;
	}
	
	public void setButtons(List<Button> buttons){
		this.buttons = buttons;
	}
	
	public boolean hasTable(){
		return hasTable;
	}
	
	public List<Text> getTexts(){
		return texts;
	}
	
	public List<Text> getTextsWithFields(){
		return textsWithFields;
	}
	
	public List<TextField> getFields(){
		return fields;
	}
	
	public List<CheckBox> getFlags(){
		return flags;
	}
	
	public List<Button> getButtons(){
		return buttons;
	}
	
	public int getCount(){
		return ((texts == null) ? 0 : texts.size()) + 
		((textsWithFields == null) ? 0 : textsWithFields.size()) + 
		((flags == null) ? 0 : flags.size()) + 
		((buttons == null) ? 0 : buttons.size());
	}

	private boolean hasTable = false;
	private List<Text> texts;
	private List<Text> textsWithFields;
	private List<TextField> fields;
	private List<CheckBox> flags;
	private List<Button> buttons;
}