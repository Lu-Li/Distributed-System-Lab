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
import clock.VectorTimeStamp;
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
	
	// CO multicast: V_i for each group g
	private List<VectorTimeStamp> groupsTimestamp;
	private List<MultiCastTimestampedMessage> holdbackQueue = new LinkedList<>();
	
	// TODO: R-multicast: check if identical
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

	/**
	 * group helper functions
	 */
	public List<String> getAllMembersByGroupName(String groupName) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getName().equals(groupName)) {
				return groups.get(i).getAllMembers();
			}
		}
		return null;
	}

	public int getIndexByGroupName(String groupName) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getName().equals(groupName)) {
				return i;
			}
		}
		return -1;
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

			//init V_i for each group g
			groupsTimestamp = new ArrayList<VectorTimeStamp>();
			for (int i=0;i<groups.size();i++){
				int size = groups.get(i).getGroupSize();
				groupsTimestamp.add(new VectorTimeStamp(size));
			}
			Log.verbose("GROUPS", groups.toString());
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}

	/**
	 * DistApp methods
	 */
	@Override
	public void OnMessage(Message msg) {
		System.out.println("ReceivedMulticast:"+msg.toString());

		// When received a message, that must be B_Multicast-ed message
		// deliver the message directly according to the algorithm 
		B_Deliver(msg);
	}

	@Override
	public String getAppName() {
		return "MultiCaster";
	}


	/**
	 * B_Multicast methods
	 */
	public void B_MultiCast(String groupName, Message message) {
		Log.info("MultiCaster", "B_MC ::"+message+"->"+groupName);
		
		List<String> members = getAllMembersByGroupName(groupName);
		for (String dest:members) {
			String msgType = message.getKind();
			if (!msgType.equals(Message_CO_MultiCast)  
				&& !msgType.equals(Message_R_MultiCast))
				msgType = Message_B_MultiCast;
			MultiCastTimestampedMessage MCMessage = 
					new MultiCastTimestampedMessage(message,dest,groupName,msgType);
			Log.info("MultiCaster", "B_MC :"+MCMessage+" => "+dest);
			MessagePasser.send(MCMessage);
		}
	}
	
	
	void B_Deliver(Message msg) {
		Log.info("MultiCaster","B_Deliver: "+msg);
		
		// Using B_Multicast, deliver to caller app (use sysout instead)
		if (msg.getKind().equals(Message_B_MultiCast)) {
			System.out.println("Multicast Deliver!  {B_Deliver}"+msg);
		}
		
		// Using R_Multicast, call further methods
		if (msg.getKind().equals(Message_R_MultiCast)) {
			if (msg instanceof MultiCastTimestampedMessage) {
				MultiCastTimestampedMessage MCMessage = (MultiCastTimestampedMessage) msg;
//						new MultiCastTimestampedMessage(msg,localName,
//								((MultiCastTimestampedMessage) msg).getGroupName(), Message_R_MultiCast);
				if (!receivedMsg.contains(MCMessage)) {
					receivedMsg.add(MCMessage);
					if (!this.localName.equals(MCMessage.getOriginSrc())) {
						B_MultiCast(MCMessage.getGroupName(), MCMessage);
					}
					R_Deliver(MCMessage);
				}
			} else {
				Log.error("MultiCaster", "message type error!");
			}
			
		} 		
		
		// Using CO_Multicast, call further methods
		if (msg.getKind().equals(Message_CO_MultiCast)) {
			CO_ReceiveHelper(msg);
		}
	}
	
	/**
	 * R_Multicast methods
	 */
	public void R_MultiCast(String groupName, Message message) {
		Log.info("MultiCaster", "R_MC :"+message.getData()+"->"+groupName);
		
		TimeStamp timeStamp = ClockServiceFactory.getClockService().issueTimeStamp();
		TimestampedMessage newMessage = new TimestampedMessage("", Message_R_MultiCast, message.getData());
		
		Log.info("MultiCaster", "R_MC call B_MC: "+newMessage+" -> "+groupName);
		
		B_MultiCast(groupName, newMessage);
	}

	void R_Deliver(Message msg) {
		Log.info("MultiCaster","R_Deliver: "+msg);

		// Using R_Multicast, deliver to caller app
		if (msg.getKind().equals(Message_R_MultiCast)) { 
			System.out.println("Multicast Deliver! {R_Deliver}"+msg);
		}		
		// Using CO_Multicast, call further methods
		if (msg.getKind().equals(Message_CO_MultiCast)) {
			CO_ReceiveHelper(msg);
		}
		
	}

	/**
	 * CO_Multicast methods
	 */
	void CO_MultiCast(String groupName, Message message) {
		int groupIndex = getIndexByGroupName(groupName);
		if (groupIndex != -1){
			String myName = MessagePasser.getLocalName();
			int myIndex = groups.get(groupIndex).getIndexByName(myName);
		} else {
			Log.error("MultiCaster", "Group not exist!");
		}
	}

	/**
	 * When B_Deliver/R_Deliver, call helper that will place 
	 * the message into queue
	 * @param message multicast-ed message
	 */
	void CO_ReceiveHelper(Message message) {
		if (message instanceof MultiCastTimestampedMessage){
			holdbackQueue.add((MultiCastTimestampedMessage)message);
		} else {
			Log.error("MultiCaster", "CO_ReceiveHelper : "
				+ "received non-multicast message");
		}
	}
	
	/**
	 * Check check all message in queue, deliver if meet criteria
	 */
	void CO_CheckDeliver(){
		for (MultiCastTimestampedMessage message : holdbackQueue){
			String groupName = message.getGroupName();
			String origSrc = message.getOriginSrc();

			// find info of group g and Vector V 
			int groupIndex = getIndexByGroupName(groupName);
			MultiCastGroup group = groups.get(groupIndex);
			VectorTimeStamp groupTimestamp = groupsTimestamp.get(groupIndex);
			
			// Sender timestamp			
			if (!(message.getTimeStamp() instanceof VectorTimeStamp)){
				Log.error("MultiCaster", "CO_CheckDeliver : "
					+ "received non-vector timestamp in multicast-ed message");
				continue;
			}			
			VectorTimeStamp senderTimestamp = 
					(VectorTimeStamp)message.getTimeStamp();
			if (groupTimestamp.getSize()!=senderTimestamp.getSize()){
				Log.error("MultiCaster", "CO_CheckDeliver : "
						+ "received non-equal-sized vectortimestamp");
				continue;
			}
			
			boolean deliver = true;
			
			for (int i = 0; i < senderTimestamp.getSize();i++){
				// TODO: add logic
				deliver = false;
			}
			
			if (deliver){
				CO_Deliver(message);
				int origIndex = group.getIndexByName(origSrc); // index of j
				groupTimestamp.incrementVectorItem(origIndex); // Vi_g[j]++
			}
		}
	}
	
	void CO_Deliver(Message message) {
		System.out.println("Multicast Deliver!  {CO_Deliver}"+message);
	}

}
