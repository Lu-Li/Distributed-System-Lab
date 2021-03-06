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
	public List<MultiCastGroup> groups = new ArrayList<MultiCastGroup>();

	// CO multicast: V_i for each group g
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
		Log.info("MultiCaster", "ReceivedMulticast:" + msg.toString());

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
//		Log.info("MultiCaster", "B_MC ::" + message + "->" + groupName);

		int groupIndex = getIndexByGroupName(groupName);
		MultiCastGroup myGroup = groups.get(groupIndex);
		if (groupIndex != -1) {
			String myName = MessagePasser.getLocalName();
			if (myGroup.getIndexByName(myName) != -1) {
				List<String> members = getAllMembersByGroupName(groupName);
				for (String dest : members) {
					String msgType = message.getKind();
										
					if (!msgType.equals(Message_CO_MultiCast) && !msgType.equals(Message_R_MultiCast))
						msgType = Message_B_MultiCast;
					MultiCastTimestampedMessage MCMessage = new MultiCastTimestampedMessage(message, dest, groupName,
							msgType);
					
					if(msgType.equals(Message_CO_MultiCast) && dest.equals(myName)  && message.getSrc() == null){
						Log.verbose("MultiCaster", "B_MultiCast self-deliver CO_MC directly");						
						CO_Deliver(MCMessage);						
						continue;
					}
					
//					Log.info("MultiCaster", "B_MC :" + MCMessage + " => " + dest);
					MessagePasser.send(MCMessage);
				}
			} else {
				Log.error("MultiCaster", "I'm not in that group!");
			}
		} else {
			Log.error("MultiCaster", "Group not exist!");
		}
	}

	void B_Deliver(Message msg) {
		//Log.info("MultiCaster", "B_Deliver: " + msg);

		// Using B_Multicast, deliver to caller app (use sysout instead)
		if (msg.getKind().equals(Message_B_MultiCast)) {
			System.out.println("Multicast Deliver!  {B_Deliver}" + msg);
		}

		// Using R_Multicast, call further methods
		if (msg.getKind().equals(Message_R_MultiCast) || msg.getKind().equals(Message_CO_MultiCast)) {
			if (msg instanceof MultiCastTimestampedMessage) {
				MultiCastTimestampedMessage MCMessage = (MultiCastTimestampedMessage) msg;
				// new MultiCastTimestampedMessage(msg,localName,
				// ((MultiCastTimestampedMessage) msg).getGroupName(),
				// Message_R_MultiCast);
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
	}

	/**
	 * R_Multicast methods
	 */
	public void R_MultiCast(String groupName, Message message) {
		Log.verbose2("MultiCaster", "R_MC :" + message.getData() + "->" + groupName);

		int groupIndex = getIndexByGroupName(groupName);
		MultiCastGroup myGroup = groups.get(groupIndex);
		if (groupIndex != -1) {
			String myName = MessagePasser.getLocalName();
			if (myGroup.getIndexByName(myName) != -1) {
				// NOTE: in case race condition
				Message newMessage = null;
				if (message.getKind().equals(Message_CO_MultiCast)){
					newMessage = message;
				} else {
					TimeStamp timeStamp = ClockServiceFactory.getClockService().issueTimeStamp();
					newMessage = new TimestampedMessage("", Message_R_MultiCast, message.getData(),timeStamp);
				}

				//Log.info("MultiCaster", "R_MC call B_MC: " + newMessage + " -> " + groupName);

				B_MultiCast(groupName, newMessage);
			} else {
				Log.error("MultiCaster", "I'm not in that group!");
			}
		} else {
			Log.error("MultiCaster", "Group not exist!");
		}
	}

	void R_Deliver(Message msg) {
		Log.info("MultiCaster", "R_Deliver: " + msg);

		// Using R_Multicast, deliver to caller app
		if (msg.getKind().equals(Message_R_MultiCast)) {
			//used in multicast
			if (Locker.isEnabled()){
				if (msg.getData() instanceof String){
					String src = "";
					String content = (String) msg.getData();
					if (msg instanceof MultiCastTimestampedMessage)
						src = ((MultiCastTimestampedMessage) msg).getOriginSrc();
					else
						src = msg.getSrc();
					Locker.onEvent(content, src);
				}
			} else {
				System.out.println("Multicast Deliver!  {R_Deliver}" + msg);
			}
		}
		// Using CO_Multicast, call further methods
		if (msg.getKind().equals(Message_CO_MultiCast)) {
			CO_ReceiveHelper(msg);
		}

	}

	/**
	 * CO_Multicast methods
	 */
	public void CO_MultiCast(String groupName, Message message) {
		int groupIndex = getIndexByGroupName(groupName);
		MultiCastGroup myGroup = groups.get(groupIndex);

		// if groupname valid, get Vi[i]
		if (groupIndex != -1) {
			String myName = MessagePasser.getLocalName();
			VectorTimeStamp myGroupTimestamp = myGroup.getTimestampByName(myName);

			// if I am in the group, Vi[i]++
			if (myGroupTimestamp != null) {
				int myIndex = myGroup.getIndexByName(myName);
				myGroupTimestamp.incrementVectorItem(myIndex);

				// set timestamp as Vi_g
				TimestampedMessage newMessage = new TimestampedMessage("", Message_CO_MultiCast, message.getData());
				
				newMessage.setTimeStamp(new VectorTimeStamp(myGroupTimestamp));

				Log.info("MultiCaster", "CO_MC -> B_MC | " + "myGroupTimestamp:" + myGroupTimestamp);
				R_MultiCast(groupName, newMessage);
			} else {
				Log.error("MultiCaster", "I'm not in that group!");
			}

		} else {
			Log.error("MultiCaster", "Group not exist!");
		}
	}

	/**
	 * When B_Deliver/R_Deliver, call helper that will place the message into
	 * queue
	 * 
	 * @param message
	 *            multicast-ed message
	 */
	void CO_ReceiveHelper(Message message) {
		if (message instanceof MultiCastTimestampedMessage) {
			Log.verbose("MultiCaster", "CO_ReceiveHelper");
			holdbackQueue.add((MultiCastTimestampedMessage) message);
			CO_CheckDeliver();		
		} else {
			Log.error("MultiCaster", "CO_ReceiveHelper : " + "received non-multicast message");
		}
	}

	/**
	 * Check check all message in queue, deliver if meet criteria
	 */
	void CO_CheckDeliver() {
		Log.verbose("MultiCaster", "CO_CheckDeliver");

		int index = 0;
		boolean finished = true;
		// for (MultiCastTimestampedMessage message : holdbackQueue) {
		while (index < holdbackQueue.size()) {
			MultiCastTimestampedMessage message = holdbackQueue.get(index);

			String sendergroupName = message.getGroupName();
			String senderName = message.getOriginSrc();

			// find info of group g
			int groupIndex = getIndexByGroupName(sendergroupName);
			MultiCastGroup group = groups.get(groupIndex);

			// find info of Vector Vi
			String myName = MessagePasser.getLocalName();
			VectorTimeStamp myTimestamp = group.getTimestampByName(myName);

			// Sender timestamp
			if (!(message.getOriginTimestamp() instanceof VectorTimeStamp)) {
				Log.error("MultiCaster",
						"CO_CheckDeliver : " + "received non-vector timestamp in multicast-ed message");
				index++;
				continue;
			}

			// check if both timestamp are valid
			VectorTimeStamp senderTimestamp = (VectorTimeStamp)message.getOriginTimestamp();
			if (myTimestamp.getSize() != senderTimestamp.getSize()) {
				Log.error("MultiCaster", "CO_CheckDeliver : " + "received non-equal-sized vectortimestamp. senderTS:"
						+ senderTimestamp + " myTS:" + myTimestamp);
				index++;
				continue;
			}

			boolean deliver = true;
			int senderIndex = group.getIndexByName(senderName);
			for (int k = 0; k < senderTimestamp.getSize(); k++) {
				// Vj[j] = vi[j]+1
				if (k == senderIndex) {
					if (senderTimestamp.getVectorItem(senderIndex) != myTimestamp.getVectorItem(senderIndex) + 1) {
						deliver = false;
						break;
					}
					// vj[k]<=vi[k]
				} else {
					if (senderTimestamp.getVectorItem(k) > myTimestamp.getVectorItem(k)) {
						deliver = false;
						break;
					}
				}
			}

			if (deliver) {
				finished = false;
				CO_Deliver(message);
				myTimestamp.incrementVectorItem(senderIndex); // Vi_g[j]++
				Log.verbose("MultiCaster", "Vi_g[j]++ : "+" j="+senderIndex + " Vi="+myTimestamp);
				holdbackQueue.remove(index);
			} else {
				index++;
			}
		}
		
		// Changed, do again
		if (!finished)
			CO_CheckDeliver();
	}

	void CO_Deliver(Message message) {
		System.out.println("Multicast Deliver!  {CO_Deliver}" + message);
	}

}
