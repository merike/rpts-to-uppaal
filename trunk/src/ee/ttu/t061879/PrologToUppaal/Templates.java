package ee.ttu.t061879.PrologToUppaal;

import java.util.ArrayList;

/**
 * @author     Merike Sell
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.Template"
 */
public class Templates extends ArrayList<Template>{
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="f"
	 * @uml.associationEnd  
	 */
	FunctionHandler f;
	/**
	 * @uml.property  name="declaration"
	 * @uml.associationEnd  
	 */
	Node declaration;
	ArrayList<Variable> globalVariables;
	String system = "";
	String processes = "\n";
	private LayoutCalculator l;

	public Templates(FunctionHandler f, LayoutCalculator l) {
		this.f = f;
		this.l = l;
		globalVariables = new ArrayList<Variable>();
	}
	
	void add(Node n){
		if(n.name.equalsIgnoreCase("template")){
			Template t = new Template();
			t.initTemplate(n);
			this.add(t);
		}
		else if(n.name.equalsIgnoreCase("location")){
			this.getTemplateByName(n.tmpl).locations.add(n);
		}
		else if(n.name.equalsIgnoreCase("transition")){
			this.getTemplateByName(n.tmpl).transitions.add(n);
		}
		else if(n.name.equalsIgnoreCase("declaration")){
			if(n.tmpl.length() != 0)
				this.getTemplateByName(n.tmpl).setDeclaration(n);
			else this.declaration = n;
		}
	}
	
	void add(Variable v){
		globalVariables.add(v);
	}
	
	void addToSystem(String s){
		if(s.substring(0, 6).equalsIgnoreCase("system")) system = s;
		else processes += s;
	}
	
	//
	public String toString(){
		String r = "";
		
		// variables
		if(globalVariables.size() != 0){
			declaration = new Node("declaration");
			String text = "";
			for(Variable v : globalVariables){
				text += v.toString() + "\n";
			}
			declaration.setText(text);
			r += declaration.toString() + "\n";
		}
		
		// fix function-calls in guards
		String[] functionNames = f.getFunctionNames();
		for(int i = 0; i < this.size(); i++){
			Template t = this.get(i);
			for(int j = 0; j < t.transitions.size(); j++){
				// guard node
				String guard = t.transitions.get(j).children.get(3).getText();
//				System.err.println("before: " + guard);
				for(String n : functionNames){
					guard = guard.replace(n + " ", n + "() ");
				}
//				System.err.println("\nafter: " + guard);
				t.transitions.get(j).children.get(3).setText(guard);
			}
		}
		
		// templates
		for(Template t : this){
			this.l.calculatePositions(t);
			r += t.getAsNode().toString() + "\n";
		}
		
		Node syst = new Node("system");
		String systemText = processes + system;
		syst.setText(systemText);
		r += syst.toString();
		
		return r;
	}
	
	private Template getTemplateByName(String s){
		for(Template t : this){
			Node name = t.getName();
			if(name.text.equalsIgnoreCase(s))
				return t;
		}
		
		return null;
	}
	
	public AttrValuePair getTrapsData(){
		AttrValuePair a = null;
		String name = "",
			length = "";
		
		for(Variable v : globalVariables){
			if(v.getName().equalsIgnoreCase("t") 
					&& v.getVariableType().equalsIgnoreCase("array")){
				name = v.getName();
				length = v.getLength() + "";
				a = new AttrValuePair(name, length);
//				System.err.println("traps found");
			}
		}
		
		return a;
	}
}
