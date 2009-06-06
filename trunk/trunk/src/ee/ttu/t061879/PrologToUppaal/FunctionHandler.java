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
	
	
	public String toString(){
		String t = "\n";
		
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
