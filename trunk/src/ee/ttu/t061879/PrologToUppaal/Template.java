package ee.ttu.t061879.PrologToUppaal;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Single template matching <template>
 * @author     Merike Sell
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.Templates"
 */
public class Template{
	ArrayList<Node> locations;
	ArrayList<Node> transitions;
	/**
	 * @uml.property  name="declaration"
	 * @uml.associationEnd  
	 */
	Node declaration;
	/**
	 * @uml.property  name="name"
	 * @uml.associationEnd  
	 */
	Node name;
	/**
	 * @uml.property  name="init"
	 * @uml.associationEnd  
	 */
	Node init;
	
	public Template() {
		locations = new ArrayList<Node>();
		transitions = new ArrayList<Node>();
	}

	/**
	 * @param declaration
	 * @uml.property  name="declaration"
	 */
	public void setDeclaration(Node declaration) {
		this.declaration = declaration;
	}

	/**
	 * the order of child nodes in xml seems to matter
	 * therefore ordering children in the following order:
	 * 1) name
	 * 2) declaration
	 * 3) location, ..., location
	 * 4) init
	 * 5) transition, ..., transition
	 */
	public Node getAsNode(){
		Node n = new Node("template");
		n.addChildNode(this.name);
		
		if(this.declaration != null)
			n.addChildNode(this.declaration);
		
		for(Node l : locations){
			n.addChildNode(l);
		}
		
		n.addChildNode(this.init);
		
		for(Node t : transitions){
			n.addChildNode(t);
		}
		
		return n;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public Node getName() {
		return name;
	}
	
	public void initTemplate(Node n){
		this.name = n.getChildNamed("name");
		this.init = n.getChildNamed("init");
	}
	
	/**
	 * 
	 * @return hashmap of vertices, where key is vertice's inner name
	 * and inner name of vertices having a connection to key vertice
	 * (undirected graph) are kept in ArrayList
	 */
	public HashMap<String, ArrayList<String>> getSourceTargetPairs(){
		HashMap<String, ArrayList<String>> c = new HashMap<String, ArrayList<String>>();
		for(Node n : this.transitions){
			String source = n.getChildNamed("source").getAttrValue("ref");
			String target = n.getChildNamed("target").getAttrValue("ref");
			
			if(!c.containsKey(source)) c.put(source, new ArrayList<String>());
			c.get(source).add(target);
			if(!c.containsKey(target)) c.put(target, new ArrayList<String>());
			c.get(target).add(source);
		}
		return c;
	}
	
	/**
	 * 
	 * @return hashmap of vertices, where key is vertice's inner name
	 * and inner name of vertices having a connection from key vertice
	 * (directed graph) are kept in ArrayList
	 */
	public HashMap<String, ArrayList<String>> getUniqueSourceTargetPairs(){
		HashMap<String, ArrayList<String>> c = new HashMap<String, ArrayList<String>>();
		for(Node n : this.transitions){
			String source = n.getChildNamed("source").getAttrValue("ref");
			String target = n.getChildNamed("target").getAttrValue("ref");
			
			if(!c.containsKey(source)) c.put(source, new ArrayList<String>());
			c.get(source).add(target);
		}
		return c;
	}
	
	public void updateForcePositions(HashMap<String, Point> positions){
		Set<String> keys = positions.keySet();
		
		// try to update each location
		for(String key : keys){
			// find appropriate location
			for(Node l : locations){
				if(l.getAttrValue("id").equalsIgnoreCase(key)){
					AttrValuePair x = new AttrValuePair("x", "" + positions.get(key).x);
					AttrValuePair y = new AttrValuePair("y", "" + positions.get(key).y);
					l.setAvp(x);
					l.setAvp(y);
				}
			}
		}
	}
	
	public void updateDotPositions(HashMap<String, 
	Point> positions, HashMap<String, ArrayList<Point>> edges){
		// location positions
		Set<String> keys = positions.keySet();
		
		// try to update each location
		for(String key : keys){
			// find appropriate location
			for(Node l : locations){
				if(l.getAttrValue("id").equalsIgnoreCase(key)){
					AttrValuePair x = new AttrValuePair("x", "" + positions.get(key).x);
					AttrValuePair y = new AttrValuePair("y", "" + positions.get(key).y);
					l.setAvp(x);
					l.setAvp(y);
				}
			}
		}
		
		// nails
		for(Node tr : transitions){
			Node src = tr.getChildNamed("source");
			String srcName = src.getAttrValue("ref");
			Node dst = tr.getChildNamed("target");
			String dstName = dst.getAttrValue("ref");
			
			Set<String> keys2 = edges.keySet();
			// for all edges
			for(String edgeKey : keys2){
				// if the transition is the same as edge
				if(edgeKey.equalsIgnoreCase(srcName + "," + dstName)){
					ArrayList<Point> p = edges.get(edgeKey);
					// add each nail
					for(Point point : p){
						Node nail = new Node("nail");
						nail.setAvp(new AttrValuePair("x", point.x + ""));
						nail.setAvp(new AttrValuePair("y", point.y + ""));
						tr.addChildNode(nail);
					}
				}
			}
		}
	}

}
