package tourists.helpers;

import javafx.scene.control.*;
import java.util.Iterator;

import tourists.MenuElements;

class ElementsCleaner{
	public  static void cleanElements(MenuElements elements){
		if(elements == null){
			throw new NullPointerException("Problem in ElementsCleaner.cleanElements: elements is null");
		}
		if(elements.getFields() != null){
			Iterator<TextField> iteratorTextField = elements.getFields().iterator();
			while(iteratorTextField.hasNext()){
				iteratorTextField.next().setText("");
			}
		}
		if(elements.getFlags() != null){
			Iterator<CheckBox> iteratorFlag = elements.getFlags().iterator();
			while(iteratorFlag.hasNext()){
				iteratorFlag.next().setSelected(false);
			}
		}
	}
}