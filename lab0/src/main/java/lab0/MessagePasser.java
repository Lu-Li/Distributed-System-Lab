package lab0;

import java.util.concurrent.atomic.AtomicInteger;

import lab0.Configuration.Action;
import lab0.Configuration.Direction;

public class MessagePasser {
	String src = null;
	Configuration config = null;
	AtomicInteger seqNumber = new AtomicInteger(0);
	
	public MessagePasser(String configuration_filename, String local_name){
		if (configuration_filename!=null)
			config = new Configuration(configuration_filename);
		else 
			config = new Configuration();
		src = local_name;
	}
	
	void send(Message message){
		int seq = seqNumber.incrementAndGet();
		message.set_seqNum(seq);
		message.set_source(src);
		Action action = config.getAction(message, Direction.Send);
		// TODO: Send/Drop/Delay the message
	}
	
	Message receive(){
		// TODO: Receive the message
		return null;
	}
}
