package tourists;

import javafx.scene.text.*;
import javafx.scene.control.*;
import java.util.*;

public class StringMaster{
	public static Map<String, String> getMapFromTextsAndFields(List<Text> texts, List<TextField> fields, boolean withNull){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<TextField> iteratorField = fields.iterator();
		Iterator<Text> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			TextField field = iteratorField.next();
			Text text = iteratorText.next();
			if(field.getText().length() > 0 && !field.getText().isBlank() && (withNull || !isNull(field.getText()))){
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
	
	public static Map<String, String> getMapFromStrings(List<String> texts, List<String> fields, boolean withNull){
		if(fields == null || texts == null){
			return null;
		}
		Iterator<String> iteratorField = fields.iterator();
		Iterator<String> iteratorText = texts.iterator();
		Map<String, String> strings = new HashMap<String, String>();
		while(iteratorField.hasNext() && iteratorText.hasNext()){
			String field = iteratorField.next();
			String text = iteratorText.next();
			if(field.length() > 0 && !field.isBlank() && (withNull || !isNull(field))){
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
	
	public static boolean isHour(String string){
		if(string == null){
			return true;
		}
		if(string.length() < HOUR_LENGTH){
			return false;
		}
		int i = 0;
		if(!isNumber(string, i, HOUR_PART_LENGTH)){
			return false;
		}
		i += HOUR_PART_LENGTH;
		if(string.charAt(i) != ':'){
			return false;
		}
		++i;
		if(!isNumber(string, i, i + HOUR_PART_LENGTH)){
			return false;
		}
		if(string.length() > HOUR_LENGTH && !string.substring(i, string.length()).isBlank()){
			return false;
		}
		return true;
	}
	
	public static boolean isWeekDay(String string){
		if(string == null){
			return true;
		}
		switch(string){
			case "Sunday":
			case "Monday":
			case "Tuesday":
			case "Wednesday":
			case "Thursday":
			case "Friday":
			case "Seturday":
				return true;
			default:
				return false;
		}
	}
	
	public static int getDayFromWeekDay(String string){
		if(string == null){
			return 0;
		}
		switch(string){
			case "Sunday":
				return 7;
			case "Monday":
				return 1;
			case "Tuesday":
				return 2;
			case "Wednesday":
				return 3;
			case "Thursday":
				return 4;
			case "Friday":
				return 5;
			case "Seturday":
				return 6;
			default:
				return 0;
		}
	}
	
	public static String getWeekDays(){
		return "Sunday; Monday; Tuesday; Wednesday; Thursday, Friday; Seturday";
	}
	
	public static int getHour(String string){
		if(string == null){
			throw new NullPointerException("Problem in StringMaster.getHour: string is null");
		}
		return Integer.decode(string.substring(0, HOUR_PART_LENGTH));
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
	
	public static boolean isNull(String string){
		if(string == null){
			return true;
		}
		if(string.length() < NULL_LENGTH){
			return false;
		}
		String nullPart = string.substring(0, NULL_LENGTH).toLowerCase();
		if(!nullPart.equals("null")){
			return false;
		}
		if(string.length() >= NULL_LENGTH && !string.substring(NULL_LENGTH, string.length()).isBlank()){
			return false;
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
	private static int HOUR_PART_LENGTH = 2;
	private static int MONTH_LENGTH = 2;
	private static int YEAR_LENGTH = 4;
	private static int DATE_LENGTH = 10;
	private static int HOUR_LENGTH = 5;
	private static int NULL_LENGTH = 4;
}