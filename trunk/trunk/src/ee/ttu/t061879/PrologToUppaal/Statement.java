package ee.ttu.t061879.PrologToUppaal;

/**
 * Formats all statements coming from main class to nodes and adds these to node pool (Templates)
 * @author         Merike Sell
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.Node"
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.Templates"
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.FunctionHandler"
 */
public class Statement {
	public static void location(Templates pool, String s){
//		System.err.println("location: " + s);
		try{
			String arguments[] = s.split(",");
			
			for(int i = 0; i < arguments.length; i++) arguments[i] = arguments[i].trim();
			if(arguments[1].equalsIgnoreCase("init")) arguments[1] += "_";
			
	//		System.err.println("internal name " + arguments[0]);
	//		System.err.println("name " + arguments[1]);
	//		System.err.println("template " + arguments[4]);
			
			Node n = new Node("location");
			n.setTmpl(arguments[4]);
			n.setAvp(new AttrValuePair("id", arguments[0]));
			
			Node name = new Node("name");
			name.setText(arguments[1]);
			
			n.addChildNode(name);
			
			pool.add(n);
	//		System.err.println(n);
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("location fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
	}
	
	public static void template(Templates pool, String s){
//		System.err.println("template: " + s);
		try{
			String arguments[] = s.split(",");

			for(int i = 0; i < arguments.length; i++) arguments[i] = arguments[i].trim();
			
			Node n = new Node("template");
			n.setTmpl(arguments[0]);
					
			Node name = new Node("name");
			name.setText(arguments[0]);
			n.addChildNode(name);
			
			Node init = new Node("init");
			init.setAvp(new AttrValuePair("ref", arguments[3]));
			n.addChildNode(init);
			
			pool.add(n);
	//		System.err.println(n);
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("template fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
	}
	
	public static void transition(Templates pool, String s){
		s = s.replace(" ", "");
//		System.err.println("transition: " + s);
		
		Node trans = new Node("transition");
		
		int progress;
		
		// source
		try{
			progress = s.indexOf(",");
			
			Node source = new Node("source");
			source.setAvp(new AttrValuePair("ref", s.substring(0, progress)));
			trans.addChildNode(source);
			
			// target
			int tmp = s.indexOf(",", progress + 1);
			Node target = new Node("target");
			target.setAvp(new AttrValuePair("ref",
					s.substring(progress + 1, tmp).trim()));
			progress = tmp;
			trans.addChildNode(target);
			
			// TODO correct?
			tmp = s.indexOf("],", progress + 1);
			String trans_label = s.substring(progress + 2, tmp).trim();
	//		System.err.println("Transition_lable_expr: " + trans_label);
			Node label = new Node("label");
			label.setAvp(new AttrValuePair("kind", "comments"));
			label.setText(trans_label);
			trans.addChildNode(label);
			progress = tmp + 2;
			
			// IO guards, two-dimensional
			String d = s.substring(progress, progress + 2);
			String ioGuard;
			// empty
			if(d.equalsIgnoreCase("[]")){
				progress += 3;
				ioGuard = "";
			}
			// array
			else{
				tmp = s.indexOf("]]", progress + 1);
				ioGuard = s.substring(progress + 1, tmp + 1);
				progress = tmp + 3;
			}
			
			// internal guard, one dimension
			tmp = s.indexOf("]", progress) + 1;
			String intGuard;
			// empty
			if((tmp - progress == 2)){
				intGuard = "";
			}
			else{
				intGuard = s.substring(progress + 1, tmp - 1);
			}
			progress = tmp + 1;
			
			Node guard = new Node("label");
			guard.setAvp(new AttrValuePair("kind", "guard"));
			guard.setText(guard(ioGuard, intGuard));
			trans.addChildNode(guard);
			
			// IO assignment, two-dimensional
			d = s.substring(progress, progress + 2);
			String ioAssign;
			// empty
			if(d.equalsIgnoreCase("[]")){
				progress += 3;
				ioAssign = "";
			}
			// array
			else{
				tmp = s.indexOf("]]", progress + 1);
				ioAssign = s.substring(progress + 1, tmp + 1);
				progress = tmp + 3;
			}
			
			// output assignment, two-dimensional
			d = s.substring(progress, progress + 2);
			String outAssign = "";
			// empty
			if(d.equalsIgnoreCase("[]")){
				progress += 3;
				outAssign = "";
			}
			// array
			else{
				tmp = s.indexOf("]]", progress + 1);
				outAssign = s.substring(progress + 1, tmp + 1);
				progress = tmp + 3;
			}
			Node ass = new Node("label");
			ass.setAvp(new AttrValuePair("kind", "assignment"));
			ass.setText(assign(ioAssign, outAssign));
			trans.addChildNode(ass);
			
			// template
			trans.setTmpl(s.substring(progress, s.length()));
			
			pool.add(trans);
	//		System.err.println(trans);
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("transition fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
	}
	
	private static String guard(String io, String internal){
		String r = "";
		
		// io
		if(io.trim().length() != 0){
			String ioGuard = io.trim();
			ioGuard = ioGuard.substring(1, ioGuard.length() - 1);
			ioGuard = ioGuard.replace("],[", "REMOVEME");
			
			ioGuard = ioGuard.replace(",", " ");
			ioGuard = ioGuard.replace("REMOVEME", ",");
			
//			ioGuard = ioGuard.replace("<", "&lt;");
//			ioGuard = ioGuard.replace(">", "&gt;");
			r += ioGuard;
//			f.variablesFromAssignments(ioGuard.replace("==", "="));
//			System.err.println("io:" + ioGuard);
		}
		
		// internal
		String internalGuard = "";
		if((internalGuard = internal.trim()).length() > 0){
			if(r.length() > 0) r += ", ";
			
			internalGuard = internalGuard.replace(" ", "");
			r += internalGuard;
//			System.err.println("Statement.guard() " + internalGuard);
		}
		
		r = r.replace(",or,", " or ");
		r = r.replace(",and,", " and ");
		
//		System.err.println("guard: " + r);
		// " " needed for Template.toString()
		return r + " ";
	}
	
	private static String assign(String ioAssign, String outAssign){
		String r = "";
		
		// io
		if(ioAssign.trim().length() != 0){
			String ioAssignment = ioAssign.trim();
			ioAssignment = ioAssignment.substring(1, ioAssignment.length() - 1);
			ioAssignment = ioAssignment.replace("],[", "REMOVEME");
			
			ioAssignment = ioAssignment.replace(",", "");
			ioAssignment = ioAssignment.replace("REMOVEME", ",");
			
			// variables need to be declared
//			f.variablesFromAssignments(ioAssignment);
			
			r += ioAssignment;
		}
		
		// out
		String outAssignment = "";
		if((outAssignment = outAssign.trim()).length() > 0){
			if(r.length() > 0) r += ", ";
			
			outAssignment = outAssignment.substring(1, outAssignment.length() - 1);
			outAssignment = outAssignment.replace("],[", "REMOVEME");
			
			outAssignment = outAssignment.replace(",", "");
			outAssignment = outAssignment.replace("REMOVEME", ",");
			
			// variables need to be declared
//			f.variablesFromAssignments(outAssignment);
			
			r += outAssignment;
		}
		
//		System.err.println("assignment: " + r);
		return r;
	}

	public static void declaration(Templates pool, String s){
		s = s.replace(" ", "");
//		System.err.println(s);
		
		String variableType, dataType, allowedValues, variableName, length,
			initValues;
		
		try{
			int pos = s.indexOf(",");
			variableType = s.substring(0, pos);
			s = s.substring(pos + 1);
			
			pos = s.indexOf(",");
			dataType = s.substring(0, pos);
			s = s.substring(pos + 1);
			
			pos = s.indexOf("],") + 1;
			allowedValues = s.substring(0, pos);
			s = s.substring(pos + 1);
			
			pos = s.indexOf(",");
			variableName = s.substring(0, pos);
			s = s.substring(pos + 1);
			
			pos = s.indexOf("],") + 1;
			length = s.substring(1, pos - 1);
			s = s.substring(pos + 1);
			
			pos = s.indexOf(",[");
			initValues = s.substring(0, pos);
			initValues = initValues.replace("[", "{").replace("]", "}")
				.replace(",", ", ");
			
			s = s.substring(pos + 1);
			
//			System.err.println(variableType + " " + dataType + " " + 
//					allowedValues + " " + variableName + " " + length + " " + 
//					initValues);
			
			Variable v = new Variable();
			v.setName(variableName);
			v.setDataType(dataType);
			v.setVariableType(variableType);
			v.setInitValues(initValues);
			v.setAllowedValues(allowedValues);
			try{
				v.setLength(Integer.parseInt(length));
			}
			catch(NumberFormatException e){}
			pool.add(v);
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("declaration fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
	}
	
	public static void system(Templates pool, String s){
		s = s.replace(" ", "");
		
		try{
			int pos = s.indexOf(",[") + 2;
			String system = s.substring(pos, s.length() - 1).trim();
			system = system.replace(",", ", ");
			pool.addToSystem("system " + system + ";\n");
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("system fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
		
//		System.err.println(system);
	}
	
	public static void instantiation(Templates pool, String s){
		try{
			int pos = s.indexOf(",");
			int pos2 = s.indexOf(",", pos + 1);
			
			String inst = s.substring(pos + 1, pos2).trim() + " = " +
				s.substring(pos2 + 1, s.length()).trim() + ";\n";
			
			pool.addToSystem(inst);
			
	//		System.err.println(inst);
		}
		catch (IndexOutOfBoundsException e) {
			System.err.println("instantiation fakti parsimine ebaõnnestus " 
					+ e.getMessage());
		}
	}
}
