import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PSXVariables implements List<String> {

	static Map<String, String[]> vectorArray = new HashMap<String, String[]>();

	public static String[] readAll() {
		
		String[] s = null;
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("src/Variables.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.endsWith(";"))
					sCurrentLine = sCurrentLine.substring(0,
							sCurrentLine.length() - 1);
				s = sCurrentLine.split(";");
				if (!sCurrentLine.startsWith("[") && !sCurrentLine.isEmpty()) {
					vectorArray.put(s[0].split("=")[0],s);
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
		return null;

	}
	
	public static void printAll() {

		for (String[] value:vectorArray.values())  {

	    	System.out.println("PSXVariables: " + Arrays.deepToString(value));
		}
	}
	
	public static String[] getLine(String cmd) {
			return vectorArray.get(cmd);
	}
	
	
}
