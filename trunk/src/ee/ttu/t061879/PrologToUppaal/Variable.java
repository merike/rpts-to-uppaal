package ee.ttu.t061879.PrologToUppaal;

/**
 * @author  Merike Sell
 */
public class Variable {
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	private String dataType = "";
	/**
	 * @uml.property  name="variableType"
	 */
	private String variableType = "";
	/** 
	 * @uml.property name="allowedValues"
	 */
	private String allowedValues = "";
	private String initValues = "";
	/**
	 * @uml.property  name="length"
	 */
	private int length = 1;
	
	public Variable() {
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 * @uml.property  name="length"
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return
	 * @uml.property  name="variableType"
	 */
	public String getVariableType() {
		return variableType;
	}

	/**
	 * @param allowedValues
	 * @uml.property  name="allowedValues"
	 */
	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	/**
	 * @param dataType
	 * @uml.property  name="dataType"
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @param initValues
	 * @uml.property  name="initValues"
	 */
	public void setInitValues(String initValues) {
		this.initValues = initValues;
	}

	/**
	 * @param length
	 * @uml.property  name="length"
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param variableType
	 * @uml.property  name="variableType"
	 */
	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}
	
	public String toString(){
		String r = "";
		
		// const, if present
		if(!this.variableType.equalsIgnoreCase("array") &&
		!this.variableType.equalsIgnoreCase("var"))
			r += this.variableType + " ";
		
		// data type
		r += this.dataType;
		if(this.dataType.equalsIgnoreCase("int") &&
		this.variableType.equalsIgnoreCase("var"))
			r += this.allowedValues;
		
		// name, possibly length
		r += " " + this.name;
		if(this.variableType.equalsIgnoreCase("array")){
			r += "[" + this.length + "]";
		}
		
		if(this.initValues.length() > 0)
			r += " = " + this.initValues;
		
		r += ";";
		
		return r;
	}

	/**
	 * Getter of the property <tt>allowedValues</tt>
	 * @return  Returns the allowedValues.
	 * @uml.property  name="allowedValues"
	 */
	public String getAllowedValues() {
		return allowedValues;
	}

}
