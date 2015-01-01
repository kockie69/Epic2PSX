import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class mappings implements List<String> {

	static boolean PSX2EPIC = true;
	public static Map<String, String[]> EPIC2PSXmappingArray = new HashMap<String, String[]>();
	public static Map<String, String[]> PSX2EPICmappingArray = new HashMap<String, String[]>();
	
	public static void readAll() {
		
		String[] s = null;
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("src/EPIC2PSX.map"));

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.endsWith(";"))
					sCurrentLine = sCurrentLine.substring(0,
							sCurrentLine.length() - 1);
				if (sCurrentLine.equals("EPIC2PSX")) 
					PSX2EPIC = false;
				else {
					s = sCurrentLine.split(";");
					if (!sCurrentLine.startsWith("[") && !sCurrentLine.isEmpty()) {
						if (PSX2EPIC==true) PSX2EPICmappingArray.put(s[0].split("=")[0],s);
						else{
							EPIC2PSXmappingArray.put(s[0].split("=")[0],s);
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void printAllPSX2EPIC() {

		for (String[] value:PSX2EPICmappingArray.values())  {

	    	System.out.println("PSXVariables: " + Arrays.deepToString(value));
		}
	}
	
	public static void printAllEPIC2PSX() {

		for (String[] value:EPIC2PSXmappingArray.values())  {

	    	System.out.println("PSXVariables: " + Arrays.deepToString(value));
		}
	}
	
	public static String getLineEPIC2PSX(String cmd) {
		
		String[] found = EPIC2PSXmappingArray.get(cmd);
		String retFound = "";
		if (found!=null) {
			retFound = Arrays.toString(found);
		}
		return retFound; 
	}
	
	public static String getLinePSX2EPIC(String data) {

		String retFound = "";
		String[] found = PSX2EPICmappingArray.get(data);
		
		if (found!=null) {
			retFound=Arrays.toString(found);
		}
		return retFound;
}
	
}