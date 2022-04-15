package tourists;

import javafx.beans.property.*;
import java.util.*;

public class TableData {
	public TableData(int rowSize) {
		columns = new StringProperty[rowSize];
	}
	
	public void set(int index, String value){
		if(index > columns.length){
			return;
		}
		property(index).set(value);
	}
	
	public String get(int index){
		if(index > columns.length){
			return null;
		}
		return property(index).get();
	}
	
	public int getLength(){
		if(columns == null){
			return 0;
		}
		return columns.length;
	}
	
	public StringProperty property(int index){
		if(index > columns.length){
			return null;
		}
		if(null == columns[index]){
			columns[index] = new SimpleStringProperty(this, "column" + index);
		}
		return columns[index];
	}
	
	public List<String> getListStrings(){
		if(columns == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for(StringProperty string : columns){
			list.add(string.get());
		}
		return list;
	}
	
	private StringProperty[] columns;
}