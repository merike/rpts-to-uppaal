package ee.ttu.t061879.PrologToUppaal;

import java.util.ArrayList;

/**
 * Formats all statements coming from main class to nodes and adds these to node pool (Templates)
 * @author   Merike Sell
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.AttrValuePair"
 */
public class Node {
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	/** 
	 * @uml.property name="children"
	 */
	private ArrayList<Node> children;
	/**
	 * template where node belongs or which it represents
	 * @uml.property  name="tmpl"
	 */
	private String tmpl = "";
	/**
	 * @uml.property  name="avp"
	 * @uml.associationEnd  
	 */
	private ArrayList<AttrValuePair> avp;
	/**
	 * @uml.property  name="text"
	 */
	private String text = "";
	
	public Node(String name){
		this.name = name;
		this.children = new ArrayList<Node>();
		this.avp = new ArrayList<AttrValuePair>();
	}
	
	/** 
	 * @uml.property  name="children"
	 */
	public ArrayList<Node> getChildren() {
		return children;
	}
	
	public void addChildNode(Node child){
		if(child == null){
			System.err.println("Trying to add null!");
		} else children.add(child);
	}
	
	public String toString(){
		// start tag
		String s = "";
		s += "<";
		s += this.name;
		
		// attribute
		if(this.avp.size() > 0)
			for(int i = 0; i < this.avp.size(); i++){
				s += " " + this.avp.get(i).getAttr() + "=\"" + this.avp.get(i).getValue() + "\"";
			}
		
		// text
		if(this.text.length() > 0 || this.children.size() > 0){
			s += ">";
			s += this.text;
		}
		
		// child nodes
		if(this.children.size() != 0){
			s += "\n";
			for(Node c : this.children) s += c.toString() + "\n";
		}
		
		// end tag
		if(this.text.length() > 0 || this.children.size() > 0){
			s += "</" + this.name + ">";
		}
		else{
			s += "/>";
		}
		
		return s;
	}
	
	/**
	 * Set node attribute
	 * @param  avp
	 * @uml.property  name="avp"
	 */
	public void setAvp(AttrValuePair avp) {
		for(AttrValuePair a : this.avp){
			if(a.getAttr().equalsIgnoreCase(avp.getAttr())){
				a.setAttr(avp.getValue());
				return;
			}
		}
	
		this.avp.add(avp);
	}
	
	/**
	 * 
	 * @param attr attribute name
	 * @return null if not found
	 */
	public String getAttrValue(String attr){
		for(AttrValuePair a : this.avp){
			if(a.getAttr().equalsIgnoreCase(attr)) return a.getValue();
		}
		return null;
	}

	/**
	 * Set node inner text
	 * @return
	 * @uml.property  name="text"
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 * @uml.property  name="text"
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * @return
	 * @uml.property  name="tmpl"
	 */
	public String getTmpl() {
		return tmpl;
	}

	/**
	 * @param tmpl
	 * @uml.property  name="tmpl"
	 */
	public void setTmpl(String tmpl) {
		this.tmpl = tmpl;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	
	public ArrayList<Node> getChildrenNamed(String name){
		ArrayList<Node> children = new ArrayList<Node>();
		for(Node c : this.children){
			if(c.name.equalsIgnoreCase(name))
				children.add(c);
		}
		
		return children;
	}

	/**
	 * Setter of the property <tt>children</tt>
	 * @param children  The children to set.
	 * @uml.property  name="children"
	 */
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
}
