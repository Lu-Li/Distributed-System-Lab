package lab0;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import edu.cmu.ds.lab0.Sender;
import lab0.Configuration.Action;
import lab0.Configuration.Direction;

public class MessagePasser {
	// Local name
	private String src = null;
	// Configuration Object. 
	private Configuration config = null;
	
	// Destination -> SequenceID
	private Map<String,Integer> seqNumberMap = new HashMap<String, Integer>();
	// Queue for receiving
	private Queue<Message> receiveQueue = new LinkedList<Message>();
	// Queue for sending
	private Queue<Message> delayQueue = new LinkedList<Message>();


	// MARK: Application level API	
	// ==============================================================
	
	public MessagePasser(String configuration_filename, String local_name) {
		if (configuration_filename != null)
			config = new Configuration(configuration_filename);
		else
			config = new Configuration();
		src = local_name;
	}

	public void send(Message message) {
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

	public Message receive() {	
		// get a message from the messageQueue
		if (receiveQueue.isEmpty()) {
			return null;
		} else {
			return receiveQueue.poll();
		}
	}

	// MARK: Communication level API
	// ==============================================================

	public void getMessageFromSocketCallback(Message message){
		// TODO(Lu): Call this when socket received a message
		receiveQueue.add(message);		
	}
	
	public void sendMessageBySocket(Message message){		
		// TODO(Lu): Send the message via socket (This should be put in another class)
		Sender.addMsg(msg);
	}
}
