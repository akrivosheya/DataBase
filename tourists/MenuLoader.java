package tourists;

import java.io.*;
import java.util.*;
import javax.xml.stream.*;

class MenuLoader{
	public boolean getMapMenuData(String configFileName, Map<String, MenuData> menuesData){
		if(configFileName == null || menuesData == null){
			return false;
		}
		else{
			List<String> texts = new ArrayList<String>();
			List<String> fields = new ArrayList<String>();
			List<String> flags = new ArrayList<String>();
			List<ButtonData> buttons = new ArrayList<ButtonData>();
			StringBuilder name = new StringBuilder("");
			boolean hasTable = false;
			boolean isStarting = false;
			XMLStreamReader reader = null;
			File file = new File(configFileName);
			if(!file.exists()){
				System.err.println("file " + configFileName + " doesn't exist");
				return false;
			}
			try{
				reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(file));
				int indexMessage = 0;
				if(reader.hasNext() && reader.next() == XMLStreamConstants.START_ELEMENT && 
				reader.getName().toString().equals("menues")){
					if(!doubleNext(reader)){
						return false;
					}
					while(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
					reader.getName().toString().equals("menu")){
						if(!doubleNext(reader)){
							return false;
						}
						if(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
						reader.getName().toString().equals("name")){
							if(!(getText(reader, "name", name))){
								return false;
							}
						}
						isStarting = getFlag(reader, "isStarting");
						hasTable = getFlag(reader, "hasTable");
						if(!getTexts(reader, texts, "texts", "text")){
							return false;
						}
						if(!getTexts(reader, fields, "fields", "field")){
							return false;
						}
						if(!getTexts(reader, flags, "flags", "flag")){
							return false;
						}
						if(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
						reader.getName().toString().equals("buttons")){
							if(!(getButtons(reader, buttons) && reader.getEventType() == XMLStreamConstants.END_ELEMENT && 
							reader.getName().toString().equals("buttons"))){
								return false;
							}
						}
						if(!(doubleNext(reader) && reader.getEventType() == XMLStreamConstants.END_ELEMENT &&
						reader.getName().toString().equals("menu"))){
							return false;
						}
						if(!doubleNext(reader)){
							return false;
						}
						if(!menuesData.containsKey(name.toString())){
							MenuData menuData = new MenuData(name.toString(), hasTable, isStarting);
							menuData.setTexts(List.copyOf(texts));
							menuData.setFields(List.copyOf(fields));
							menuData.setFlags(List.copyOf(flags));
							menuData.setButtons(List.copyOf(buttons));
							menuesData.put(name.toString(), menuData);
							name.delete(0, name.length());
							texts.clear();
							fields.clear();
							flags.clear();
							buttons.clear();
							hasTable = false;
						}
						else{
							return false;
						}
					}
					if(!(reader.getEventType() == XMLStreamConstants.END_ELEMENT &&
					reader.getName().toString().equals("menues"))){
						return false;
					}
				}
				else{
					return false;
				}
			}
			catch(XMLStreamException | FileNotFoundException e){
				System.err.println("Problems with parsing file: " + e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	private boolean getTexts(XMLStreamReader reader, List<String> texts, String param) throws XMLStreamException{
		if(reader == null || texts == null || param == null){
			System.err.println("Null arguments for getTexts");
			return false;
		}
		if(!doubleNext(reader)){
			return false;
		}
		StringBuilder text = new StringBuilder("");
		while(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
		reader.getName().toString().equals(param)){
			if(getText(reader, param, text)){
				texts.add(text.toString());
				text.delete(0, text.length());
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	private boolean getFlag(XMLStreamReader reader, String param) throws XMLStreamException{
		if(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
		reader.getName().toString().equals(param)){
			StringBuilder value = new StringBuilder("");
			if(!(getText(reader, param, value))){
				throw new XMLStreamException("Problem with parsing flag " + param);
			}
			if(value.toString().equals("true")){
				return true;
			}
			return false;
		}
		return false;
	}
	
	private boolean getTexts(XMLStreamReader reader, List<String> texts, String paramGroup, String paramElement) throws XMLStreamException{
		if(reader == null || texts == null || paramGroup == null || paramElement == null){
			System.err.println("Null arguments for getTexts");
			return false;
		}
		if(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
		reader.getName().toString().equals(paramGroup)){
			if(!(getTexts(reader, texts, paramElement) && reader.getEventType() == XMLStreamConstants.END_ELEMENT && 
			reader.getName().toString().equals(paramGroup))){
				return false;
			}
			if(!doubleNext(reader)){
				return false;
			}
		}
		return true;
	}
	
	private boolean getButtons(XMLStreamReader reader, List<ButtonData> buttons) throws XMLStreamException{
		if(reader == null || buttons == null){
			System.err.println("Null arguments for getButtons");
			return false;
		}
		if(!doubleNext(reader)){
			return false;
		}
		StringBuilder text = new StringBuilder(""), type = new StringBuilder(""), param = new StringBuilder("");
		while(reader.getEventType() == XMLStreamConstants.START_ELEMENT && 
		reader.getName().toString().equals("button")){
			if(!doubleNext(reader)){
				return false;
			}
			if(reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
			reader.getName().toString().equals("text")){
				if(!getText(reader, "text", text)){
					return false;
				}
			}
			if(reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
			reader.getName().toString().equals("type")){
				if(!getText(reader, "type", type)){
					return false;
				}
			}
			if(reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
			reader.getName().toString().equals("param")){
				if(!getText(reader, "param", param)){
					return false;
				}
			}
			buttons.add(new ButtonData(text.toString(), type.toString(), param.toString()));
			text.delete(0, text.length());
			type.delete(0, type.length());
			param.delete(0, param.length());
			if(!(reader.getEventType() == XMLStreamConstants.END_ELEMENT && 
			reader.getName().toString().equals("button"))){
				return false;
			}
			if(!doubleNext(reader)){
				return false;
			}
		}
		return true;
	}
	
	private boolean getText(XMLStreamReader reader, String param, StringBuilder value) throws XMLStreamException{
		if(reader == null || param == null || value == null){
			System.err.println("Null arguments for getText");
			return false;
		}
		if(reader.hasNext() && reader.next() == XMLStreamConstants.CHARACTERS){
			value.append(reader.getText());
			if(reader.hasNext() && reader.next() == XMLStreamConstants.END_ELEMENT &&
			reader.getName().toString().equals(param)){
				if(!doubleNext(reader)){
					return false;
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
		return true;
	}
	
	private boolean doubleNext(XMLStreamReader reader) throws XMLStreamException{
		if(reader == null){
			System.err.println("Null arguments for doubleNext");
			return false;
		}
		if(reader.hasNext()){
			reader.next();
		}
		else{
			return false;
		}
		if(reader.hasNext()){
			reader.next();
		}
		else{
			return false;
		}
		return true;
	}
}