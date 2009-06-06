package ee.ttu.t061879.PrologToUppaal;

import java.util.ArrayList;

/**
 * @author             Merike Sell
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Variable"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Templates"
 */
public class FunctionHandler {
	private ArrayList<String> functions;
	private int max;
	private final int UPPAL_MAX_INT = 32767;
	
	/**
	 * 
	 * @param coefficent if 2*coefficient exceeds Uppaal's default 
	 * then range is set explicitly
	 */
	public FunctionHandler(int coefficent) {
		functions = new ArrayList<String>();
		max = coefficent;
	}
	
	public void module(Templates pool, String modName, String module){
//		System.err.println("--" + module.substring(0, 20) + "--");
		Node declaration = new Node("declaration");
		modName = modName.trim();
		modName = modName.substring(0, modName.indexOf('_'));
		declaration.setTmpl(modName);
		
		// remove import
		module = module.substring(module.indexOf(";") + 1, module.length());
		
		int pointer = 0;
		int find = 0;
		
		// find and append each function to ArrayList
		while(pointer < module.length() - 1){
			int starts = 0, ends = 0;
			
			pointer = module.indexOf("{", pointer) + 1;
			starts ++;
			
			while(starts != ends){
				if(module.charAt(pointer) == '{') starts++;
				else if(module.charAt(pointer) == '}') ends++;
				pointer++;
			}
			
//			System.err.println(find + " " + start);
//			System.err.println("--" + module.substring(find, pointer).trim() + "--");
			functions.add(module.substring(find, pointer).trim()); 
			find = pointer;
		}
		
		// convert functions
		convertFuntions();
		orderFunctions();
		
		declaration.setText(toString());
		pool.add(declaration);
	}
	
	private void convertFuntions(){
		int c = functions.size();
		while((c -= 1 ) >= 0){
			String f = functions.remove(0);

			f = f.replace("not_ (", "not_(");

			if(f.startsWith("int ") && max > UPPAL_MAX_INT){
//				f = "int[-1048576,1048576] " + f.substring(4);
				f = f.replace("int", "int[0," + getMaxInt()+ "]");
//				System.err.println("fixing range");
			}
			
			functions.add(f);
//			System.err.println("f: " + f);
		}
	}

	/**
	 * oreders functions so that they are defined before
	 * they are referenced, needed by Uppaal
	 *
	 */
	private void orderFunctions(){
		for(int i = 1; i < functions.size(); i++){
			String f = functions.get(i);
			
//			System.err.println(":" + f + ":");
			String functionName = f.substring(
					f.indexOf(" "), f.indexOf("(")).trim();
			
			for(int j = 0; j < i; j++){
				if(functions.get(j).contains(functionName + "(")){
					functions.add(j, functions.remove(i));
					break;
				}
			}
		}
	}
	
//	public void variablesFromAssignments(String s){
//		String assignments[] = s.split(",");
//		
//		for(int i = 0; i < assignments.length; i++){
//			String variableValue[] = assignments[i].split("=");
//			int left = 0, right = 1;
//			variableValue[left] = variableValue[left].trim();
//			variableValue[right] = variableValue[right].trim();
//			
//			int n = 1;
//			String name = "";
//			
//			// not array
//			if(variableValue[left].indexOf("[") == -1){
//				name = variableValue[left];
//			}
//			// array
//			else{
//				n = Integer.parseInt(variableValue[left].substring(
//						variableValue[left].indexOf("[") + 1,
//						variableValue[left].indexOf("]")));
//				name = variableValue[left].substring(
//						0,
//						variableValue[left].indexOf("["));
//			}
//			
//			// boolean
//			if(variableValue[right].equalsIgnoreCase("true") ||
//					variableValue[right].equalsIgnoreCase("false")){
//				// array
//				if(variableValue[left].contains("[")){
//					// if already exists, length might need update
//					int c = variableExists(name);
//					if(c != -1){
//						int l = variables.get(c).getLength();
//						
//						// update length
//						if((n + 1) > l){
////							System.err.println("need to update length");
//							variables.get(c).setLength(n + 1);
//						}
//					}
//					// else declare
//					else{
////						System.err.println("declaring");
//						declareVariable(name, n + 1, "bool");
//					}
//				} // end array
//			}
//			// declare as int
//			else{
//				if(variableExists(variableValue[left]) == -1)
//					declareVariable(variableValue[left], 1, "int");
//				// declare, unless integer value
//				try{
//	            	Integer.parseInt(variableValue[right]);
//	            }
//	            catch (Exception e) {
//	            	if(variableExists(variableValue[right]) == -1)
//	            		declareVariable(variableValue[right], 1, "int");
//				}
//			}
//		}
//	}
	
	/**
	 * 
	 * @param name
	 * @return -1 if does not exist, position if exists
	 */
//	private int variableExists(String name){
////		System.err.println("checking " + name);
//		int c = -1;
//		int i = 0;
//		for(Variable v : variables){
//			if(v.getName().equalsIgnoreCase(name)){
////				System.err.println(name + " exists");
//				return i;
//			}
////			else System.err.println(name + " exists not!");
//			i++;
//		}
//		return c;
//	}
	
//    private void declareVariable(String name, int length, String type){
//    	Variable v = new Variable(name, type);
//    	if(length > 1) v.setLength(length);
//    	variables.add(v);
//    }
	
	public String toString(){
		String t = "\n";
		
//		// variables
//		for(Variable v : variables){
//			if(v.getType().equalsIgnoreCase("bool")){
//				t += "bool " + v.getName();
//				
//				if(v.getLength() == 1) t += " = false;\n";
//				else{
//					t += "[" + v.getLength() + "] = {false";
//					for(int i = 1; i < v.getLength(); i++){
//						t += ", false";
//					}
//					t += "};\n";
//				}
//			} // end boolean
//			if(v.getType().equalsIgnoreCase("int")){
//				t += "int ";
//				t += v.getName();
//				t += ";\n";
//			}
//		}
//		
//		t += "\n";
		
		// functions
		for(String f : functions) t += f + "\n";
		return t;
	}
	
	/**
	 * To be called only after calling convertFunctions
	 * @return
	 */
	public String[] getFunctionNames(){
		String[] r = new String[functions.size()];
		for(int i = 0; i < functions.size(); i++){
			String f = functions.get(i);
			int start = f.indexOf(" ");
			int end = f.indexOf("(", start + 1);
			r[i] = f.substring(start + 1, end);
//			System.err.println(r[i]);
		}
		return r;
	}
	
	private int getMaxInt(){
		return max * 2;
	}
}
