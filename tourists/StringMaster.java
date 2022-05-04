package tourists;

import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

public class StringMaster{
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
			if(field.getText().length() > 0){
				strings.put(text.getText(), field.getText());
			}
		}
		return strings;
	}
	
	public static Map<String, String> getMapFormTextsAndDropdowns(List<Text> texts, List<ComboBox<String>> dropdowns){
		if(dropdowns == null || texts == null){
			return null;
		}
		Iterator<ComboBox<String>> iteratorDropdown = dropdowns.iterator();
		Iterator<Text> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorDropdown.hasNext() && iteratorText.hasNext()){
			ComboBox<String> dropdown = iteratorDropdown.next();
			Text text = iteratorText.next();
			String dropDownValue = dropdown.getSelectionModel().getSelectedItem();
			if(dropDownValue != null){
				strings.put(text.getText(), dropDownValue);
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
	
	public static boolean isDate(String string){
		if(string == null){
			return true;
		}
		if(string.length() < DATE_LENGTH){
			return false;
		}
		int i = 0;
		if(!isNumber(string, i, DAY_LENGTH)){
			return false;
		}
		i += DAY_LENGTH;
		if(string.charAt(i) != '.'){
			return false;
		}
		++i;
		if(!isNumber(string, i, i + MONTH_LENGTH)){
			return false;
		}
		i += MONTH_LENGTH;
		if(string.charAt(i) != '.'){
			return false;
		}
		++i;
		if(!isNumber(string, i, i + YEAR_LENGTH)){
			return false;
		}
		if(string.length() > DATE_LENGTH && !string.substring(i, string.length()).isBlank()){
			return false;
		}
		return true;
	}
	
	public static boolean isFlag(String flag){
		if(flag == null){
			return true;
		}
		if(flag.isEmpty() || flag.isBlank()){
			return false;
		}
		char digit = flag.charAt(0);
		if(!(Character.isDigit(digit) && digit <= '1')){
			return false;
		}
		if(flag.length() > 1 && !flag.substring(1, flag.length()).isBlank()){
			return false;
		}
		return true;
	}
	
	public static boolean isSex(String flag){
		if(flag == null){
			return true;
		}
		if(flag.isEmpty() || flag.isBlank()){
			return false;
		}
		char sex = flag.charAt(0);
		if(!(sex == 'M' || sex == 'W')){
			return false;
		}
		if(flag.length() > 1 && !flag.substring(1, flag.length()).isBlank()){
			return false;
		}
		return true;
	}
	
	public static boolean isNumber(String number){
		if(number == null){
			return true;
		}
		char[] arrayChar = number.toCharArray();
		for(char character : arrayChar){
			if(!Character.isDigit(character)){
				return false;
			}
		}
		return true;
	}
	
	private static boolean isNumber(String string, int begin, int end){
		if(string == null){
			return false;
		}
		if(end < begin || string.length() < begin){
			return false;
		}
		for(int i = begin; i < end; ++i){
			if(!Character.isDigit(string.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	private static int DAY_LENGTH = 2;
	private static int MONTH_LENGTH = 2;
	private static int YEAR_LENGTH = 4;
	private static int DATE_LENGTH = 10;
}