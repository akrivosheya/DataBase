package tourists;

class ButtonData{
	public ButtonData(String text, String helper, String param){
		this.text = text;
		this.helper = helper;
		this.param = param;
	}
	
	@Override
	public String toString(){
		return text + " " + helper + " " + param;
	}
	
	public String getText(){
		return text;
	}
	
	public String getHelper(){
		return helper;
	}
	
	public String getParam(){
		return param;
	}
	
	private String text;
	private String helper;
	private String param;
}