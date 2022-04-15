package tourists;

class ButtonData{
	public ButtonData(String text, String type, String param){
		this.text = text;
		this.type = type;
		this.param = param;
	}
	
	@Override
	public String toString(){
		return text + " " + type + " " + param;
	}
	
	public String getText(){
		return text;
	}
	
	public String getType(){
		return type;
	}
	
	public String getParam(){
		return param;
	}
	
	private String text;
	private String type;
	private String param;
}