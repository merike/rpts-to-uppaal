package ee.ttu.t061879.PrologToUppaal;

/**
 * Attribute-value pair for xml node attributes
 * @author  Merike Sell
 */
public class AttrValuePair {
	/**
	 * @uml.property  name="attr"
	 */
	private String attr;
	
	/**
	 * @uml.property  name="value"
	 */
	private String value;
	
	/**
	 * @return
	 * @uml.property  name="attr"
	 */
	public String getAttr() {
		return attr;
	}
	
	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public String getValue() {
		return value;
	}

	public AttrValuePair(String attr, String value) {
		this.attr = attr;
		this.value = value;
	}
}
