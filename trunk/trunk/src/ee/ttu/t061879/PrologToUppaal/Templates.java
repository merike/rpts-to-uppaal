package ee.ttu.t061879.PrologToUppaal;

import java.util.ArrayList;

/**
 * @author     Merike Sell
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Template"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.FunctionHandler"
 */
public class Templates extends ArrayList<Template>{
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="f"
	 * @uml.associationEnd  
	 */
	private FunctionHandler f;
	/**
	 * @uml.property  name="declaration"
	 * @uml.associationEnd  
	 */
	private Node declaration;
	private ArrayList<Variable> globalVariables;
	private String system = "";
	private String processes = "\n";
	private LayoutCalculator l;

	public Templates(FunctionHandler f, LayoutCalculator l) {
		this.f = f;
		this.l = l;
		globalVariables = new ArrayList<Variable>();
	}
	
	public void add(Node n){
		if(n.getName().equalsIgnoreCase("template")){
			Template t = new Template();
			t.initTemplate(n);
			this.add(t);
		}
		else if(n.getName().equalsIgnoreCase("location")){
			this.getTemplateByName(n.getTmpl()).getLocations().add(n);
		}
		else if(n.getName().equalsIgnoreCase("transition")){
			this.getTemplateByName(n.getTmpl()).getLocations().add(n);
		}
		else if(n.getName().equalsIgnoreCase("declaration")){
			if(n.getTmpl().length() != 0)
				this.getTemplateByName(n.getTmpl()).setDeclaration(n);
			else this.declaration = n;
		}
	}
	
	public void add(Variable v){
		globalVariables.add(v);
	}
	
	public void addToSystem(String s){
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
			for(int j = 0; j < t.getTransitions().size(); j++){
				// guard node
				String guard = t.getTransitions().get(j).getChildren().get(3).getText();
//				System.err.println("before: " + guard);
				for(String n : functionNames){
					guard = guard.replace(n + " ", n + "() ");
				}
//				System.err.println("\nafter: " + guard);
				t.getTransitions().get(j).getChildren().get(3).setText(guard);
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
			if(name.getText().equalsIgnoreCase(s))
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
