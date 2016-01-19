package lab0;

import lab0.Configuration.Action;
import lab0.Configuration.Direction;

public class Driver {	
	public static void main(String[] args) {
		// Test
		Configuration config = new Configuration();
		Message message = new Message("bob", "ACK", null);
		message.set_seqNum(0);
		message.set_source("charlie");
		Action action = config.getAction(message, Direction.Receive);
		System.out.println(action);
	}
}
