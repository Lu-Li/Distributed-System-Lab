package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class FileIO {
	public static void readFromURL(String url, String localFilePath) {
		URL oracle;
		try {
			oracle = new URL(url);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(oracle.openStream()));
			FileWriter fw = new FileWriter(new File(localFilePath));
			BufferedWriter bw = new BufferedWriter(fw);
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
//	        	System.out.println(inputLine);
	        	bw.write(inputLine);
	        	bw.newLine();
	        }           
	        in.close();
	        bw.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}    
	}
}
