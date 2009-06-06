package ee.ttu.t061879.PrologToUppaal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Helper class to read input file to String
 * @author     Merike Sell
 * @uml.dependency  supplier="ee.ttu.t061879.PrologToUppaal.PrologToUppaal"
 */
public class MyFileReaderWriter {
	/**
	 * 
	 * @param filepath file to read
	 * @return file contents as a string
	 */
	static String readFileToString(String filepath){
		String fileString = "";
		try{
			FileReader fr = new FileReader(new File(filepath));
			BufferedReader bf = new BufferedReader(fr);
			String line;
			while((line = bf.readLine()) != null){
				if(line.trim().startsWith("//")){
					System.err.println("Throwing away one-line comment: " + line);
				}
				else fileString += line.trim() + "\n";
			}
		} catch(FileNotFoundException e){
			System.err.println("Ei leidnud faili " + filepath + ": " + e.getMessage());
			return "";
		} catch (IOException e) {
			System.err.println("Viga lugemisel: " + e.getMessage());
			return "";
		}
		
		return fileString;
	}
	
	/**
	 * 
	 * @param content string to write
	 * @param filepath filepath to write
	 * @return success
	 */
	static boolean writeStringToFile(String content, String filepath){
		try{
			FileWriter fw = new FileWriter(new File(filepath));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			return true;
		} catch(FileNotFoundException e){
			System.err.println("Ei leidnud faili " + filepath + ": " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Viga kirjutamisel: " + e.getMessage());
		}
		return false;
	}
}
