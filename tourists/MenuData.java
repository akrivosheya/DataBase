package tourists;

import java.util.List;

class MenuData{
	public MenuData(String name, boolean hasTable, boolean isStarting){
		this.name = name;
		this.hasTable = hasTable;
		this.isStarting = isStarting;
	}
	
	@Override
	public String toString(){
		return name + "\nTexts: " + texts + "\nFields: " + fields + "\nFlags: " + flags + "\nButtons: " + buttons;
	}
	
	public String getName(){
		return name;
	}
	
	public List<String> getTexts(){
		return texts;
	}
	
	public List<String> getFields(){
		return fields;
	}
	
	public List<String> getFlags(){
		return flags;
	}
	
	public List<ButtonData> getButtons(){
		return buttons;
	}
	
	public void setTexts(List<String> texts){
		this.texts = texts;
	}
	
	public void setFields(List<String> fields){
		this.fields = fields;
	}
	
	public void setFlags(List<String> flags){
		this.flags = flags;
	}
	
	public void setButtons(List<ButtonData> buttons){
		this.buttons = buttons;
	}
	
	public boolean hasTable(){
		return hasTable;
	}
	
	public boolean isStarting(){
		return isStarting;
	}
	
	private boolean hasTable;
	private boolean isStarting;
	private String name;
	private List<String> texts;
	private List<String> fields;
	private List<String> flags;
	private List<ButtonData> buttons;
}