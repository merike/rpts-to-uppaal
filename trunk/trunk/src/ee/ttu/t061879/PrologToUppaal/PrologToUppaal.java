package ee.ttu.t061879.PrologToUppaal;

import java.io.File;

/**
 * Main class to run to produce xml Doesn't format with indentation but uses newlines
 * @author                               Merike Sell
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.MyFileReaderWriter"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Templates"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Requirements"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.Statement"
 * @uml.dependency   supplier="ee.ttu.t061879.PrologToUppaal.FunctionHandler"
 */
public class PrologToUppaal extends Requirements {
	final static String doctype = "<?xml version='1.0' encoding='utf-8'?>\n<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>";
	
	public static void main(String[] args){
		String filepath = "";
		String file = "";
		String outfile = "";
		String out = "";
		String layout = "";
		int max = 0;
		
		if(args.length > 0){
			// last argument is filepath
			filepath = args[args.length - 1];
			
			file = MyFileReaderWriter.readFileToString(filepath);
			if(file.length() == 0) System.exit(2);
			
			File f = new File(filepath);
			out = f.getPath() + ".xml";
			
			try{
				max = Integer.parseInt(args[0]);
			}
			catch(NullPointerException e){}
			catch(NumberFormatException e){}
			
			// layout
			if(args.length > 1){
				if(args[1].equalsIgnoreCase("dot")) layout = "dot";
				else if(args[1].equalsIgnoreCase("force")) layout = "force";
				else layout = "none";
			}
		}
		else{
			System.err.println("Sisendfaili nimi puudu!");
			System.exit(1);
		}
		
//		System.err.println(file);
		
		FunctionHandler fh = new FunctionHandler(max);
		Requirements r = new Requirements();
		LayoutCalculator l = new LayoutCalculator(layout);
		Templates pool = new Templates(fh, l);
		
		int start = 0;
		while(true){
			if(file.startsWith("template", start)){
				int find = file.indexOf(".\n", start);
				Statement.template(pool, file.substring(start + 9, find - 1));
				start = find + 1;
//				System.err.println(file.substring(start, start + 3));
			}
			else if(file.startsWith("location", start)){
				int find = file.indexOf(".\n", start);
				Statement.location(pool, file.substring(start + 9, find - 1));
				start = find + 1;
//				System.err.println(file.substring(start, start + 3));
			}
			else if(file.startsWith("transition", start)){
				int find = file.indexOf(".\n", start);
				Statement.transition(pool, file.substring(start + 11, find - 1));
				start = find + 1;
			}
			else if(file.startsWith("declaration", start)){
				int find = file.indexOf(".\n", start);
				Statement.declaration(pool, file.substring(start + 12, find - 1));
				start = find + 1;
			}
			else if(file.startsWith("system", start)){
				int find = file.indexOf(".\n", start);
				Statement.system(pool, file.substring(start + 7, find - 1));
				start = find + 1;
			}
			else if(file.startsWith("instantiation", start)){
				int find = file.indexOf(".\n", start);
				Statement.instantiation(pool, file.substring(start + 14, find - 1));
				start = find + 1;
			}
			else if(file.startsWith("module", start)){
				int find = file.indexOf("{\n", start) + 2;
				String modName = file.substring(start + 6, find - 2);
				start = find;
				
				int starts = 1, ends = 0;
				while(starts != ends){
//					System.err.println("in a loop");
					if(file.charAt(start) == '{') starts++;
					else if(file.charAt(start) == '}') ends++;
					start++;
//					System.err.println("start " + start + " at " + file.substring(start, start + 5));
				}
				
//				System.err.println("---" + file.substring(find, start - 1) + "---");
				fh.module(pool, modName, file.substring(find, start - 1));
			}
			// unfamiliar data
			else{
				// move a line forward
				start = file.indexOf("\n", start) + 1;
//				System.err.println("offset is: " + start);
			}
			
			if(start == file.length()) break;
		}
		
		outfile += doctype + "\n";
		outfile += "<nta>\n";
		outfile += pool.toString();
		outfile += "\n</nta>";
		boolean result = MyFileReaderWriter.writeStringToFile(outfile, out);
		if(!result) System.exit(3);
		System.err.println(out);
		
		// requirements
		AttrValuePair a = pool.getTrapsData();
		if(a != null){
//			System.err.println(a.getAttr() + Integer.parseInt(a.getValue()));
			r.requiredTransitionsTaken(a.getAttr(), Integer.parseInt(a.getValue()));
			File f = new File(filepath);
			out = f.getPath() + ".q";
			boolean result2 = MyFileReaderWriter.writeStringToFile(r.toString(), out);
			if(!result2) System.exit(3);
			System.err.println(out);
		}
	}
}
