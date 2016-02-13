package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import message.Message;

public class MultiCaster implements DistributedApplication{
	private final static String Message_B_MultiCast = "B_MultiCast";
	private final static String Message_CO_MultiCast = "CO_MultiCast";
	//groups
	private List<List<String>> groups = new ArrayList<List<String>>();
	private String localName;

	public MultiCaster(String configFilename, String localName) {
		this.parseConfigFile(configFilename);
		this.localName = localName;
	}
	
	public void parseConfigFile(String filename) {
		InputStream input;
		try {
			input = new FileInputStream(new File(filename));
			Yaml yaml = new Yaml();

			Map config = (Map) yaml.load(input);
			List<Map> groups = (List<Map>) config.get("groups");

			System.out.println();
			Log.verbose("GROUPS", groups.toString());
			System.out.println();
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}

	void B_MultiCast(){
		
	}
	
	void B_Deliver(){
		
	}
	
	void R_MultiCast(){
		
	}
	
	void R_Deliver(){
		
	}

	void CO_MultiCast(){
		
	}
	
	void CO_Deliver(){
		
	}

	@Override
	public void OnMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAppName() {
		return "MultiCaster";
	}
	
	
}
