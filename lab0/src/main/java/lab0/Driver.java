package lab0;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class Driver {
	public static void main(String[] args) throws FileNotFoundException {
		InputStream input = new FileInputStream(new File(
	            "src/main/resources/config.yaml"));
		Yaml yaml = new Yaml();
		for (Object data : yaml.loadAll(input)) {
	        System.out.println(data);
		}
	}
}
