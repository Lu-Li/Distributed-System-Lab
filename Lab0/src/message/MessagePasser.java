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
	private static Map<String,Integer> seqNumberMap = new HashMap<String, Integer>();
	// Received non-delay messages
	private static int countNonDelay = 0;
	// Queue for receiving
	private static Queue<Message> receiveQueue = new LinkedList<Message>();
	// Queue for sending
	private static Queue<Message> delayQueue = new LinkedList<Message>();

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
		if (configuration_filename != null)
			config = new Configuration(configuration_filename);
		else
			config = new Configuration();
		src = local_name;
		
		Integer port = config.getPortNumberForName(local_name);
		new Listener(port).start();
		new Sender().start();
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
		System.err.println("[MsgPasser] receive: "+countNonDelay+"/"+receiveQueue.size());
		// if no message or  all are delayed message, return null
		if (countNonDelay == 0){
			return null;
		} else {
			// get a message from the messageQueue
			Message message = receiveQueue.poll();
			Action action = config.getAction(message, Direction.Receive);
			if (action == Action.NoAction)
				countNonDelay--;
			return message;
		}
	}

	// MARK: Communication level API
	// ==============================================================

	public static void getMessageFromSocketCallback(Message message){
		Action action = config.getAction(message, Direction.Receive);
		switch (action) {
		case NoAction:
			countNonDelay ++ ;
			System.err.println("[MsgPasser] Callback - NoAction "+countNonDelay+"/"+receiveQueue.size()+1);
		case Delay:
			receiveQueue.add(message);
			System.err.println("[MsgPasser] Callback - Delay "+countNonDelay+"/"+receiveQueue.size());
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
}
