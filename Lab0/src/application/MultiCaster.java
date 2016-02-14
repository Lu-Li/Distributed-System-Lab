package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Broker;
import message.Message;
import message.MessagePasser;
import message.MultiCastTimestampedMessage;
import message.TimestampedMessage;

public class MultiCaster implements DistributedApplication {
	// constants
	private final static String Message_B_MultiCast = "B_MultiCast";
	private final static String Message_R_MultiCast = "R_MultiCast";
	private final static String Message_CO_MultiCast = "CO_MultiCast";

	// groups
	private List<MultiCastGroup> groups = new ArrayList<MultiCastGroup>();
	// TODO:
	private Set<MultiCastTimestampedMessage> receivedMsg = new HashSet<>();

	// multicaster name
	private String localName;

	public MultiCaster(String configFilename, String localName, Broker broker) {
		this.parseConfigFile(configFilename);
		this.localName = localName;
		broker.register(Message_B_MultiCast, this);
		broker.register(Message_R_MultiCast, this);
		broker.register(Message_CO_MultiCast, this);
	}

	public List<String> getAllMembersByGroupName(String groupName) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getName().equals(groupName)) {
				return groups.get(i).getAllMembers();
			}
		}
		return null;
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

	
	public void B_MultiCast(String groupName, Message message) {
		Log.info("MultiCaster", "B_MC :"+message+"->"+groupName);
		
		List<String> members = getAllMembersByGroupName(groupName);
		for (String name:members) {
			MultiCastTimestampedMessage MCMessage = 
					new MultiCastTimestampedMessage(message,name,groupName,Message_B_MultiCast);
			Log.info("MultiCaster", "B_MC :"+MCMessage+" => "+name);
			MessagePasser.send(MCMessage);
		}
	}

	void B_Deliver(Message msg) {
		// if we are only using B_multicast:
		// sysout....
		System.out.println("B_Deliver:"+msg.toString());
		// if m not reeceived ....
		// do sth
		// call R_deliver()

	}

	// only original sender use r_multicast
	public void R_MultiCast(String groupName, Message message) {
		Log.info("MultiCaster", "R_MC :"+message.getData()+"->"+groupName);
		TimeStamp timeStamp = ClockServiceFactory.getClockService().issueTimeStamp();
		TimestampedMessage newMessage = new TimestampedMessage("", Message_R_MultiCast, message.getData());
		Log.info("MultiCaster", "R_MC call B_MC: "+newMessage+" -> "+groupName);
		B_MultiCast(groupName, newMessage);
	}

	void R_Deliver(Message msg) {
		// sysout "get reliable m message:...."
	}

	void CO_MultiCast() {

	}

	void CO_Deliver() {

	}

	@Override
	public void OnMessage(Message msg) {
		if (msg.getKind().equals(Message_B_MultiCast)) {
			B_Deliver(msg);
		} else if (msg.getKind().equals(Message_R_MultiCast)) {
			R_Deliver(msg);
		}
	}

	@Override
	public String getAppName() {
		return "MultiCaster";
	}

}
