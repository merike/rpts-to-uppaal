 package ee.ttu.t061879.PrologToUppaal;

import java.util.ArrayList;

public class Requirements {
	private ArrayList<String> reqs = new ArrayList<String>();
	
	void requiredTransitionsTaken(String variableName, int length){
		String req = "E <> ";
		req += variableName + "[0] == true ";
		for(int i = 1; i < length; i++){
			req += " && " + variableName + "[" + i + "] == true"; 
		}
		reqs.add(req + "\n");
	}
	
	@Override
	public String toString() {
		String result = "";
		for(String s : reqs) result += s;
		return result;
	}
}
