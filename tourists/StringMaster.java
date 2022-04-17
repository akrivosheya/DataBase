package tourists;

import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

class StringMaster{
	public static Map<String, String> getMapFormTextsAndFields(List<Text> texts, List<TextField> fields){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<TextField> iteratorField = fields.iterator();
		Iterator<Text> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			TextField field = iteratorField.next();
			Text text = iteratorText.next();
			if(field == null || text == null){
				return null;
			}
			if(field.getText().length() > 0){
				strings.put(text.getText(), field.getText());
			}
		}
		return strings;
	}
	
	public static Map<String, String> getMapFromStrings(List<String> texts, List<String> fields){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<String> iteratorField = fields.iterator();
		Iterator<String> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			String field = iteratorField.next();
			String text = iteratorText.next();
			if(field == null || text == null){
				return null;
			}
			if(field.length() > 0){
				strings.put(text, field);
			}
		}
		return strings;
	}
	
	public static List<String> getFlags(List<CheckBox> flags){
		if(flags == null){
			return null;
		}
		Iterator<CheckBox> iterator = flags.iterator();
		List<String> choosenFlags = new ArrayList<String>();
		while(iterator.hasNext()){
			CheckBox flag = iterator.next();
			if(flag == null){
				return null;
			}
			if(flag.isSelected()){
				choosenFlags.add(flag.getText());
			}
		}
		return choosenFlags;
	}
	
	public static List<String> arrayStringsToList(String[] array){
		if(array == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		for(String string : array){
			list.add(string);
		}
		return list;
	}
}