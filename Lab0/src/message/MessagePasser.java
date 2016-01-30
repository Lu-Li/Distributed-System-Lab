package message;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import message.Configuration.*;
import socket.Listener;
import socket.Receiver;
import socket.Sender;
import socket.SessionMap;

public class MessagePasser {
	// Local name
	private static String src = null;
	// Configuration Object. 
	private static String configurationFileName;
	private static Configuration config = null;
	
	// Destination -> SequenceID
	private static Map<String,Integer> seqNumberMap = new HashMap<String, Integer>();
	// Queue for receiving
	private static Queue<Message> receiveQueue = new LinkedList<Message>();
	private static Queue<Message> delayReceiveQueue = new LinkedList<Message>();
	// Queue for sending
	private static Queue<Message> delayQueue = new LinkedList<Message>();

	
	//Socket
	private static Listener listener;
	private static Sender sender;
	
	// MARK: Configuration API
	// ==============================================================

	public static String getLocalName(){
		return src;
	}
	
	public static String getLocalPort(){
		return src;
	}
	
	/**
	 * @return a list of names defined in the network configuration file
	 */
	public static  List<String> getAllNames(){
		return config.getAllNames();
	}
	
	/**
	 * @param name of the process
	 * @return String representation of the process's ip, null if not found
	 */
	public static String getIpStringForName(String name){
		return config.getIpStringForName(name);
	}
		
	/**
	 * @param name of the process
	 * @return Integer representation of the process's port, null if not found
	 */
	public static Integer getPortNumberForName(String name){
		return config.getPortNumberForName(name);
	}
	
	// MARK: Application level API	
	// ==============================================================
	
	public static void init(String configuration_filename, String local_name) {
		configurationFileName = configuration_filename;
		if (configurationFileName != null)
			config = new Configuration(configurationFileName);
		else
			config = new Configuration();
		src = local_name;
		
		Integer port = config.getPortNumberForName(local_name);
		
		listener = new Listener(port);
		listener.start();
		
		sender = new Sender();
		sender.start();
	}

	public static void send(Message message) {
		// re-read configuration file
		if (configurationFileName != null)
			config = new Configuration(configurationFileName);
		else
			config = new Configuration();

		
		// get sequence number from hashmap
		Integer seqNumber = seqNumberMap.get(src);
		int seq = 0;
		if (seqNumber == null)
			seqNumber = new Integer(0);
		else 
			seq = ++seqNumber;
		seqNumberMap.put(src, seqNumber);
		
		// set message parameters
		message.set_seqNum(seq);
		message.set_source(src);
		
		// find corresponding action
		Action action = config.getAction(message, Direction.Send);
		switch (action) {
		case NoAction:
			while (!delayQueue.isEmpty()){
				Message delayedMessage = delayQueue.poll();
				sendMessageBySocket(delayedMessage);
			}
			sendMessageBySocket(message);
			System.err.println("[MsgPasser] Send - NoAction");
			break;
		case Delay:
			delayQueue.add(message);
			System.err.println("[MsgPasser] Send - Delay");
			break;
		case Drop:
			System.err.println("[MsgPasser] Send - Drop");
			break;
		case DropAfter:
			System.err.println("[MsgPasser] Send - DropAfter");
			break;
		}
	}

	public static Message receive() {
		System.err.println("[MsgPasser] receive: "+receiveQueue.size()+"/"+delayReceiveQueue.size());
		// if no message or  all are delayed message, return null
		if (receiveQueue.isEmpty()){
			return null;
		} else {
			// get a message from the messageQueue			
			return receiveQueue.poll();
		}
	}

	// MARK: Communication level API
	// ==============================================================

	public static void getMessageFromSocketCallback(Message message){
		// re-read configuration file
		if (configurationFileName != null)
			config = new Configuration(configurationFileName);
		else
			config = new Configuration();

		// find corresponding action
		Action action = config.getAction(message, Direction.Receive);
		switch (action) {
		case NoAction:
			receiveQueue.add(message);
			while (!delayReceiveQueue.isEmpty()){
				Message delayMessage = delayReceiveQueue.poll();
				receiveQueue.add(delayMessage);
			}
			System.err.println("[MsgPasser] Callback - NoAction " + receiveQueue.size()+"/"+delayReceiveQueue.size());
			break;
		case Delay:
			delayReceiveQueue.add(message);
			System.err.println("[MsgPasser] Callback - Delay " + receiveQueue.size()+"/"+delayReceiveQueue.size());
			break;
		case Drop:
			System.err.println("[MsgPasser] Callback - Drop");
			break;
		case DropAfter:
			System.err.println("[MsgPasser] Callback - DropAfter");
			break;
		}		
	}
	
	public static void sendMessageBySocket(Message message){		
		Sender.addMsg(message);
	}
	
	public static void terminateAll() {
		Listener.setFlagFalse();
		Sender.setFlagFalse();
		Receiver.setFlagFalse();
	}
}
