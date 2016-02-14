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
	//constants
	private final static String Message_R_MultiCast = "R_MultiCast";
	private final static String Message_CO_MultiCast = "CO_MultiCast";
	
	//groups
	private List<MultiCastGroup> groups = new ArrayList<MultiCastGroup>();

	//multicaster name
	private String localName;

	
	public MultiCaster(String configFilename, String localName) {
		this.parseConfigFile(configFilename);
		this.localName = localName;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parseConfigFile(String filename) {
		InputStream input;
		try {
			input = new FileInputStream(new File(filename));
			Yaml yaml = new Yaml();

			Map config = (Map) yaml.load(input);
			List<Map> groupsMap = (List<Map>) config.get("groups");
			for (Map map : groupsMap)
				groups.add(new MultiCastGroup(map));

			Log.verbose("GROUPS", groups.toString());
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
