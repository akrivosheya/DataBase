package tourists;

import javafx.scene.*;
import java.util.*;

class Main{
	public static void main(String[] argv){
		Controller controller = new Controller();
		Map<String, Group> map = new HashMap<String, Group>();
		map = controller.getGroups("activation");
		map.forEach((String k, Group m)->{
			System.out.println(k);
			System.out.println(m.toString());
		});
	}
}