package message;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import message.Configuration.*;
import socket.Listener;
import socket.Sender;

public class MessagePasser {
	// Local name
	private static String src = null;
	// Configuration Object. 
	private static Configuration config = null;
	
	// Destination -> SequenceID
	private static  Map<String,Integer> seqNumberMap = new HashMap<String, Integer>();
	// Queue for receiving
	private static  Queue<Message> receiveQueue = new LinkedList<Message>();
	// Queue for sending
	private static  Queue<Message> delayQueue = new LinkedList<Message>();

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
	public static  String getIpStringForName(String name){
		return config.getIpStringForName(name);
	}
	
	
	/**
	 * @param name of the process
	 * @return String representation of the process's port, null if not found
	 */
	public static String getPortStringForName(String name){
		return config.getPortStringForName(name);
	}
	
	// MARK: Application level API	
	// ==============================================================
	
	public static void init(String configuration_filename, String local_name) {
		if (configuration_filename != null)
			config = new Configuration(configuration_filename);
		else
			config = new Configuration();
		src = local_name;
		
		String port = config.getPortStringForName(local_name);
		new Listener(Integer.parseInt(port)).run();
		new Sender().run();
	}

	public static void send(Message message) {
		// get sequence number from hashmap
		Integer seqNumber = seqNumberMap.get(src);
		int seq = 0;
		if (seqNumber == null)
			seqNumber = new Integer(0);
		else 
			seq = seqNumber++;
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
			break;
		case Delay:
			delayQueue.add(message);
		case Drop:
		case DropAfter:
			break;
		}
	}

	public static Message receive() {	
		// get a message from the messageQueue
		if (receiveQueue.isEmpty()) {
			return null;
		} else {
			return receiveQueue.poll();
		}
	}

	// MARK: Communication level API
	// ==============================================================

	public static void getMessageFromSocketCallback(Message message){
		receiveQueue.add(message);		
	}
	
	public static void sendMessageBySocket(Message message){		
		Sender.addMsg(message);
	}
}
